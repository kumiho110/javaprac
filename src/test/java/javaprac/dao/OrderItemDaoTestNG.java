package javaprac.dao;

import javaprac.model.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Optional;

public class OrderItemDaoTestNG extends AbstractEntityManagerTest {

    private OrderItemDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new OrderItemDaoHibernate(em);
    }

    @Test
    public void orderItemQueriesMustCoverFoundAndNotFoundBranches() {
        ProductType type = persistProductType("TV");
        Manufacturer manufacturer = persistManufacturer("Samsung");
        Product product = persistProduct(type, manufacturer, "Model X", new BigDecimal("500.00"), 3);
        Product otherProduct = persistProduct(type, manufacturer, "Model Y", new BigDecimal("600.00"), 2);
        AppUser user = persistUser("buyer", AppRole.USER, true);
        OrderEntity order = persistOrder(user, OrderStatus.processing, "Address", new BigDecimal("500.00"));
        OrderItem item = persistOrderItem(order, product, 1, new BigDecimal("500.00"));

        flushAndClear();

        Assert.assertTrue(dao.existsByProductId(product.getId()));
        Assert.assertFalse(dao.existsByProductId(otherProduct.getId()));
        Assert.assertFalse(dao.existsByProductId(999999L));

        Optional<OrderItem> foundByPair = dao.findByOrderIdAndProductId(order.getId(), product.getId());
        Assert.assertTrue(foundByPair.isPresent());
        Assert.assertEquals(foundByPair.get().getId(), item.getId());
        Assert.assertEquals(foundByPair.get().getQty(), Integer.valueOf(1));
        Assert.assertTrue(dao.findByOrderIdAndProductId(order.getId(), otherProduct.getId()).isEmpty());

        Optional<OrderItem> foundById = dao.findByIdWithOrderAndProduct(item.getId());
        Assert.assertTrue(foundById.isPresent());
        Assert.assertEquals(foundById.get().getOrder().getId(), order.getId());
        Assert.assertEquals(foundById.get().getProduct().getId(), product.getId());
        Assert.assertTrue(dao.findByIdWithOrderAndProduct(999999L).isEmpty());
    }
}
