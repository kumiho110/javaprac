package javaprac.dao;

import javaprac.model.ProductType;
import javaprac.model.ProductTypeAttribute;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductTypeAttributeDaoTestNG extends AbstractEntityManagerTest {

    private ProductTypeDaoHibernate productTypeDao;
    private ProductTypeAttributeDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        productTypeDao = new ProductTypeDaoHibernate(em);
        dao = new ProductTypeAttributeDaoHibernate(em);
    }

    @Test
    public void productTypeAttributeDaoMustSaveFindAndListByProductType() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Оперативная память");
        attr.setSortOrder(1);
        dao.save(attr);
        flushAndClear();

        ProductTypeAttribute persisted = dao.findById(attr.getId()).orElseThrow();
        Assert.assertEquals(persisted.getName(), "Оперативная память");
        Assert.assertEquals(persisted.getSortOrder(), Integer.valueOf(1));
        Assert.assertEquals(persisted.getProductType().getId(), type.getId());

        List<ProductTypeAttribute> attrs = dao.findByProductTypeId(type.getId());
        Assert.assertEquals(attrs.size(), 1);
        Assert.assertEquals(attrs.get(0).getId(), attr.getId());
    }

    @Test
    public void findByProductTypeIdMustReturnOnlyAttributesOfRequestedTypeOrderedBySortOrder() {
        ProductType laptops = new ProductType();
        laptops.setName("Ноутбуки");
        productTypeDao.save(laptops);

        ProductType tvs = new ProductType();
        tvs.setName("Телевизоры");
        productTypeDao.save(tvs);

        ProductTypeAttribute attr2 = new ProductTypeAttribute();
        attr2.setProductType(laptops);
        attr2.setName("Диагональ");
        attr2.setSortOrder(2);
        dao.save(attr2);

        ProductTypeAttribute attr1 = new ProductTypeAttribute();
        attr1.setProductType(laptops);
        attr1.setName("Оперативная память");
        attr1.setSortOrder(1);
        dao.save(attr1);

        ProductTypeAttribute other = new ProductTypeAttribute();
        other.setProductType(tvs);
        other.setName("Разрешение");
        other.setSortOrder(1);
        dao.save(other);

        flushAndClear();

        List<ProductTypeAttribute> attrs = dao.findByProductTypeId(laptops.getId());
        Assert.assertEquals(attrs.size(), 2);
        Assert.assertEquals(attrs.get(0).getName(), "Оперативная память");
        Assert.assertEquals(attrs.get(0).getSortOrder(), Integer.valueOf(1));
        Assert.assertEquals(attrs.get(1).getName(), "Диагональ");
        Assert.assertEquals(attrs.get(1).getSortOrder(), Integer.valueOf(2));
    }

    @Test
    public void findByProductTypeIdAndNameIgnoreCaseMustReturnMatchingAttribute() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Объем SSD");
        attr.setSortOrder(1);
        dao.save(attr);
        flushAndClear();

        ProductTypeAttribute found = dao.findByProductTypeIdAndNameIgnoreCase(type.getId(), "оБъЕм sSd").orElseThrow();
        Assert.assertEquals(found.getId(), attr.getId());
        Assert.assertEquals(found.getName(), "Объем SSD");
    }

    @Test
    public void findByProductTypeIdAndNameIgnoreCaseMustReturnEmptyForUnknownNameOrType() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        ProductTypeAttribute attr = new ProductTypeAttribute();
        attr.setProductType(type);
        attr.setName("Объем SSD");
        attr.setSortOrder(1);
        dao.save(attr);
        flushAndClear();

        Assert.assertTrue(dao.findByProductTypeIdAndNameIgnoreCase(type.getId(), "Диагональ").isEmpty());
        Assert.assertTrue(dao.findByProductTypeIdAndNameIgnoreCase(-1L, "Объем SSD").isEmpty());
    }

    @Test
    public void findByProductTypeIdAndNameIgnoreCaseMustReturnEmptyForNullName() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);
        flushAndClear();

        Assert.assertTrue(dao.findByProductTypeIdAndNameIgnoreCase(type.getId(), null).isEmpty());
    }

    @Test
    public void findMaxSortOrderByProductTypeIdMustReturnMaxValue() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);

        ProductTypeAttribute attr1 = new ProductTypeAttribute();
        attr1.setProductType(type);
        attr1.setName("Оперативная память");
        attr1.setSortOrder(1);
        dao.save(attr1);

        ProductTypeAttribute attr2 = new ProductTypeAttribute();
        attr2.setProductType(type);
        attr2.setName("Объем SSD");
        attr2.setSortOrder(5);
        dao.save(attr2);

        ProductTypeAttribute attr3 = new ProductTypeAttribute();
        attr3.setProductType(type);
        attr3.setName("Процессор");
        attr3.setSortOrder(3);
        dao.save(attr3);

        flushAndClear();

        Integer max = dao.findMaxSortOrderByProductTypeId(type.getId());
        Assert.assertEquals(max, Integer.valueOf(5));
    }

    @Test
    public void findMaxSortOrderByProductTypeIdMustReturnZeroWhenNoAttributesExist() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        productTypeDao.save(type);
        flushAndClear();

        Integer max = dao.findMaxSortOrderByProductTypeId(type.getId());
        Assert.assertEquals(max, Integer.valueOf(0));
    }

    @Test
    public void findMaxSortOrderByProductTypeIdMustReturnZeroForUnknownType() {
        Integer max = dao.findMaxSortOrderByProductTypeId(-1L);
        Assert.assertEquals(max, Integer.valueOf(0));
    }
}