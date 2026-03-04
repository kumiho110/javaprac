package javaprac.dao;

import javaprac.model.Manufacturer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ManufacturerDaoTestNG extends AbstractEntityManagerTest {

    private ManufacturerDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new ManufacturerDaoHibernate(em);
    }

    @Test
    public void manufacturerDaoMustSupportCrudOperations() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Samsung");
        manufacturer.setAssemblyCountry("Vietnam");
        dao.save(manufacturer);
        flushAndClear();

        Manufacturer persisted = dao.findById(manufacturer.getId()).orElseThrow();
        Assert.assertEquals(persisted.getName(), "Samsung");
        Assert.assertEquals(persisted.getAssemblyCountry(), "Vietnam");

        List<Manufacturer> all = dao.findAll();
        Assert.assertEquals(all.size(), 1);
        Assert.assertEquals(all.get(0).getId(), manufacturer.getId());

        dao.delete(persisted);
        flushAndClear();
        Assert.assertTrue(dao.findById(manufacturer.getId()).isEmpty());
    }
}
