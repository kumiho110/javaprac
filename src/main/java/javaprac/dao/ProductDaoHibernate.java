package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoHibernate extends CommonDaoHibernate<Product, Long> implements ProductDao {

    private final EntityManager entityManager;

    public ProductDaoHibernate(EntityManager entityManager) {
        super(entityManager, Product.class);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllWithRefs() {
        return session().createQuery(
                        "select p from Product p " +
                                "join fetch p.type " +
                                "join fetch p.manufacturer " +
                                "order by p.id desc",
                        Product.class
                )
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByIdWithAttributes(Long id) {
        return session().createQuery(
                        "select distinct p from Product p " +
                                "join fetch p.type t " +
                                "join fetch p.manufacturer m " +
                                "left join fetch p.attributeValues v " +
                                "left join fetch v.attribute a " +
                                "where p.id = :id",
                        Product.class
                )
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    @Transactional
    public Product findByIdForUpdate(Long id) {
        Product product = entityManager.find(Product.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTypeId(Long typeId) {
        Long count = session().createQuery(
                        "select count(p.id) from Product p where p.type.id = :typeId",
                        Long.class
                )
                .setParameter("typeId", typeId)
                .uniqueResult();

        return count != null && count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByManufacturerId(Long manufacturerId) {
        Long count = session().createQuery(
                        "select count(p.id) from Product p where p.manufacturer.id = :manufacturerId",
                        Long.class
                )
                .setParameter("manufacturerId", manufacturerId)
                .uniqueResult();

        return count != null && count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> search(String typeName,
                                String manufacturerName,
                                String attributeName,
                                String attributeValue) {

        StringBuilder hql = new StringBuilder(
                "select distinct p from Product p " +
                        "join fetch p.type t " +
                        "join fetch p.manufacturer m "
        );

        boolean hasAttributeFilter =
                attributeName != null && !attributeName.isBlank() &&
                        attributeValue != null && !attributeValue.isBlank();

        if (hasAttributeFilter) {
            hql.append("join p.attributeValues v ");
            hql.append("join v.attribute a ");
        }

        hql.append("where 1=1 ");

        if (typeName != null && !typeName.isBlank()) {
            hql.append("and lower(t.name) = lower(:typeName) ");
        }

        if (manufacturerName != null && !manufacturerName.isBlank()) {
            hql.append("and lower(m.name) = lower(:manufacturerName) ");
        }

        if (attributeName != null && !attributeName.isBlank()) {
            if (hasAttributeFilter) {
                hql.append("and lower(a.name) = lower(:attributeName) ");
            } else {
                hql.append(
                        "and exists (" +
                                "select 1 from ProductAttributeValue v2 " +
                                "join v2.attribute a2 " +
                                "where v2.product.id = p.id and lower(a2.name) = lower(:attributeName)" +
                                ") "
                );
            }
        }

        if (attributeValue != null && !attributeValue.isBlank()) {
            if (hasAttributeFilter) {
                hql.append("and lower(v.value) = lower(:attributeValue) ");
            } else {
                hql.append(
                        "and exists (" +
                                "select 1 from ProductAttributeValue v3 " +
                                "where v3.product.id = p.id and lower(v3.value) = lower(:attributeValue)" +
                                ") "
                );
            }
        }

        hql.append("order by p.id desc");

        var query = session().createQuery(hql.toString(), Product.class);

        if (typeName != null && !typeName.isBlank()) {
            query.setParameter("typeName", typeName);
        }

        if (manufacturerName != null && !manufacturerName.isBlank()) {
            query.setParameter("manufacturerName", manufacturerName);
        }

        if (attributeName != null && !attributeName.isBlank()) {
            query.setParameter("attributeName", attributeName);
        }

        if (attributeValue != null && !attributeValue.isBlank()) {
            query.setParameter("attributeValue", attributeValue);
        }

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listAttributeNamesByType(String typeName) {
        return session().createQuery(
                        "select distinct a.name from ProductTypeAttribute a " +
                                "join a.productType t " +
                                "where lower(t.name) = lower(:typeName) " +
                                "order by a.name asc",
                        String.class
                )
                .setParameter("typeName", typeName)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listAttributeValuesByTypeAndName(String typeName, String attributeName) {
        return session().createQuery(
                        "select distinct v.value from ProductAttributeValue v " +
                                "join v.attribute a " +
                                "join a.productType t " +
                                "where lower(t.name) = lower(:typeName) " +
                                "and lower(a.name) = lower(:attributeName) " +
                                "order by v.value asc",
                        String.class
                )
                .setParameter("typeName", typeName)
                .setParameter("attributeName", attributeName)
                .getResultList();
    }
}