package javaprac.dao;

import javaprac.model.ProductType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductTypeDaoTestNG extends AbstractEntityManagerTest {

    private ProductTypeDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new ProductTypeDaoHibernate(em);
    }

    @Test
    public void productTypeDaoMustSaveFindAndListEntities() {
        ProductType type = new ProductType();
        type.setName("Телевизоры");
        dao.save(type);
        flushAndClear();

        ProductType persisted = dao.findById(type.getId()).orElseThrow();
        Assert.assertEquals(persisted.getName(), "Телевизоры");

        List<ProductType> all = dao.findAll();
        Assert.assertTrue(all.stream().anyMatch(it -> it.getId().equals(type.getId())));
    }

    @Test
    public void findByNameIgnoreCaseMustReturnMatchingType() {
        ProductType type = new ProductType();
        type.setName("Ноутбуки");
        dao.save(type);
        flushAndClear();

        ProductType found = dao.findByNameIgnoreCase("нОуТбУкИ").orElseThrow();
        Assert.assertEquals(found.getId(), type.getId());
        Assert.assertEquals(found.getName(), "Ноутбуки");
    }

    @Test
    public void findByNameIgnoreCaseMustReturnEmptyForUnknownName() {
        Assert.assertTrue(dao.findByNameIgnoreCase("Неизвестный тип").isEmpty());
    }

    @Test
    public void findByNameIgnoreCaseMustReturnEmptyForNull() {
        Assert.assertTrue(dao.findByNameIgnoreCase(null).isEmpty());
    }
}