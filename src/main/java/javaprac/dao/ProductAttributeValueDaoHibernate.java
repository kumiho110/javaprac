package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.ProductAttributeValue;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ProductAttributeValueDaoHibernate
        extends CommonDaoHibernate<ProductAttributeValue, Long>
        implements ProductAttributeValueDao {

    public ProductAttributeValueDaoHibernate(EntityManager entityManager) {
        super(entityManager, ProductAttributeValue.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValue> findByProductId(Long productId) {
        return session().createQuery(
                        "select v from ProductAttributeValue v " +
                                "join fetch v.attribute a " +
                                "join fetch a.productType pt " +
                                "where v.product.id = :productId " +
                                "order by a.sortOrder asc, a.id asc",
                        ProductAttributeValue.class
                )
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAttributeId(Long attributeId) {
        Long count = session().createQuery(
                        "select count(v.id) from ProductAttributeValue v where v.attribute.id = :attributeId",
                        Long.class
                )
                .setParameter("attributeId", attributeId)
                .uniqueResult();

        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId) {
        session().createMutationQuery(
                        "delete from ProductAttributeValue v where v.product.id = :productId"
                )
                .setParameter("productId", productId)
                .executeUpdate();
    }
}