package javaprac.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class AdminAndStaffSystemTest extends BaseSystemTest {

    @Test
    public void adminCanCreateUserAndRejectDuplicateEmail() {
        TestUser manager = newUser("managed");

        createUserViaAdmin(manager, "MANAGER", true);
        waitForText("Пользователь создан");
        logout();

        loginAsAdmin();
        open("/admin/users/new");
        type("email", manager.email());
        type("password", manager.password());
        type("password2", manager.password());
        type("fullName", manager.fullName());
        selectByVisibleText("role", "MANAGER");
        clickButton("Создать");
        waitForText("Такой email уже занят");
    }

    @Test
    public void adminCanDisableUserAndDisabledUserCannotLogin() {
        TestUser user = newUser("disableme");

        createUserViaAdmin(user, "USER", true);
        waitForText("Пользователь создан");
        clickRowButton(user.email(), "Отключить");
        waitForText("Пользователь отключён");
        logout();

        login(user.email(), user.password());
        waitForText("Неверный email или пароль");
    }

    @Test
    public void adminCanEditUserProfile() {
        TestUser user = newUser("editme");

        createUserViaAdmin(user, "USER", true);
        waitForText("Пользователь создан");

        clickRowLink(user.email(), "Редактировать");
        waitForText("Редактирование пользователя");

        String updatedName = user.fullName() + " Updated";
        type("fullName", updatedName);
        type("phone", "+79991112233");

        WebElement address = driver.findElement(By.name("address"));
        address.clear();
        address.sendKeys("Обновлённый адрес");

        clickButton("Сохранить");
        waitForText("Пользователь сохранён");
        waitForText(updatedName);
        waitForText("+79991112233");
    }

    @Test
    public void adminCanUpdateUserCredentials() {
        TestUser user = newUser("credme");

        createUserViaAdmin(user, "USER", true);
        waitForText("Пользователь создан");

        clickRowLink(user.email(), "Редактировать");
        waitForText("Редактирование пользователя");

        String newEmail = unique("updated") + "@mail.test";
        type("email", newEmail);
        type("resetValue1", "Pass124");
        type("resetValue2", "Pass124");
        clickButton("Сохранить учётные данные");
        waitForText("Учётные данные обновлены");

        logout();
        login(newEmail, "Pass124");
        Assert.assertTrue(driver.getCurrentUrl().contains("/products"));
        waitForText("Каталог товаров");
    }

    @Test
    public void managerCanCreateTypeAndDuplicateTypeIsRejected() {
        TestUser manager = createManagerViaAdmin();
        login(manager.email(), manager.password());

        String typeName = unique("Тип");

        open("/staff/catalog/types/new");
        type("name", typeName);
        clickButton("Создать");
        waitForText("Тип товара создан");
        waitForText(typeName);

        open("/staff/catalog/types/new");
        type("name", typeName);
        clickButton("Создать");
        waitForText("Такой тип товара уже существует");
    }

    @Test
    public void managerCannotDeleteTypeThatAlreadyHasProducts() {
        TestUser manager = createManagerViaAdmin();

        login(manager.email(), manager.password());
        open("/staff/catalog/types");
        clickRowButtonAndAcceptAlert("Телевизоры", "Удалить");
        waitForText("Нельзя удалить тип товара: к нему уже привязаны товары");
    }

    @Test
    public void managerCanCreateManufacturerAttributeAndProduct() {
        TestUser manager = createManagerViaAdmin();
        login(manager.email(), manager.password());

        String typeName = unique("ТестТип");
        open("/staff/catalog/types/new");
        type("name", typeName);
        clickButton("Создать");
        waitForText("Тип товара создан");

        String manufacturerName = unique("Maker");
        open("/staff/catalog/manufacturers/new");
        type("name", manufacturerName);
        type("assemblyCountry", "Россия");
        clickButton("Создать");
        waitForText("Производитель создан");

        open("/staff/catalog/types");
        clickRowLink(typeName, "Характеристики");
        clickLink("Добавить характеристику");

        type("name", "Тестовый параметр");
        WebElement required = driver.findElement(By.name("required"));
        if (!required.isSelected()) {
            required.click();
        }
        clickButton("Создать");
        waitForText("Характеристика создана");
        waitForText("Тестовый параметр");

        clickLink("Добавить характеристику");
        type("name", "Тестовый параметр");
        clickButton("Создать");
        waitForText("Такая характеристика уже существует для этого типа товара");

        open("/staff/products/new");
        String productName = unique("Товар");
        type("name", productName);
        selectByVisibleText("typeId", typeName);
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.name("attributeId"), 0));
        selectByVisibleText("manufacturerId", manufacturerName + " (Россия)");
        type("description", "Системный тестовый товар");
        type("price", "12345");
        type("stockQty", "3");

        List<WebElement> attrValues = driver.findElements(By.name("attributeValue"));
        attrValues.get(0).sendKeys("Значение");

        clickButton("Создать");
        waitForText("Управление товарами");
        waitForText(productName);
    }

    @Test
    public void managerCanProcessDeliveredOrderAndDeleteIt() {
        TestUser user = newUser("stafforder");

        register(user);
        open("/products/4");
        waitForText("Lenovo IdeaPad Slim 5");
        clickButton("В корзину");

        open("/cart");
        type("deliveryAddress", "Санкт-Петербург, Тест 10");
        clickButton("Оформить заказ");
        waitForText("processing");
        logout();

        TestUser manager = createManagerViaAdmin();
        login(manager.email(), manager.password());

        open("/staff/orders");
        clickRowLink(user.email(), "Открыть");

        selectByVisibleText("status", "Packed");
        clickButton("Сохранить статус");
        waitForText("Статус:");
        waitForText("packed");

        selectByVisibleText("status", "Delivered");
        clickButton("Сохранить статус");
        waitForText("Статус:");
        waitForText("delivered");

        clickButtonAndAcceptAlert("Удалить заказ");
        waitForText("Заказ удалён");
    }

    @Test
    public void managerGetsValidationErrorOnInvalidStatusTransition() {
        TestUser user = newUser("badtransition");

        register(user);
        open("/products/5");
        waitForText("ASUS VivoBook 15");
        clickButton("В корзину");

        open("/cart");
        type("deliveryAddress", "Казань, Проверка 3");
        clickButton("Оформить заказ");
        logout();

        TestUser manager = createManagerViaAdmin();
        login(manager.email(), manager.password());

        open("/staff/orders");
        clickRowLink(user.email(), "Открыть");
        selectByVisibleText("status", "Delivered");
        clickButton("Сохранить статус");
        waitForText("Из processing можно перейти только в packed или cancelled");
    }

    @Test
    public void managerCannotDeleteProcessingOrder() {
        TestUser user = newUser("processingdelete");

        register(user);
        open("/products/4");
        waitForText("Lenovo IdeaPad Slim 5");
        clickButton("В корзину");

        open("/cart");
        type("deliveryAddress", "Новосибирск, Проверка 7");
        clickButton("Оформить заказ");
        waitForText("processing");
        logout();

        TestUser manager = createManagerViaAdmin();
        login(manager.email(), manager.password());

        open("/staff/orders");
        clickRowButtonAndAcceptAlert(user.email(), "Удалить");
        waitForText("Удалять можно только отменённые или доставленные заказы");
    }
}