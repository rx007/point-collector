package services.pageobjects;

import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import services.InitChromeDriver;

/**
 * AbstractPageObject.
 * Mostly used to store the driver (as all page objects need one), and also to define some waiting methods,
 * since Selenium WebDriver library do not have exclusive wait functions which Selenium IDE provides.
 * 
 * @author tang.yen
 * 
 */
public abstract class AbstractPageObject {

    private static final Logger LOGGER                  = Logger.getLogger(AbstractPageObject.class);

    protected final WebDriver   driver;

    protected static final int  ATTEMPTS                = 3;

    protected WebDriverWait     wait;
    
    private static final Integer STRINGBUILDER_INITIAL_SIZE = 512;

    protected static final long LRESPONSETIMEOUTMAXWAIT = InitChromeDriver.DRIVERIMPLICITWAITDURATION;

    public AbstractPageObject(WebDriver driver) {

        this.driver = driver;
        if (driver != null) {
            wait = new WebDriverWait(driver, LRESPONSETIMEOUTMAXWAIT);
        }
    }

    public void setImplicitWait(int seconds) {

        wait = new WebDriverWait(driver, seconds);
    }

    protected String convertSeleniumCookieToCurl(WebDriver driver) {
        // initialize StringBuilder to STRINGBUILDER_INITIAL_SIZE characters to avoid reallocations
        StringBuilder resultBuilder = new StringBuilder(STRINGBUILDER_INITIAL_SIZE);
        Set<Cookie> cookieList = driver.manage().getCookies();
        for (Cookie cookie : cookieList) {
            resultBuilder.append(cookie.getName());
            resultBuilder.append("=");
            resultBuilder.append(cookie.getValue());
            resultBuilder.append("; ");
        }
        // remove the last two character "; " from the end
        resultBuilder.setLength(resultBuilder.length() - 2);
        
        return resultBuilder.toString();
    }
    
    /**
     * Waits until an by is clickable.
     * 
     * @param by
     */
    protected void waitUntilClickable(By by) {

        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * Waits until by is visible. 'Visible' is defined as an by with a width and height greater than 0.
     * 
     * @param by
     */
    protected void waitUntilVisible(By by) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Provides extra checking to make sure typing into field will not yield unexpected results.
     * 
     * @param by
     *            the by to type "text" into
     * @param text
     *            the text to fill in
     */
    protected void type(By by, String text) {

        int i = 0;
        while (i < ATTEMPTS) {
            try {
                driver.findElement(by).clear();
                driver.findElement(by).sendKeys(text);
                break;
            } catch (StaleElementReferenceException e) {
                LOGGER.debug(String.format("Stale element reference detected on type operation: Retries: %d/%d", ++i, ATTEMPTS));
                if (i == ATTEMPTS)
                    throw e;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("Sleep exception", e1);
                }
            }
        }
    }

    /**
     * Provides extra checking to make sure the by can be clicked before performing the action.
     * 
     * @param by
     */
    protected void click(By by) {

        int i = 0;
        while (i < ATTEMPTS) {
            try {
                waitUntilVisible(by);
                waitUntilClickable(by);
                driver.findElement(by).click();
                break;
            } catch (StaleElementReferenceException e) {
                LOGGER.debug(String.format("Stale element reference detected on click operation: Retries: %d/%d", ++i, ATTEMPTS));
                if (i == ATTEMPTS)
                    throw e;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("Sleep exception", e1);
                }
            } catch (UnhandledAlertException e1) {
                driver.switchTo().alert().accept();
                driver.findElement(by).click();
            }
        }
    }

    protected void openUrl(String url) {

        try {
            driver.get(url);
        } catch (UnhandledAlertException e1) {
            driver.switchTo().alert().accept();
            driver.get(url);
        }
    }

}
