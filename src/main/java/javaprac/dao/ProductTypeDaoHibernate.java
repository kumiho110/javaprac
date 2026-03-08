package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.ProductType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ProductTypeDaoHibernate extends CommonDaoHibernate<ProductType, Long> implements ProductTypeDao {
    public ProductTypeDaoHibernate(EntityManager entityManager) {
        super(entityManager, ProductType.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductType> findByNameIgnoreCase(String name) {
        if (name == null) {
            return Optional.empty();
        }

        return session().createQuery(
                        "select t from ProductType t where lower(t.name) = lower(:name)",
                        ProductType.class
                )
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
