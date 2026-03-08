package javaprac.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PublicAuthCartSystemTest extends BaseSystemTest {

    @Test
    public void guestCanBrowseCatalogAndOpenProductCard() {
        open("/");
        waitForText("Интернет-магазин бытовой техники");
        clickLink("Каталог");
        waitForText("Каталог товаров");

        selectByVisibleText("type", "Холодильники");
        selectByVisibleText("manufacturer", "Bosch");
        clickButton("Фильтровать");

        waitForText("Bosch KGN39VL25R");
        Assert.assertFalse(pageContains("Lenovo IdeaPad Slim 5"));

        clickRowLink("Bosch KGN39VL25R", "Открыть");
        waitForText("Двухкамерный холодильник с системой No Frost");
        waitForText("Производитель:");
    }

    @Test
    public void protectedPagesRedirectGuestToLogin() {
        open("/cart");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
        waitForText("Вход");
    }

    @Test
    public void loginWithContinueRedirectsToCart() {
        TestUser user = newUser("continue");

        register(user);
        logout();

        open("/login?continue=/cart");
        type("email", user.email());
        type("password", user.password());
        clickButton("Войти");

        Assert.assertTrue(driver.getCurrentUrl().contains("/cart"));
        waitForText("Корзина");
    }

    @Test
    public void registrationSucceedsAndDuplicateEmailIsRejected() {
        TestUser user = newUser("buyer");

        register(user);
        Assert.assertTrue(driver.getCurrentUrl().contains("/products"));
        waitForText("Каталог товаров");
        logout();

        open("/register");
        type("email", user.email());
        type("fullName", user.fullName());
        type("password", user.password());
        type("password2", user.password());
        clickButton("Create account");
        waitForText("Email уже зарегистрирован");
    }

    @Test
    public void invalidLoginShowsErrorMessage() {
        open("/login");
        type("email", "nobody@example.com");
        type("password", "wrongpass");
        clickButton("Войти");
        waitForText("Неверный email или пароль");
    }

    @Test
    public void userCanUpdateCartItemQuantityAndRemoveIt() {
        TestUser user = newUser("cartops");

        register(user);

        open("/products/3");
        waitForText("Bosch KGN39VL25R");

        WebElement qty = driver.findElement(By.name("qty"));
        qty.clear();
        qty.sendKeys("1");
        clickButton("В корзину");

        open("/cart");
        waitForText("Bosch KGN39VL25R");

        WebElement cartQty = driver.findElement(By.name("qty"));
        cartQty.clear();
        cartQty.sendKeys("2");
        clickButton("Обновить");
        waitForText("Количество обновлено");

        clickButtonAndAcceptAlert("Удалить");
        waitForText("Товар удалён из корзины");
        waitForText("Корзина пуста.");
    }

    @Test
    public void userCanAddToCartCheckoutAndCancelOrder() {
        TestUser user = newUser("orderuser");

        register(user);

        open("/products/3");
        waitForText("Bosch KGN39VL25R");

        WebElement qty = driver.findElement(By.name("qty"));
        qty.clear();
        qty.sendKeys("1");
        clickButton("В корзину");

        open("/cart");
        waitForText("Bosch KGN39VL25R");
        type("deliveryAddress", "Москва, Тестовая 1");
        type("deliveryTimeWindow", "18:00-21:00");
        clickButton("Оформить заказ");

        waitForText("Заказ #");
        waitForText("processing");

        clickButtonAndAcceptAlert("Отменить заказ");
        waitForText("Заказ отменён");
    }

    @Test
    public void userCanUpdateProfileAndChangePassword() {
        TestUser user = newUser("profile");

        register(user);

        open("/me");
        type("fullName", user.fullName() + " Updated");
        type("phone", "+79990001122");

        WebElement address = driver.findElement(By.name("address"));
        address.clear();
        address.sendKeys("Новый адрес");

        clickButton("Сохранить");
        waitForText("Профиль сохранён");

        type("currentPassword", user.password());
        type("newPassword", "Pass124");
        type("newPassword2", "Pass124");
        clickButton("Изменить пароль");
        waitForText("Пароль успешно изменён");

        logout();
        login(user.email(), "Pass124");
        Assert.assertTrue(driver.getCurrentUrl().contains("/products"));
    }
}