package services.pageobjects;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Images should be stored in a separate "img" directory so it is easier to configure.
 * Types of images should be separated based on some rule or nomenclature
 * 
 * @author tang.yen
 *
 */
public class InfoseekMail extends AbstractPageObject {

    private static List<String> mailIdProcessed      = new ArrayList<String>();

    private static final By     BY_ALLPROMOTIONLINKS = By.xpath("//a[contains(@href,'pmrd.rakuten.co.jp')]");

    public InfoseekMail(WebDriver driver) {

        super(driver);
        setImplicitWait(5);
    }

    public int clickAllPromotionLinks(String currentUrl) {

        String mailId = getMailId(currentUrl);
        if (mailIdProcessed.contains(mailId)) {
            return 1;
        }

        openUrl(currentUrl);
        List<WebElement> promoLinks = driver.findElements(BY_ALLPROMOTIONLINKS);

        String rootWindowId = driver.getWindowHandle();
        for (int linkIndex = 0; linkIndex < promoLinks.size(); linkIndex++) {
            Actions actions = new Actions(driver);
            actions.moveToElement(promoLinks.get(linkIndex)).click().perform();
            driver.switchTo().window(rootWindowId);
        }

        // when done, remove all other windows opened
        for (String window : driver.getWindowHandles()) {
            if (!window.equals(rootWindowId)) {
                driver.switchTo().window(window);
                driver.close();
            }
        }
        driver.switchTo().window(rootWindowId);
        mailIdProcessed.add(mailId);

        return 0;
    }

    public String getMailId(String url) {

        String result = url.split("\\?")[1];
        result = result.split("&")[0];
        result = result.substring(9);
        return result;
    }

}
