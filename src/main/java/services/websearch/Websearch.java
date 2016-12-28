package services.websearch;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import services.InitChromeDriver;
import services.LoginDialog;
import services.kuji.Kuji;
import services.pageobjects.RakutenLogin;

public class Websearch extends InitChromeDriver implements Runnable {

    private static final Logger LOGGER               = Logger.getLogger(Websearch.class);

    private static final String WEBSEARCH_CLICK_LINK = "https://websearch.rakuten.co.jp/login.html?tool_id=1&tp=r_navi";

    private static WebDriver    driver;

    static {
        // Initialize Logger
        DOMConfigurator.configure("log4j.xml");
        LOGGER.setLevel(Level.DEBUG);

    }

    public Websearch(WebDriver browser) {
        driver = browser;
    }

    public Websearch() {
        try {
            driver = initDriver();
        } catch (Exception e) {
            LOGGER.error("Error initializing Websearch driver", e);
        }
    }

    @Override
    public WebDriver initDriver() throws Exception {

        try {
            System.setProperty("webdriver.chrome.driver", setChromeDriverByOS());
            Map<String, Object> contentSettings = new HashMap<String, Object>();
            Map<String, Object> preferences = new HashMap<String, Object>();
            preferences.put("profile.default_content_settings", contentSettings);
            ChromeOptions options = new ChromeOptions();
            options.addArguments(Arrays.asList("--ignore-certificate-errors"));
            // load websearch extension
            options.addArguments(String.format("load-extension=%s", createWebsearchPath()));

            DesiredCapabilities cap = DesiredCapabilities.chrome();
            cap.setCapability(ChromeOptions.CAPABILITY, options);
            cap.setCapability("chrome.prefs", preferences);
            cap.setCapability("chrome.switches", Arrays.asList("--incognito"));

            WebDriver d = new ChromeDriver(cap);
            d.manage().timeouts().implicitlyWait(DRIVERIMPLICITWAITDURATION, TimeUnit.SECONDS);

            return d;
        } catch (Exception e) {
            LOGGER.error("Unable to create driver..." + e);
            throw e;
        }
    }

    private String createWebsearchPath() {

        try {
            String result = String.format("%s%s", Websearch.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace("point_collector.jar", ""), "Rwebsearch/");
            // Remove Leading "/" if program determines OS is Windows
            String os = System.getProperty("os.name");
            LOGGER.debug(os);
            if (os.contains("Windows"))
                result = result.trim().substring(1);
            
            LOGGER.debug(String.format("Extension path: %s", result));
            return result;
        } catch (URISyntaxException e) {
            LOGGER.error("Cannot locate websearch plugin", e);
        }

        return null;
    }

    public void execute(String username, String password) throws Exception {

        navigateToUrl(WEBSEARCH_CLICK_LINK, driver);
        RakutenLogin rakutenLogin = new RakutenLogin(driver);
        rakutenLogin.executeLogin(username, password);
        if ("https://grp03.id.rakuten.co.jp/rms/nid/vc".equals(driver.getCurrentUrl())) {
            LOGGER.error("Unable to log in. Please check your login credentials.");
            return;
        }

        String currentHandle = driver.getWindowHandle();
        // closes all other windows
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }

        driver.switchTo().window(currentHandle);
        // hard wait for loading bar to transfer to actual page
        while (driver.findElements(By.id("sf_qt")).isEmpty()) {
            Thread.sleep(1000);
        }

        List<String> keywords = WebsearchConf.getKeywords();
        for (String keyword : keywords) {
            if (driver.findElement(By.className("current-num")).getText().contains("30")) {
                LOGGER.info("Max amount of searches reached. Exiting program");
                return;
            }
            driver.findElement(By.id("sf_qt")).sendKeys(keyword);
            driver.findElement(By.id("sBtn")).click();
            Thread.sleep(5000);
            driver.get("http://websearch.rakuten.co.jp/SimpleTop?qt=&col=OW");
        }

    }

    @Override
    public void run() {

        try {
            if (driver == null) {
                driver = initDriver();
            }
            execute(LoginDialog.getUsername(), LoginDialog.getPassword());
        } catch (Exception e) {
            LOGGER.error("Error caught in run of websearch", e);
        }
        LOGGER.info("Finished running Websearch.");
        destroyDriver(driver);
    }

    public static void main(String[] args) throws Exception {

        LoginDialog.setUsername("");
        LoginDialog.setPassword("");

        new Websearch().run();
    }

}
