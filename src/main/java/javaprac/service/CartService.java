package javaprac.service;

import javaprac.dao.AppUserDao;
import javaprac.dao.OrderDao;
import javaprac.dao.OrderItemDao;
import javaprac.dao.ProductDao;
import javaprac.model.AppUser;
import javaprac.model.OrderEntity;
import javaprac.model.OrderItem;
import javaprac.model.OrderStatus;
import javaprac.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;

@Service
public class CartService {

    private final AppUserDao appUserDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public CartService(AppUserDao appUserDao, ProductDao productDao, OrderDao orderDao, OrderItemDao orderItemDao) {
        this.appUserDao = appUserDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    @Transactional
    public OrderEntity getOrCreateCart(Long userId) {
        var existing = orderDao.findCartByUserIdWithItems(userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        AppUser user = appUserDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        OrderEntity cart = new OrderEntity();
        cart.setUser(user);
        cart.setStatus(OrderStatus.cart);
        cart.setDeliveryAddress(null);
        cart.setDeliveryTimeWindow(null);
        cart.setTotalAmount(BigDecimal.ZERO);
        orderDao.save(cart);

        return orderDao.findByIdWithItems(cart.getId()).orElse(cart);
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getPlacedOrders(Long userId) {
        return orderDao.findPlacedOrdersByUserId(userId);
    }

    @Transactional
    public void addToCart(Long userId, Long productId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be > 0");
        }

        OrderEntity cart = getOrCreateCart(userId);
        Product product = productDao.findByIdForUpdate(productId);

        int have = product.getStockQty();

        if (have <= 0) {
            throw new IllegalStateException("Нет в наличии");
        }
        if (have < qty) {
            throw new IllegalStateException("Недостаточно на складе: доступно " + have + ", запрошено " + qty);
        }

        product.setStockQty(product.getStockQty() - qty);
        productDao.update(product);

        OrderItem item = orderItemDao.findByOrderIdAndProductId(cart.getId(), productId).orElse(null);

        if (item == null) {
            item = new OrderItem();
            item.setOrder(cart);
            item.setProduct(product);
            item.setQty(qty);
            item.setUnitPrice(product.getPrice());
            orderItemDao.save(item);
            cart.getItems().add(item);
        } else {
            item.setQty(item.getQty() + qty);
            orderItemDao.update(item);
        }

        recalcTotal(cart);
        orderDao.findByIdWithItems(cart.getId());
    }

    @Transactional
    public void updateCartItemQty(Long userId, Long itemId, int newQty) {
        OrderItem item = orderItemDao.findByIdWithOrderAndProduct(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Не найден элемент корзины: " + itemId));

        OrderEntity cart = item.getOrder();

        if (!cart.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Предмет не принадлежит данному пользователю");
        }
        if (cart.getStatus() != OrderStatus.cart) {
            throw new IllegalStateException("Это не корзина");
        }

        int oldQty = item.getQty();

        if (newQty <= 0) {
            removeFromCart(userId, itemId);
            return;
        }

        int delta = newQty - oldQty;

        if (delta > 0) {
            Product product = productDao.findByIdForUpdate(item.getProduct().getId());
            int have = product.getStockQty();

            if (have < delta) {
                if (have <= 0) {
                    throw new IllegalStateException("Нет в наличии");
                }
                throw new IllegalStateException("Недостаточно на складе: доступно " + have + ", нужно добавить " + delta);
            }
            product.setStockQty(product.getStockQty() - delta);
            productDao.update(product);
        } else if (delta < 0) {
            Product product = productDao.findByIdForUpdate(item.getProduct().getId());
            product.setStockQty(product.getStockQty() + (-delta));
            productDao.update(product);
        }

        item.setQty(newQty);
        orderItemDao.update(item);

        recalcTotal(cart);
        orderDao.findByIdWithItems(cart.getId());
    }

    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        OrderItem item = orderItemDao.findByIdWithOrderAndProduct(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Не найден элемент корзины: " + itemId));

        OrderEntity cart = item.getOrder();

        if (!cart.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Предмет не принадлежит данному пользователю");
        }
        if (cart.getStatus() != OrderStatus.cart) {
            throw new IllegalStateException("Это не корзина");
        }

        Product product = productDao.findByIdForUpdate(item.getProduct().getId());
        product.setStockQty(product.getStockQty() + item.getQty());
        productDao.update(product);

        cart.getItems().remove(item);
        orderItemDao.delete(item);

        recalcTotal(cart);
        orderDao.findByIdWithItems(cart.getId());
    }

    @Transactional
    public OrderEntity checkout(Long userId, String deliveryAddress, String deliveryTimeWindow) {
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new IllegalArgumentException("Требуется адрес доставки");
        }

        OrderEntity cart = orderDao.findCartByUserIdWithItems(userId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста");
        }

        recalcTotal(cart);
        cart.setStatus(OrderStatus.processing);
        cart.setDeliveryAddress(deliveryAddress.trim());
        cart.setDeliveryTimeWindow(
                deliveryTimeWindow == null || deliveryTimeWindow.isBlank()
                        ? null
                        : deliveryTimeWindow.trim()
        );

        orderDao.update(cart);
        return cart;
    }

    private void recalcTotal(OrderEntity order) {
        BigDecimal total = BigDecimal.ZERO;

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                BigDecimal line = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty()));
                total = total.add(line);
            }
        }

        order.setTotalAmount(total);
        orderDao.update(order);
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getCartQtyByProductId(Long userId) {
        var cartOpt = orderDao.findCartByUserIdWithItems(userId);
        if (cartOpt.isEmpty() || cartOpt.get().getItems() == null) {
            return Collections.emptyMap();
        }

        Map<Long, Integer> result = new LinkedHashMap<>();
        for (OrderItem item : cartOpt.get().getItems()) {
            if (item.getProduct() != null && item.getProduct().getId() != null) {
                result.put(item.getProduct().getId(), item.getQty());
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getCartQtyByProductIdStr(Long userId) {
        Map<Long, Integer> raw = getCartQtyByProductId(userId);
        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<Long, Integer> e : raw.entrySet()) {
            result.put(String.valueOf(e.getKey()), e.getValue());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public int getCartDistinctItemCount(Long userId) {
        var cartOpt = orderDao.findCartByUserIdWithItems(userId);
        if (cartOpt.isEmpty() || cartOpt.get().getItems() == null) {
            return 0;
        }
        return cartOpt.get().getItems().size();
    }

    @Transactional
    public void deleteCartAndReturnStock(Long userId) {
        OrderEntity cart = orderDao.findCartByUserIdWithItems(userId).orElse(null);
        if (cart == null) {
            return;
        }

        if (cart.getStatus() != OrderStatus.cart) {
            throw new IllegalStateException("Это не корзина");
        }

        if (cart.getItems() != null) {
            java.util.List<OrderItem> items = new java.util.ArrayList<>(cart.getItems());

            for (OrderItem item : items) {
                if (item.getProduct() == null || item.getProduct().getId() == null || item.getQty() == null) {
                    continue;
                }

                Product product = productDao.findByIdForUpdate(item.getProduct().getId());
                product.setStockQty(product.getStockQty() + item.getQty());
                productDao.update(product);

                cart.getItems().remove(item);
                orderItemDao.delete(item);
            }
        }

        recalcTotal(cart);
        orderDao.delete(cart);
    }
}