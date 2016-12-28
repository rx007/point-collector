package services.pageobjects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class KujiTop extends AbstractPageObject {

    private static final Logger LOGGER = Logger.getLogger(KujiTop.class);

    private By                  kujiBanners;

    public KujiTop(WebDriver driver, By xpath) {

        super(driver);
        kujiBanners = xpath;
    }

    public By getKujiBanners() {

        return kujiBanners;
    }

    public void setKujiBanners(By xpath) {

        kujiBanners = xpath;
    }

    public List<WebElement> getAllKujiBannerElements() {

        return new ArrayList<WebElement>(driver.findElements(kujiBanners));
    }

    public CustomKuji clickBannerByIndex(int index) {

        try {
            driver.get(driver.findElements(kujiBanners).get(index).getAttribute("href"));
            return new CustomKuji(driver);
        } catch (UnhandledAlertException e1) {
            driver.switchTo().alert().accept();
        } catch (TimeoutException e) {
            LOGGER.error("TimeoutException caught. Skipping current kuji..");
        }

        return null;
    }

}
