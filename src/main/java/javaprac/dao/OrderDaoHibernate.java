package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.OrderEntity;
import javaprac.model.OrderStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderDaoHibernate extends CommonDaoHibernate<OrderEntity, Long> implements OrderDao {

    public OrderDaoHibernate(EntityManager entityManager) {
        super(entityManager, OrderEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderEntity> findByIdWithItems(Long id) {
        return session().createQuery(
                        "select distinct o from OrderEntity o " +
                                "join fetch o.user u " +
                                "left join fetch o.items i " +
                                "left join fetch i.product p " +
                                "left join fetch p.type t " +
                                "left join fetch p.manufacturer m " +
                                "where o.id = :id",
                        OrderEntity.class
                )
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findAllWithUser() {
        return session().createQuery(
                "select o from OrderEntity o " +
                        "join fetch o.user " +
                        "where o.status <> :cart " +
                        "order by o.createdAt desc",
                OrderEntity.class
        ).setParameter("cart", OrderStatus.cart).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findByStatus(OrderStatus status) {
        return session().createQuery(
                "select o from OrderEntity o join fetch o.user where o.status = :st order by o.createdAt desc",
                OrderEntity.class
        ).setParameter("st", status).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderEntity> findCartByUserIdWithItems(Long userId) {
        return session().createQuery(
                        "select distinct o from OrderEntity o " +
                                "join fetch o.user u " +
                                "left join fetch o.items i " +
                                "left join fetch i.product p " +
                                "left join fetch p.type t " +
                                "left join fetch p.manufacturer m " +
                                "where u.id = :uid and o.status = :status",
                        OrderEntity.class
                )
                .setParameter("uid", userId)
                .setParameter("status", OrderStatus.cart)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findPlacedOrdersByUserId(Long userId) {
        return session().createQuery(
                        "select o from OrderEntity o " +
                                "where o.user.id = :uid and o.status <> :status " +
                                "order by o.createdAt desc",
                        OrderEntity.class
                )
                .setParameter("uid", userId)
                .setParameter("status", OrderStatus.cart)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findAllByUserIdWithItems(Long userId) {
        return session().createQuery(
                        "select distinct o from OrderEntity o " +
                                "join fetch o.user u " +
                                "left join fetch o.items i " +
                                "left join fetch i.product p " +
                                "where u.id = :uid " +
                                "order by o.createdAt desc",
                        OrderEntity.class
                )
                .setParameter("uid", userId)
                .getResultList();
    }
}