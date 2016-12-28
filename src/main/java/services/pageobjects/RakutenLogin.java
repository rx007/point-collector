package services.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RakutenLogin extends AbstractPageObject {

    private By userIdField   = By.name("u");

    private By passwordField = By.name("p");

    private By loginButton   = By.className("loginButton");

    public RakutenLogin(WebDriver driver) {

        super(driver);
    }

    public RakutenLogin typeUserId(String username) {

        type(userIdField, username);
        return this;
    }

    public RakutenLogin typePassword(String pass) {

        type(passwordField, pass);
        return this;
    }

    public RakutenTop clickLoginButton() {

        click(loginButton);
        return new RakutenTop(driver);
    }

    public RakutenTop executeLogin(String username, String pass) {

        typeUserId(username);
        typePassword(pass);
        clickLoginButton();
        return new RakutenTop(driver);
    }
}
