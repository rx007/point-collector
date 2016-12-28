package services.card;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import services.InitChromeDriver;
import services.LoginDialog;
import services.pageobjects.RakutenLogin;

public class Card extends InitChromeDriver implements Runnable {

    private static final Logger LOGGER          = Logger.getLogger(Card.class);

    private static final String CARD_CLICK_LINK = "https://www.rakuten-card.co.jp/e-navi/members/point/click-point/index.xhtml";

    private static WebDriver    driver;

    static {
        // Initialize Logger
        DOMConfigurator.configure("log4j.xml");
        LOGGER.setLevel(Level.DEBUG);
    }

    public Card(WebDriver browser) {
        driver = browser;
    }

    public Card() {
        try {
            driver = initDriver();
        } catch (Exception e) {
            LOGGER.error("Problem initializing Card driver: ", e);
        }
    }

    /**
     * 
     * @param username
     * @param password
     * @throws Exception
     */
    public void execute(String username, String password) throws Exception {

        navigateToUrl(CARD_CLICK_LINK, driver);
        RakutenLogin rakutenLogin = new RakutenLogin(driver);
        rakutenLogin.executeLogin(username, password);
        if ("https://grp01.id.rakuten.co.jp/rms/nid/vc".equals(driver.getCurrentUrl())) {
            LOGGER.error("Unable to log in. Please check your login credentials.");
            return;
        }

        List<WebElement> rows = driver.findElements(By.xpath("//*[contains(@id,'position')]"));
        int numUnclickedLinks = driver.findElements(By.xpath("//*[contains(@id,'position')]//img[@alt='Check']")).size();
        LOGGER.info(String.format("%s potential link[s] found.", numUnclickedLinks));
        if (numUnclickedLinks == 0) {
            LOGGER.info("No fresh links to click today. Exiting...");
            return;
        }

        // the meat of the program
        int clickedLinksCounter = 0;
        for (int i = 0; i < rows.size(); i++) {
            // if "check" image exists
            if (!rows.get(i).findElements(By.xpath(".//img[@alt='Check']")).isEmpty()) {
                rows.get(i).findElement(By.tagName("a")).click();
                Thread.sleep(3000);
                clickedLinksCounter++;
            }
            rows = driver.findElements(By.xpath("//*[contains(@id,'position')]"));
        }
        LOGGER.info(String.format("%s link[s] were clicked (%s point[s] will be obtained three days from today).", clickedLinksCounter, clickedLinksCounter));
    }

    @Override
    public void run() {

        try {
            if (driver == null) {
                driver = initDriver();
            }
            execute(LoginDialog.getUsername(), LoginDialog.getPassword());
        } catch (Exception e) {
            LOGGER.error("Exception caught during run: ", e);
        }
        LOGGER.info("Finished running Card.");
        destroyDriver(driver);
    }

}
