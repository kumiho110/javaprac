package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.ProductTypeAttribute;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductTypeAttributeDaoHibernate
        extends CommonDaoHibernate<ProductTypeAttribute, Long>
        implements ProductTypeAttributeDao {

    public ProductTypeAttributeDaoHibernate(EntityManager entityManager) {
        super(entityManager, ProductTypeAttribute.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductTypeAttribute> findByProductTypeId(Long productTypeId) {
        return session().createQuery(
                        "select a from ProductTypeAttribute a " +
                                "where a.productType.id = :typeId " +
                                "order by a.sortOrder asc, a.id asc",
                        ProductTypeAttribute.class
                )
                .setParameter("typeId", productTypeId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductTypeAttribute> findByProductTypeIdAndNameIgnoreCase(Long productTypeId, String name) {
        if (productTypeId == null || name == null) {
            return Optional.empty();
        }

        return session().createQuery(
                        "select a from ProductTypeAttribute a " +
                                "where a.productType.id = :typeId and lower(a.name) = lower(:name)",
                        ProductTypeAttribute.class
                )
                .setParameter("typeId", productTypeId)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    @Override
    @Transactional(readOnly = true)
    public int findMaxSortOrderByProductTypeId(Long productTypeId) {
        Integer result = session().createQuery(
                        "select max(a.sortOrder) from ProductTypeAttribute a where a.productType.id = :typeId",
                        Integer.class
                )
                .setParameter("typeId", productTypeId)
                .uniqueResult();

        return result == null ? 0 : result;
    }
}