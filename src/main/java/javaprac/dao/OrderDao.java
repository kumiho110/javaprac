package javaprac.dao;

import javaprac.model.OrderEntity;
import javaprac.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderDao extends CommonDao<OrderEntity, Long> {
    Optional<OrderEntity> findByIdWithItems(Long id);
    List<OrderEntity> findAllWithUser();
    List<OrderEntity> findByStatus(OrderStatus status);
    Optional<OrderEntity> findCartByUserIdWithItems(Long userId);
    List<OrderEntity> findPlacedOrdersByUserId(Long userId);
    List<OrderEntity> findAllByUserIdWithItems(Long userId);
}