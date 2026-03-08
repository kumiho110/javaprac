package javaprac.dao;

import javaprac.model.Manufacturer;
import javaprac.model.Product;
import javaprac.model.ProductAttributeValue;
import javaprac.model.ProductType;
import javaprac.model.ProductTypeAttribute;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

public class ProductAttributeValueDaoTestNG extends AbstractEntityManagerTest {

    private ProductTypeDaoHibernate productTypeDao;
    private ManufacturerDaoHibernate manufacturerDao;
    private ProductDaoHibernate productDao;
    private ProductTypeAttributeDaoHibernate productTypeAttributeDao;
    private ProductAttributeValueDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        productTypeDao = new ProductTypeDaoHibernate(em);
        manufacturerDao = new ManufacturerDaoHibernate(em);
        productDao = new ProductDaoHibernate(em);
        productTypeAttributeDao = new ProductTypeAttributeDaoHibernate(em);
        dao = new ProductAttributeValueDaoHibernate(em);
    }

    @Test
    public void productAttributeValueDaoMustSaveFindAndListByProduct() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lenovo");
        manufacturer.setAssemblyCountry("China");
        manufacturerDao.save(manufacturer);

        Product product = new Product();
        product.setType(type);
        product.setManufacturer(manufacturer);
        product.setName("IdeaPad 5");
        product.setPrice(new BigDecimal("79990"));
        product.setStockQty(5);
        productDao.save(product);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Оперативная память");
        attr.setSortOrder(1);
        productTypeAttributeDao.save(attr);

        ProductAttributeValue value = new ProductAttributeValue();
        value.setProduct(product);
        value.setAttribute(attr);
        value.setValue("16 GB");
        dao.save(value);
        flushAndClear();

        ProductAttributeValue persisted = dao.findById(value.getId()).orElseThrow();
        Assert.assertEquals(persisted.getValue(), "16 GB");
        Assert.assertEquals(persisted.getProduct().getId(), product.getId());
        Assert.assertEquals(persisted.getAttribute().getId(), attr.getId());

        List<ProductAttributeValue> values = dao.findByProductId(product.getId());
        Assert.assertEquals(values.size(), 1);
        Assert.assertEquals(values.get(0).getId(), value.getId());
    }

    @Test
    public void findByProductIdMustReturnOnlyValuesOfRequestedProduct() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lenovo");
        manufacturer.setAssemblyCountry("China");
        manufacturerDao.save(manufacturer);

        Product product1 = new Product();
        product1.setType(type);
        product1.setManufacturer(manufacturer);
        product1.setName("IdeaPad 5");
        product1.setPrice(new BigDecimal("79990"));
        product1.setStockQty(5);
        productDao.save(product1);

        Product product2 = new Product();
        product2.setType(type);
        product2.setManufacturer(manufacturer);
        product2.setName("ThinkBook 14");
        product2.setPrice(new BigDecimal("99990"));
        product2.setStockQty(3);
        productDao.save(product2);

        ProductTypeAttribute attr1 = new ProductTypeAttribute();
        attr1.setProductType(type);
        attr1.setName("Оперативная память");
        attr1.setSortOrder(1);
        productTypeAttributeDao.save(attr1);

        ProductTypeAttribute attr2 = new ProductTypeAttribute();
        attr2.setProductType(type);
        attr2.setName("Объем SSD");
        attr2.setSortOrder(2);
        productTypeAttributeDao.save(attr2);

        ProductAttributeValue v1 = new ProductAttributeValue();
        v1.setProduct(product1);
        v1.setAttribute(attr1);
        v1.setValue("16 GB");
        dao.save(v1);

        ProductAttributeValue v2 = new ProductAttributeValue();
        v2.setProduct(product1);
        v2.setAttribute(attr2);
        v2.setValue("512 GB");
        dao.save(v2);

        ProductAttributeValue v3 = new ProductAttributeValue();
        v3.setProduct(product2);
        v3.setAttribute(attr1);
        v3.setValue("8 GB");
        dao.save(v3);

        flushAndClear();

        List<ProductAttributeValue> values = dao.findByProductId(product1.getId());
        Assert.assertEquals(values.size(), 2);
        Assert.assertTrue(values.stream().allMatch(v -> v.getProduct().getId().equals(product1.getId())));
    }

    @Test
    public void findByProductIdMustReturnEmptyForUnknownProduct() {
        Assert.assertTrue(dao.findByProductId(-1L).isEmpty());
    }

    @Test
    public void existsByAttributeIdMustReturnTrueWhenAtLeastOneValueUsesAttribute() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lenovo");
        manufacturer.setAssemblyCountry("China");
        manufacturerDao.save(manufacturer);

        Product product = new Product();
        product.setType(type);
        product.setManufacturer(manufacturer);
        product.setName("IdeaPad 5");
        product.setPrice(new BigDecimal("79990"));
        product.setStockQty(5);
        productDao.save(product);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Оперативная память");
        attr.setSortOrder(1);
        productTypeAttributeDao.save(attr);

        ProductAttributeValue value = new ProductAttributeValue();
        value.setProduct(product);
        value.setAttribute(attr);
        value.setValue("16 GB");
        dao.save(value);
        flushAndClear();

        Assert.assertTrue(dao.existsByAttributeId(attr.getId()));
    }

    @Test
    public void existsByAttributeIdMustReturnFalseWhenAttributeIsUnused() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Оперативная память");
        attr.setSortOrder(1);
        productTypeAttributeDao.save(attr);
        flushAndClear();

        Assert.assertFalse(dao.existsByAttributeId(attr.getId()));
    }

    @Test
    public void existsByAttributeIdMustReturnFalseForUnknownAttribute() {
        Assert.assertFalse(dao.existsByAttributeId(-1L));
    }

    @Test
    public void deleteByProductIdMustDeleteOnlyValuesOfRequestedProduct() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lenovo");
        manufacturer.setAssemblyCountry("China");
        manufacturerDao.save(manufacturer);

        Product product1 = new Product();
        product1.setType(type);
        product1.setManufacturer(manufacturer);
        product1.setName("IdeaPad 5");
        product1.setPrice(new BigDecimal("79990"));
        product1.setStockQty(5);
        productDao.save(product1);

        Product product2 = new Product();
        product2.setType(type);
        product2.setManufacturer(manufacturer);
        product2.setName("ThinkBook 14");
        product2.setPrice(new BigDecimal("99990"));
        product2.setStockQty(3);
        productDao.save(product2);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Оперативная память");
        attr.setSortOrder(1);
        productTypeAttributeDao.save(attr);

        ProductAttributeValue v1 = new ProductAttributeValue();
        v1.setProduct(product1);
        v1.setAttribute(attr);
        v1.setValue("16 GB");
        dao.save(v1);

        ProductAttributeValue v2 = new ProductAttributeValue();
        v2.setProduct(product2);
        v2.setAttribute(attr);
        v2.setValue("8 GB");
        dao.save(v2);
        flushAndClear();

        dao.deleteByProductId(product1.getId());
        flushAndClear();

        Assert.assertTrue(dao.findByProductId(product1.getId()).isEmpty());
        Assert.assertEquals(dao.findByProductId(product2.getId()).size(), 1);
    }
}