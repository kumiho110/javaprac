package javaprac.dao;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javaprac.model.OrderItem;
import java.util.Optional;

@Repository
public class OrderItemDaoHibernate extends CommonDaoHibernate<OrderItem, Long> implements OrderItemDao {

    public OrderItemDaoHibernate(EntityManager entityManager) {
        super(entityManager, OrderItem.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProductId(Long productId) {
        Long cnt = session().createQuery(
                "select count(oi.id) from OrderItem oi where oi.product.id = :productId",
                Long.class
        ).setParameter("productId", productId).getSingleResult();

        return cnt > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId) {
        return session().createQuery(
                        "select oi from OrderItem oi " +
                                "where oi.order.id = :orderId and oi.product.id = :productId",
                        OrderItem.class
                ).setParameter("orderId", orderId)
                .setParameter("productId", productId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItem> findByIdWithOrderAndProduct(Long id) {
        return session().createQuery(
                        "select oi from OrderItem oi " +
                                "join fetch oi.order o " +
                                "join fetch oi.product p " +
                                "where oi.id = :id",
                        OrderItem.class
                ).setParameter("id", id)
                .setMaxResults(1)
                .uniqueResultOptional();
    }
}