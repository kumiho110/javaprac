package javaprac.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javaprac.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractEntityManagerTest {

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public void initEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            return;
        }

        Map<String, Object> props = new HashMap<>();
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/javaprac");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "0438");

        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("jakarta.persistence.jdbc.url", dbUrl);
        props.put("jakarta.persistence.jdbc.user", dbUser);
        props.put("jakarta.persistence.jdbc.password", dbPassword);
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "validate");
        props.put("hibernate.show_sql", System.getProperty("hibernate.show_sql", "false"));
        props.put("hibernate.format_sql", "true");

        emf = Persistence.createEntityManagerFactory("test-pu", props);
    }

    protected void openEntityManager() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    protected void flushAndClear() {
        em.flush();
        em.clear();
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "+" + UUID.randomUUID() + "@example.com";
    }

    protected ProductType persistProductType(String name) {
        ProductType productType = new ProductType();
        productType.setName(name + "-" + UUID.randomUUID());
        em.persist(productType);
        return productType;
    }

    protected Manufacturer persistManufacturer(String name) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(name + "-" + UUID.randomUUID());
        manufacturer.setAssemblyCountry("Country-" + UUID.randomUUID());
        em.persist(manufacturer);
        return manufacturer;
    }

    protected AppUser persistUser(String prefix, AppRole role, boolean enabled) {
        AppUser user = new AppUser();
        user.setEmail(uniqueEmail(prefix));
        user.setPasswordHash("hash-" + UUID.randomUUID());
        user.setFullName("User " + prefix);
        user.setRole(role);
        user.setEnabled(enabled);
        em.persist(user);
        return user;
    }

    protected Product persistProduct(ProductType type, Manufacturer manufacturer, String name, BigDecimal price, int stockQty) {
        Product product = new Product();
        product.setType(type);
        product.setManufacturer(manufacturer);
        product.setName(name + "-" + UUID.randomUUID());
        product.setDescription("Description for " + name);
        product.setPrice(price);
        product.setStockQty(stockQty);
        em.persist(product);
        return product;
    }

    protected ProductTypeAttribute persistAttribute(ProductType type, String name, int sortOrder, boolean required) {
        ProductTypeAttribute attribute = new ProductTypeAttribute();
        attribute.setProductType(type);
        attribute.setName(name);
        attribute.setSortOrder(sortOrder);
        attribute.setRequired(required);
        em.persist(attribute);
        return attribute;
    }

    protected ProductAttributeValue persistAttributeValue(Product product, ProductTypeAttribute attribute, String value) {
        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setProduct(product);
        attributeValue.setAttribute(attribute);
        attributeValue.setValue(value);
        em.persist(attributeValue);
        return attributeValue;
    }

    protected OrderEntity persistOrder(AppUser user, OrderStatus status, String address, BigDecimal totalAmount) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(status);
        order.setDeliveryAddress(address);
        order.setDeliveryTimeWindow("10:00-14:00");
        order.setTotalAmount(totalAmount);
        em.persist(order);
        return order;
    }

    protected OrderItem persistOrderItem(OrderEntity order, Product product, int qty, BigDecimal unitPrice) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQty(qty);
        item.setUnitPrice(unitPrice);
        em.persist(item);
        return item;
    }

    @AfterMethod(alwaysRun = true)
    public void rollbackAndClose() {
        if (em != null) {
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } finally {
                em.close();
                em = null;
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
}
