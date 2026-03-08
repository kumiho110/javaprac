package javaprac.system;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.UUID;

public abstract class BaseSystemTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    protected record TestUser(String email, String password, String fullName) {}

    @BeforeMethod
    public void setUpDriver() {
        baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
        String browser = System.getProperty("browser", "chrome");

        if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("-headless");
            driver = new FirefoxDriver(options);
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--window-size=1600,1200", "--no-sandbox", "--disable-dev-shm-usage");
            driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void open(String path) {
        driver.get(baseUrl + path);
    }

    protected void type(String name, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));
        element.clear();
        element.sendKeys(value);
    }

    protected void clearAndType(String name, String value) {
        type(name, value);
    }

    protected void clickLink(String text) {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(text))).click();
    }

    protected void clickButton(String text) {
        By xpath = By.xpath("//button[normalize-space()=" + quote(text) + "]");
        wait.until(ExpectedConditions.elementToBeClickable(xpath)).click();
    }

    protected void clickButtonAndAcceptAlert(String text) {
        clickButton(text);
        acceptAlertIfPresent();
    }

    protected void acceptAlertIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException ignored) {
        }
    }

    protected void selectByVisibleText(String name, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));
        new Select(element).selectByVisibleText(value);
    }

    protected void setCheckbox(String name, boolean value) {
        WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));
        if (checkbox.isSelected() != value) {
            checkbox.click();
        }
    }

    protected void waitForText(String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text));
    }

    protected void waitForUrlContains(String text) {
        wait.until(ExpectedConditions.urlContains(text));
    }

    protected boolean pageContains(String text) {
        return driver.getPageSource().contains(text);
    }

    protected String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected TestUser newUser(String prefix) {
        String id = unique(prefix);
        return new TestUser(id + "@mail.test", "Pass123", "User " + id);
    }

    protected void register(TestUser user) {
        open("/register");
        type("email", user.email());
        type("fullName", user.fullName());
        type("password", user.password());
        type("password2", user.password());
        clickButton("Create account");
    }

    protected void login(String email, String password) {
        open("/login");
        type("email", email);
        type("password", password);
        clickButton("Войти");
    }

    protected void loginAsAdmin() {
        login("admin@example.com", "admin");
    }

    protected void logout() {
        if (pageContains("Выйти")) {
            clickButton("Выйти");
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlToBe(baseUrl + "/"),
                    ExpectedConditions.urlContains("/login")
            ));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Войти"));
        }
    }

    protected void createUserViaAdmin(TestUser user, String role, boolean enabled) {
        loginAsAdmin();
        open("/admin/users/new");
        type("email", user.email());
        type("password", user.password());
        type("password2", user.password());
        type("fullName", user.fullName());
        type("phone", "+70000000000");
        WebElement address = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("address")));
        address.clear();
        address.sendKeys("Test address");
        selectByVisibleText("role", role);
        setCheckbox("enabled", enabled);
        clickButton("Создать");
    }

    protected TestUser createManagerViaAdmin() {
        TestUser manager = newUser("manager");
        createUserViaAdmin(manager, "MANAGER", true);
        waitForText("Пользователь создан");
        logout();
        return manager;
    }

    protected WebElement rowByCellText(String text) {
        By xpath = By.xpath("//tr[td[contains(normalize-space(.), " + quote(text) + ")]]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(xpath));
    }

    protected void clickRowButton(String rowText, String buttonText) {
        WebElement row = rowByCellText(rowText);
        WebElement button = row.findElement(By.xpath(".//button[normalize-space()=" + quote(buttonText) + "]"));
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    }

    protected void clickRowButtonAndAcceptAlert(String rowText, String buttonText) {
        clickRowButton(rowText, buttonText);
        acceptAlertIfPresent();
    }

    protected void clickRowLink(String rowText, String linkText) {
        WebElement row = rowByCellText(rowText);
        WebElement link = row.findElement(By.xpath(".//a[normalize-space()=" + quote(linkText) + "]"));
        wait.until(ExpectedConditions.elementToBeClickable(link)).click();
    }

    protected String quote(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        StringBuilder sb = new StringBuilder("concat(");
        char[] chars = value.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (i > 0) {
                sb.append(",");
            }

            if (chars[i] == '\'') {
                sb.append("\"'\"");
            } else if (chars[i] == '"') {
                sb.append("'\"'");
            } else {
                sb.append("'").append(chars[i]).append("'");
            }
        }

        sb.append(")");
        return sb.toString();
    }
}