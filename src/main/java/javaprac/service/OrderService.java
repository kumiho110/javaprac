package javaprac.service;

import javaprac.dao.OrderDao;
import javaprac.dao.ProductDao;
import javaprac.model.OrderEntity;
import javaprac.model.OrderItem;
import javaprac.model.OrderStatus;
import javaprac.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderService {

    private final ProductDao productDao;
    private final OrderDao orderDao;

    public OrderService(ProductDao productDao, OrderDao orderDao) {
        this.productDao = productDao;
        this.orderDao = orderDao;
    }

    @Transactional
    public void cancelOrderByUser(Long userId, Long orderId) {
        OrderEntity order = orderDao.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Заказ не принадлежит этому пользователю");
        }

        cancelOrderInternal(order);
    }

    @Transactional
    public void updateOrderStatusByStaff(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderDao.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

        OrderStatus current = order.getStatus();

        if (current == OrderStatus.cart) {
            throw new IllegalArgumentException("Корзина не является оформленным заказом");
        }
        if (current == OrderStatus.cancelled) {
            throw new IllegalArgumentException("Отменённый заказ нельзя изменять");
        }
        if (current == OrderStatus.delivered) {
            throw new IllegalArgumentException("Полученный заказ нельзя изменять");
        }
        if (newStatus == OrderStatus.cart) {
            throw new IllegalArgumentException("Нельзя перевести заказ в статус cart");
        }

        if (newStatus == OrderStatus.cancelled) {
            cancelOrderInternal(order);
            return;
        }

        if (current == OrderStatus.processing && newStatus != OrderStatus.packed) {
            throw new IllegalArgumentException("Из processing можно перейти только в packed или cancelled");
        }

        if (current == OrderStatus.packed && newStatus != OrderStatus.delivered) {
            throw new IllegalArgumentException("Из packed можно перейти только в delivered или cancelled");
        }

        order.setStatus(newStatus);
    }

    @Transactional
    public void deleteCancelledDeliveredOrder(Long orderId) {
        OrderEntity order = orderDao.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

        if (order.getStatus() != OrderStatus.cancelled && order.getStatus() != OrderStatus.delivered) {
            throw new IllegalArgumentException("Удалять можно только отменённые или доставленные заказы");
        }

        orderDao.delete(order);
    }

    private void cancelOrderInternal(OrderEntity order) {
        OrderStatus current = order.getStatus();

        if (current == OrderStatus.cart) {
            throw new IllegalArgumentException("Корзина не является оформленным заказом");
        }
        if (current == OrderStatus.cancelled) {
            throw new IllegalArgumentException("Заказ уже отменён");
        }
        if (current == OrderStatus.delivered) {
            throw new IllegalArgumentException("Полученный заказ нельзя отменить");
        }

        restoreStock(order);
        order.setStatus(OrderStatus.cancelled);
    }

    private void restoreStock(OrderEntity order) {
        if (order.getItems() == null) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            if (item == null || item.getProduct() == null || item.getQty() == null || item.getQty() <= 0) {
                continue;
            }

            Product p = productDao.findByIdForUpdate(item.getProduct().getId());
            int currentStock = p.getStockQty() == null ? 0 : p.getStockQty();
            p.setStockQty(currentStock + item.getQty());
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<OrderEntity> listStaffOrders(String status) {
        java.util.List<OrderEntity> orders;

        if (status == null || status.isBlank()) {
            orders = orderDao.findAllWithUser();
        } else {
            try {
                OrderStatus st = OrderStatus.valueOf(status);
                if (st == OrderStatus.cart) {
                    orders = orderDao.findAllWithUser();
                } else {
                    orders = orderDao.findByStatus(st);
                }
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Неизвестный статус");
            }
        }

        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.cart)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderEntity getStaffOrderDetails(Long id) {
        OrderEntity order = orderDao.findByIdWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));

        if (order.getStatus() == OrderStatus.cart) {
            throw new IllegalArgumentException("Корзина не является оформленным заказом");
        }

        return order;
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderForUserView(Long userId, Long orderId) {
        OrderEntity order = orderDao.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

        if (!order.getUser().getId().equals(userId) || order.getStatus() == OrderStatus.cart) {
            throw new IllegalArgumentException("Заказ не найден: " + orderId);
        }

        return order;
    }
}