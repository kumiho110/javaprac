package javaprac.dao;

import javaprac.model.Manufacturer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

public class CommonDaoHibernateTestNG extends AbstractEntityManagerTest {

    private ManufacturerDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new ManufacturerDaoHibernate(em);
    }

    @Test
    public void saveUpdateFindDeleteByIdAndFindAllMustWork() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("CommonDao manufacturer");
        manufacturer.setAssemblyCountry("CommonDao country");

        Manufacturer saved = dao.save(manufacturer);
        Assert.assertSame(saved, manufacturer);
        Assert.assertNotNull(manufacturer.getId());

        flushAndClear();
        Optional<Manufacturer> persisted = dao.findById(manufacturer.getId());
        Assert.assertTrue(persisted.isPresent());
        Assert.assertEquals(persisted.get().getName(), "CommonDao manufacturer");
        Assert.assertEquals(persisted.get().getAssemblyCountry(), "CommonDao country");

        Manufacturer detached = persisted.get();
        detached.setName("CommonDao manufacturer updated");
        Manufacturer merged = dao.update(detached);
        Assert.assertEquals(merged.getName(), "CommonDao manufacturer updated");

        flushAndClear();
        Optional<Manufacturer> updated = dao.findById(manufacturer.getId());
        Assert.assertTrue(updated.isPresent());
        Assert.assertEquals(updated.get().getName(), "CommonDao manufacturer updated");

        List<Manufacturer> all = dao.findAll();
        Assert.assertEquals(all.size(), 1);
        Assert.assertEquals(all.get(0).getId(), manufacturer.getId());

        dao.deleteById(manufacturer.getId());
        flushAndClear();
        Assert.assertTrue(dao.findById(manufacturer.getId()).isEmpty());

        dao.deleteById(999999L);
    }

    @Test
    public void deleteByEntityMustRemoveManagedEntity() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Delete entity manufacturer");
        manufacturer.setAssemblyCountry("Delete entity country");
        dao.save(manufacturer);

        flushAndClear();
        Manufacturer managed = dao.findById(manufacturer.getId()).orElseThrow();
        dao.delete(managed);
        flushAndClear();

        Assert.assertTrue(dao.findById(manufacturer.getId()).isEmpty());
    }
}
