package services.kuji;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import services.InitChromeDriver;
import services.LoginDialog;
import services.pageobjects.CustomKuji;
import services.pageobjects.KujiTop;
import services.pageobjects.RakutenLogin;
import services.pageobjects.RakutenTop;

public class Kuji extends InitChromeDriver implements Runnable {

    private static final Logger LOGGER            = Logger.getLogger(Kuji.class);

    private static WebDriver    driver;

    private static final By     POSSIBLEKUJILINKS = By.xpath("//a[contains(@href,'v6.advg')]");

    static {
        // Initialize Logger
        DOMConfigurator.configure("log4j.xml");
        LOGGER.setLevel(Level.DEBUG);
    }

    public Kuji(WebDriver browser) {
        driver = browser;
    }

    public Kuji() {
        try {
            driver = initDriver();
        } catch (Exception e) {
            LOGGER.error("Error initializing Kuji driver", e);
        }
    }

    public boolean login(String url, String username, String password) {

        navigateToUrl(url, driver);
        RakutenTop rakutenTop = new RakutenTop(driver);
        RakutenLogin rakutenLogin = rakutenTop.clickLoginButton();
        rakutenTop = rakutenLogin.executeLogin(username, password);
        if ("https://grp01.id.rakuten.co.jp/rms/nid/vc".equals(driver.getCurrentUrl())) {
            LOGGER.error("Unable to log in. Please check your login credentials.");
            return false;
        }
        return true;
    }

    public void execute() {

        RakutenTop rakutenTop = new RakutenTop(driver);
        KujiTop kujiTop = rakutenTop.navigateToKujiLink();

        int numBanners = kujiTop.getAllKujiBannerElements().size();
        try {
            for (int index = 0; index < numBanners; index++) {
                kujiTop = playKuji(kujiTop, index);
                numBanners = kujiTop.getAllKujiBannerElements().size();
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while executing Kuji", e);
        }

        navigateToUrl("http://point.infoseek.co.jp/tie-up/kuji/?scid=su_53", driver);
        kujiTop = new KujiTop(driver, By.xpath("//div[@class='section-lv2']//a"));

        try {
            numBanners = kujiTop.getAllKujiBannerElements().size();
            for (int index = 0; index < numBanners; index++) {
                kujiTop = playKuji(kujiTop, index);
                numBanners = kujiTop.getAllKujiBannerElements().size();
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred recursive searching kuji", e);
        }

    }

    public KujiTop playKuji(KujiTop kujiTop, int index) {

        String topUrl = driver.getCurrentUrl();
        CustomKuji customKuji = kujiTop.clickBannerByIndex(index);
        try {
            String kujiInfoUnparsed = customKuji.getKujiIds();
            if (kujiInfoUnparsed != null && !kujiInfoUnparsed.isEmpty()) {
                customKuji.play(kujiInfoUnparsed);
            } else {
                crawlForKujiLinks(POSSIBLEKUJILINKS, customKuji);
            }
        } catch (Exception e) {
            LOGGER.error("Playing Kuji caused exception.", e);
        }
        navigateToUrl(topUrl, driver);
        return new KujiTop(driver, kujiTop.getKujiBanners());
    }

    private void crawlForKujiLinks(By by, CustomKuji customKuji) throws Exception {

        // Maybe page contains separate link to kuji. Search and follow
        try {
            List<WebElement> links = new ArrayList<WebElement>(driver.findElements(by));
            if (!links.isEmpty()) {
                String curPage = driver.getCurrentUrl();
                if ("https://kuji.rakuten.co.jp/".equals(driver.getCurrentUrl()))
                    return;
                for (int j = 0; j < links.size(); j++) {
                    navigateToUrl(links.get(j).getAttribute("href"), driver);
                    customKuji.play(customKuji.getKujiIds());
                    // avoids StaleElementReferenceException
                    navigateToUrl(curPage, driver);
                    links = new ArrayList<WebElement>(driver.findElements(POSSIBLEKUJILINKS));
                }
            }
        } catch (UnhandledAlertException e) {
            driver.switchTo().alert().accept();
        }
    }

    @Override
    public void run() {

        try {
            if (driver == null) {
                driver = initDriver();
            }

            if (!login("http://www.rakuten.co.jp", LoginDialog.getUsername(), LoginDialog.getPassword())) {
                return;
            }
            execute();

            // run kuji from list in file first
            String line;
            try {
                String filepath = Kuji.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace("point_collector.jar", "kujiIdList.txt");
                BufferedReader br = new BufferedReader(new FileReader(filepath));
                while ((line = br.readLine()) != null) {
                    navigateToUrl("https://kuji.rakuten.co.jp/" + line, driver);
                    CustomKuji customKuji = new CustomKuji(driver);
                    customKuji.play(customKuji.getKujiIds());
                }
                br.close();
            } catch (FileNotFoundException e) {
                LOGGER.warn("kuji ID file does not exist: kujiIdList.txt", e);
            } catch (Exception e1) {
                LOGGER.error("Exception occurred processing Kuji in file.", e1);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("Finished running Kuji.");
        destroyDriver(driver);
    }

    public static void main(String[] args) throws Exception {

        LoginDialog.setUsername("");
        LoginDialog.setPassword("");

        new Kuji().run();
    }

}
