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
        Assert.assertEquals(all.size(), 1);
        Assert.assertEquals(all.get(0).getId(), type.getId());
    }
}
