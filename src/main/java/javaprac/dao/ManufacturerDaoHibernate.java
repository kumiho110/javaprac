package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.Manufacturer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ManufacturerDaoHibernate extends CommonDaoHibernate<Manufacturer, Long> implements ManufacturerDao {
    public ManufacturerDaoHibernate(EntityManager entityManager) {
        super(entityManager, Manufacturer.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Manufacturer> findByNameAndAssemblyCountryIgnoreCase(String name, String assemblyCountry) {
        if (name == null || assemblyCountry == null) {
            return Optional.empty();
        }

        return session().createQuery(
                        "select m from Manufacturer m " +
                                "where lower(m.name) = lower(:name) and lower(m.assemblyCountry) = lower(:country)",
                        Manufacturer.class
                )
                .setParameter("name", name)
                .setParameter("country", assemblyCountry)
                .uniqueResultOptional();
    }
}
