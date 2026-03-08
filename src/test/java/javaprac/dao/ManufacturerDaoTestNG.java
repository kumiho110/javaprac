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
    public void manufacturerDaoMustSaveFindAndListEntities() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lenovo");
        manufacturer.setAssemblyCountry("China");
        dao.save(manufacturer);
        flushAndClear();

        Manufacturer persisted = dao.findById(manufacturer.getId()).orElseThrow();
        Assert.assertEquals(persisted.getName(), "Lenovo");
        Assert.assertEquals(persisted.getAssemblyCountry(), "China");

        List<Manufacturer> all = dao.findAll();
        Assert.assertTrue(all.stream().anyMatch(it -> it.getId().equals(manufacturer.getId())));
    }

    @Test
    public void findByNameAndAssemblyCountryIgnoreCaseMustReturnMatchingManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Bosch");
        manufacturer.setAssemblyCountry("Germany");
        dao.save(manufacturer);
        flushAndClear();

        Manufacturer found = dao.findByNameAndAssemblyCountryIgnoreCase("bOsCh", "gErMaNy").orElseThrow();
        Assert.assertEquals(found.getId(), manufacturer.getId());
        Assert.assertEquals(found.getName(), "Bosch");
        Assert.assertEquals(found.getAssemblyCountry(), "Germany");
    }

    @Test
    public void findByNameAndAssemblyCountryIgnoreCaseMustReturnEmptyForUnknownPair() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Bosch");
        manufacturer.setAssemblyCountry("Germany");
        dao.save(manufacturer);
        flushAndClear();

        Assert.assertTrue(dao.findByNameAndAssemblyCountryIgnoreCase("Bosch", "Poland").isEmpty());
        Assert.assertTrue(dao.findByNameAndAssemblyCountryIgnoreCase("Samsung", "Germany").isEmpty());
    }

    @Test
    public void findByNameAndAssemblyCountryIgnoreCaseMustReturnEmptyForNullArguments() {
        Assert.assertTrue(dao.findByNameAndAssemblyCountryIgnoreCase(null, "Germany").isEmpty());
        Assert.assertTrue(dao.findByNameAndAssemblyCountryIgnoreCase("Bosch", null).isEmpty());
        Assert.assertTrue(dao.findByNameAndAssemblyCountryIgnoreCase(null, null).isEmpty());
    }
}