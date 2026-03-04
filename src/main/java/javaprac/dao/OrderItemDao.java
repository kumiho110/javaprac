package javaprac.dao;

import javaprac.model.OrderItem;

import java.util.Optional;

public interface OrderItemDao extends CommonDao<OrderItem, Long> {
    boolean existsByProductId(Long productId);
    Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);
    Optional<OrderItem> findByIdWithOrderAndProduct(Long id);
}