package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.ProductType;
import org.springframework.stereotype.Repository;

@Repository
public class ProductTypeDaoHibernate extends CommonDaoHibernate<ProductType, Long> implements ProductTypeDao {
    public ProductTypeDaoHibernate(EntityManager entityManager) {
        super(entityManager, ProductType.class);
    }
}
