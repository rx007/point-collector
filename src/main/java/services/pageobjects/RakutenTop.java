package services.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RakutenTop extends AbstractPageObject {

    private static final String KUJILINKURL      = "https://kuji.rakuten.co.jp/";

    private static final By     KUJILINKELEMENTS = By.xpath("//ul[@id='all-kuji-list']//li/a");

    private By                  loginButton      = By.className("mr-login-btn");

    private By                  logoutButton     = By.className("mr-logout-btn");

    public RakutenTop(WebDriver driver) {

        super(driver);
    }

    public RakutenLogin clickLoginButton() {

        click(loginButton);
        return new RakutenLogin(driver);
    }

    public RakutenTop clickLogoutButton() {

        click(logoutButton);
        return this;
    }

    public KujiTop navigateToKujiLink() {

        driver.get(KUJILINKURL);
        return new KujiTop(driver, KUJILINKELEMENTS);
    }
}
