package javaprac.dao;

import javaprac.model.Manufacturer;

import java.util.Optional;

public interface ManufacturerDao extends CommonDao<Manufacturer, Long> {

    Optional<Manufacturer> findByNameAndAssemblyCountryIgnoreCase(String name, String assemblyCountry);
}
