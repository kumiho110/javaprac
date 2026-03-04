package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.ProductTypeAttribute;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}