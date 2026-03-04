package javaprac.dao;

import javaprac.model.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

public class ProductAttributeValueDaoTestNG extends AbstractEntityManagerTest {

    private ProductAttributeValueDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new ProductAttributeValueDaoHibernate(em);
    }

    @Test
    public void findByProductIdAndDeleteByProductIdMustWork() {
        ProductType type = persistProductType("TV");
        Manufacturer manufacturer = persistManufacturer("Samsung");
        Product product = persistProduct(type, manufacturer, "QLED", new BigDecimal("999.99"), 7);
        Product otherProduct = persistProduct(type, manufacturer, "OLED", new BigDecimal("1199.99"), 5);

        ProductTypeAttribute diagonal = persistAttribute(type, "Diagonal", 10, true);
        ProductTypeAttribute resolution = persistAttribute(type, "Resolution", 20, true);

        ProductAttributeValue v1 = persistAttributeValue(product, resolution, "4K");
        ProductAttributeValue v2 = persistAttributeValue(product, diagonal, "55");
        persistAttributeValue(otherProduct, resolution, "8K");

        flushAndClear();

        List<ProductAttributeValue> result = dao.findByProductId(product.getId());
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(0).getAttribute().getName(), "Diagonal");
        Assert.assertEquals(result.get(0).getValue(), "55");
        Assert.assertEquals(result.get(1).getAttribute().getName(), "Resolution");
        Assert.assertEquals(result.get(1).getValue(), "4K");
        Assert.assertTrue(dao.findByProductId(999999L).isEmpty());

        dao.deleteByProductId(product.getId());
        flushAndClear();

        Assert.assertTrue(dao.findById(v1.getId()).isEmpty());
        Assert.assertTrue(dao.findById(v2.getId()).isEmpty());
        Assert.assertEquals(dao.findByProductId(otherProduct.getId()).size(), 1);
    }
}
