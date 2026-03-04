package javaprac.dao;

import javaprac.model.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

public class OrderDaoTestNG extends AbstractEntityManagerTest {

    private OrderDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new OrderDaoHibernate(em);
    }

    @Test
    public void orderLifecycleDefaultsAndQueryMethodsMustBeCovered() {
        ProductType type = persistProductType("TV");
        Manufacturer manufacturer = persistManufacturer("LG");
        Product product = persistProduct(type, manufacturer, "NanoCell", new BigDecimal("700.00"), 4);
        AppUser user1 = persistUser("user1", AppRole.USER, true);
        AppUser user2 = persistUser("user2", AppRole.USER, true);

        OrderEntity cart = new OrderEntity();
        cart.setUser(user1);
        cart.setDeliveryAddress("Cart address");
        dao.save(cart);

        OrderEntity processing = persistOrder(user1, OrderStatus.processing, "Processing address", new BigDecimal("1400.00"));
        OrderItem processingItem = persistOrderItem(processing, product, 2, new BigDecimal("700.00"));

        OrderEntity delivered = persistOrder(user1, OrderStatus.delivered, "Delivered address", new BigDecimal("700.00"));
        persistOrderItem(delivered, product, 1, new BigDecimal("700.00"));

        OrderEntity otherUserOrder = persistOrder(user2, OrderStatus.processing, "Other address", new BigDecimal("700.00"));
        persistOrderItem(otherUserOrder, product, 1, new BigDecimal("700.00"));

        flushAndClear();

        OrderEntity persistedCart = dao.findById(cart.getId()).orElseThrow();
        Assert.assertEquals(persistedCart.getStatus(), OrderStatus.cart);
        Assert.assertEquals(persistedCart.getTotalAmount().compareTo(BigDecimal.ZERO), 0);
        Assert.assertNotNull(persistedCart.getCreatedAt());

        OrderEntity fullOrder = dao.findByIdWithItems(processing.getId()).orElseThrow();
        Assert.assertEquals(fullOrder.getUser().getId(), user1.getId());
        Assert.assertEquals(fullOrder.getItems().size(), 1);
        Assert.assertEquals(fullOrder.getItems().get(0).getId(), processingItem.getId());
        Assert.assertTrue(dao.findByIdWithItems(999999L).isEmpty());

        List<OrderEntity> allWithUser = dao.findAllWithUser();
        Assert.assertEquals(allWithUser.size(), 3);
        Assert.assertTrue(allWithUser.stream().noneMatch(o -> o.getStatus() == OrderStatus.cart));
        Assert.assertTrue(allWithUser.stream().allMatch(o -> o.getUser() != null));

        List<OrderEntity> processingOrders = dao.findByStatus(OrderStatus.processing);
        Assert.assertEquals(processingOrders.size(), 2);
        Assert.assertTrue(dao.findByStatus(OrderStatus.cancelled).isEmpty());

        OrderEntity foundCart = dao.findCartByUserIdWithItems(user1.getId()).orElseThrow();
        Assert.assertEquals(foundCart.getId(), cart.getId());
        Assert.assertTrue(dao.findCartByUserIdWithItems(user2.getId()).isEmpty());

        List<OrderEntity> placedByUser1 = dao.findPlacedOrdersByUserId(user1.getId());
        Assert.assertEquals(placedByUser1.size(), 2);
        Assert.assertTrue(placedByUser1.stream().noneMatch(o -> o.getStatus() == OrderStatus.cart));
        Assert.assertTrue(dao.findPlacedOrdersByUserId(999999L).isEmpty());

        List<OrderEntity> allUser1Orders = dao.findAllByUserIdWithItems(user1.getId());
        Assert.assertEquals(allUser1Orders.size(), 3);
        Assert.assertTrue(allUser1Orders.stream().allMatch(o -> o.getUser().getId().equals(user1.getId())));
        Assert.assertTrue(dao.findAllByUserIdWithItems(999999L).isEmpty());
    }
}
