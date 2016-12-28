package services.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Images should be stored in a separate "img" directory so it is easier to configure.
 * Types of images should be separated based on some rule or nomenclature
 * 
 * @author tang.yen
 *
 */
public class InfoseekMailbox extends AbstractPageObject {

    private static final By BY_NOTACQUIREDTAB = By.className("notAcquired");

    private static final By BY_ALLMAILS       = By.xpath("//div[@class='mailbox']/ul/li[contains(@class,'read')]");

    private static final By BY_MAILLINK       = By.xpath(".//div[@class='listCont']/a");

    public InfoseekMailbox(WebDriver driver) {

        super(driver);
        setImplicitWait(10);
    }

    public void clickNotAcquiredTab() {

        driver.findElement(BY_NOTACQUIREDTAB).click();
    }

    public List<WebElement> getAllMails() {

        return driver.findElements(BY_ALLMAILS);
    }

    public InfoseekMail openMailLink(WebElement mailElement) {

        driver.get(mailElement.findElement(BY_MAILLINK).getAttribute("href"));
        return new InfoseekMail(driver);
    }
}
