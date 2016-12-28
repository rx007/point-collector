package services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class InitChromeDriver {

    private static final Logger LOGGER                     = Logger.getLogger(InitChromeDriver.class);

    private static final String CHROMEDRIVER_WINDOWS       = "chromedriver.exe";

    private static final String CHROMEDRIVER_MACOSX        = "chromedriverOSX.exe";

    private static final String CHROMEDRIVER_LINUX         = "chromedriverLinux";

    public static final long    DRIVERIMPLICITWAITDURATION = 1;

    /**
     * Shared code to initialize chromedriver
     * 
     * @return
     * @throws Exception
     */
    public WebDriver initDriver() throws Exception {

        try {
            System.setProperty("webdriver.chrome.driver", setChromeDriverByOS());
            Map<String, Object> contentSettings = new HashMap<String, Object>();
            Map<String, Object> preferences = new HashMap<String, Object>();
            preferences.put("profile.default_content_settings", contentSettings);
            ChromeOptions options = new ChromeOptions();
            options.addArguments(Arrays.asList("--ignore-certificate-errors"));
            DesiredCapabilities cap = DesiredCapabilities.chrome();
            cap.setCapability(ChromeOptions.CAPABILITY, options);
            cap.setCapability("chrome.prefs", preferences);
            cap.setCapability("chrome.switches", Arrays.asList("--incognito"));

            WebDriver driver = new ChromeDriver(cap);
            driver.manage().timeouts().implicitlyWait(DRIVERIMPLICITWAITDURATION, TimeUnit.SECONDS);

            return driver;
        } catch (Exception e) {
            LOGGER.error("Unable to create driver..." + e);
            throw e;
        }
    }

    /**
     * Uses system properties to determine OS
     * 
     * @return
     */
    protected static String setChromeDriverByOS() {

        String os = System.getProperty("os.name");
        if (os.contains("Windows"))
            return CHROMEDRIVER_WINDOWS;
        if (os.contains("Mac"))
            return CHROMEDRIVER_MACOSX;
        if (os.contains("Linux"))
            return CHROMEDRIVER_LINUX;
        return null;
    }

    /**
     * opens url while handling unexpected alert messages
     * 
     * @param url
     * @param driver
     */
    protected void navigateToUrl(String url, WebDriver driver) {

        while (isAlertPresent(driver)) {
            driver.switchTo().alert().accept();
        }
        driver.get(url);
        // handle some annoying popup modals... Add here as needed
        if (!driver.findElements(By.xpath("//div[@class='app-promo-content']/img")).isEmpty() ) {
            driver.findElement(By.xpath("//div[@class='app-promo-content']/img")).click();
        }
    }

    /**
     * 
     * @param driver
     * @return
     */
    protected boolean isAlertPresent(WebDriver driver) {

        try {
            driver.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException ex) {
            return false;
        }
    }

    /**
     * 
     * @param driver
     */
    protected static void destroyDriver(WebDriver driver) {

        if (driver != null) {
            driver.quit();
        }
    }

}
