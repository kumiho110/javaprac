package javaprac.dao;

import javaprac.model.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDaoTestNG extends AbstractEntityManagerTest {

    private ProductDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new ProductDaoHibernate(em);
    }

    @Test
    public void productCrudAndLifecycleCallbacksMustWork() throws Exception {
        ProductType type = persistProductType("TV");
        Manufacturer manufacturer = persistManufacturer("Samsung");

        Product product = new Product();
        product.setType(type);
        product.setManufacturer(manufacturer);
        product.setName("Neo QLED");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("1499.99"));
        product.setStockQty(6);
        dao.save(product);
        flushAndClear();

        Product persisted = dao.findById(product.getId()).orElseThrow();
        Assert.assertNotNull(persisted.getCreatedAt());
        Assert.assertNotNull(persisted.getUpdatedAt());
        Assert.assertEquals(persisted.getName(), "Neo QLED");

        LocalDateTime firstUpdatedAt = persisted.getUpdatedAt();
        Thread.sleep(20L);
        persisted.setPrice(new BigDecimal("1299.99"));
        dao.update(persisted);
        flushAndClear();

        Product updated = dao.findById(product.getId()).orElseThrow();
        Assert.assertEquals(updated.getPrice(), new BigDecimal("1299.99"));
        Assert.assertNotEquals(updated.getUpdatedAt(), firstUpdatedAt);

        Product locked = dao.findByIdForUpdate(product.getId());
        Assert.assertEquals(locked.getId(), product.getId());

        try {
            dao.findByIdForUpdate(999999L);
            Assert.fail("Expected IllegalArgumentException for unknown product id");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("Product not found"));
        }

        List<Product> all = dao.findAllWithRefs();
        Assert.assertEquals(all.size(), 1);
        Assert.assertNotNull(all.get(0).getType());
        Assert.assertNotNull(all.get(0).getManufacturer());
    }

    @Test
    public void productSearchAndAttributeLookupsMustCoverAllBranches() {
        ProductType tv = persistProductType("TV");
        ProductType fridge = persistProductType("Fridge");
        Manufacturer samsung = persistManufacturer("Samsung");
        Manufacturer lg = persistManufacturer("LG");

        Product tvBlue = persistProduct(tv, samsung, "TV Blue", new BigDecimal("1000.00"), 5);
        Product tvRed = persistProduct(tv, lg, "TV Red", new BigDecimal("900.00"), 4);
        Product fridgeWhite = persistProduct(fridge, lg, "Fridge White", new BigDecimal("800.00"), 3);

        ProductTypeAttribute colorTv = persistAttribute(tv, "Color", 10, true);
        ProductTypeAttribute sizeTv = persistAttribute(tv, "Size", 20, false);
        ProductTypeAttribute colorFridge = persistAttribute(fridge, "Color", 10, true);

        persistAttributeValue(tvBlue, colorTv, "Blue");
        persistAttributeValue(tvBlue, sizeTv, "55");
        persistAttributeValue(tvRed, colorTv, "Red");
        persistAttributeValue(fridgeWhite, colorFridge, "White");

        flushAndClear();

        Assert.assertEquals(dao.findAllWithRefs().size(), 3);

        Product withAttributes = dao.findByIdWithAttributes(tvBlue.getId()).orElseThrow();
        Assert.assertEquals(withAttributes.getAttributeValues().size(), 2);
        Assert.assertTrue(dao.findByIdWithAttributes(999999L).isEmpty());

        Assert.assertEquals(dao.search(null, null, null, null).size(), 3);
        Assert.assertEquals(dao.search(tv.getName(), null, null, null).size(), 2);
        Assert.assertEquals(dao.search(null, lg.getName(), null, null).size(), 2);
        Assert.assertEquals(dao.search(tv.getName(), samsung.getName(), null, null).size(), 1);
        Assert.assertEquals(dao.search(null, null, "Color", "Blue").size(), 1);
        Assert.assertEquals(dao.search(null, null, "Color", null).size(), 3);
        Assert.assertEquals(dao.search(null, null, null, "White").size(), 1);
        Assert.assertEquals(dao.search(tv.getName(), null, "Color", "White").size(), 0);

        List<String> tvAttributeNames = dao.listAttributeNamesByType(tv.getName());
        Assert.assertEquals(tvAttributeNames.size(), 2);
        Assert.assertEquals(tvAttributeNames.get(0), "Color");
        Assert.assertEquals(tvAttributeNames.get(1), "Size");
        Assert.assertTrue(dao.listAttributeNamesByType("Unknown type").isEmpty());

        List<String> colorValues = dao.listAttributeValuesByTypeAndName(tv.getName(), "Color");
        Assert.assertEquals(colorValues.size(), 2);
        Assert.assertEquals(colorValues.get(0), "Blue");
        Assert.assertEquals(colorValues.get(1), "Red");
        Assert.assertTrue(dao.listAttributeValuesByTypeAndName(tv.getName(), "Missing").isEmpty());
    }

    @Test
    public void searchMustTreatBlankParametersAsAbsent() {
        ProductType type = persistProductType("Phone");
        Manufacturer manufacturer = persistManufacturer("Apple");
        persistProduct(type, manufacturer, "iPhone", new BigDecimal("1000.00"), 5);

        flushAndClear();

        List<Product> result = dao.search("   ", "   ", "   ", "   ");

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.get(0).getName().startsWith("iPhone"));
    }

    @Test
    public void searchMustHandleAttributeNameWithoutUsableAttributeValue() {
        ProductType type = persistProductType("Phone");
        Manufacturer manufacturer = persistManufacturer("Apple");

        Product blackPhone = persistProduct(type, manufacturer, "Black phone", new BigDecimal("900.00"), 3);
        Product whitePhone = persistProduct(type, manufacturer, "White phone", new BigDecimal("950.00"), 4);

        ProductTypeAttribute color = persistAttribute(type, "Color", 1, true);

        persistAttributeValue(blackPhone, color, "Black");
        persistAttributeValue(whitePhone, color, "White");

        flushAndClear();

        List<Product> result = dao.search(null, null, "Color", "   ");

        Assert.assertEquals(result.size(), 2);
    }

    @Test
    public void searchMustHandleAttributeValueWithoutUsableAttributeName() {
        ProductType type = persistProductType("Phone");
        Manufacturer manufacturer = persistManufacturer("Apple");

        Product blackPhone = persistProduct(type, manufacturer, "Black phone", new BigDecimal("900.00"), 3);
        Product whitePhone = persistProduct(type, manufacturer, "White phone", new BigDecimal("950.00"), 4);

        ProductTypeAttribute color = persistAttribute(type, "Color", 1, true);
        ProductTypeAttribute memory = persistAttribute(type, "Memory", 2, false);

        persistAttributeValue(blackPhone, color, "Black");
        persistAttributeValue(whitePhone, memory, "128");

        flushAndClear();

        List<Product> result = dao.search(null, null, "   ", "Black");

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.get(0).getName().startsWith("Black phone"));
    }
}
