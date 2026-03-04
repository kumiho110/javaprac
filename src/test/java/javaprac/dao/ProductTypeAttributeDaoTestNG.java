package javaprac.dao;

import javaprac.model.ProductType;
import javaprac.model.ProductTypeAttribute;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductTypeAttributeDaoTestNG extends AbstractEntityManagerTest {

    private ProductTypeAttributeDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new ProductTypeAttributeDaoHibernate(em);
    }

    @Test
    public void findByProductTypeIdMustReturnOnlyRequestedTypeAttributesInStableOrder() {
        ProductType tv = persistProductType("TV");
        ProductType fridge = persistProductType("Fridge");

        ProductTypeAttribute resolution = persistAttribute(tv, "Resolution", 20, true);
        ProductTypeAttribute diagonal = persistAttribute(tv, "Diagonal", 10, true);
        persistAttribute(fridge, "Volume", 5, false);

        flushAndClear();

        List<ProductTypeAttribute> result = dao.findByProductTypeId(tv.getId());
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(0).getId(), diagonal.getId());
        Assert.assertEquals(result.get(1).getId(), resolution.getId());
        Assert.assertTrue(dao.findByProductTypeId(999999L).isEmpty());
    }
}
