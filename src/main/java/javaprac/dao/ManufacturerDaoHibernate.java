package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.Manufacturer;
import org.springframework.stereotype.Repository;

@Repository
public class ManufacturerDaoHibernate extends CommonDaoHibernate<Manufacturer, Long> implements ManufacturerDao {
    public ManufacturerDaoHibernate(EntityManager entityManager) {
        super(entityManager, Manufacturer.class);
    }
}
