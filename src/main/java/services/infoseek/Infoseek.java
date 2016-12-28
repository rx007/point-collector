package services.infoseek;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import services.InitChromeDriver;
import services.LoginDialog;
import services.pageobjects.CustomKuji;
import services.pageobjects.InfoseekMail;
import services.pageobjects.InfoseekMailbox;
import services.pageobjects.Kezulots;
import services.pageobjects.LuckyLots;
import services.pageobjects.RakutenLogin;
import services.pageobjects.RakutenTop;

public class Infoseek extends InitChromeDriver implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Infoseek.class);

    private static WebDriver    driver;

    static {
        // Initialize Logger
        DOMConfigurator.configure("log4j.xml");
        LOGGER.setLevel(Level.DEBUG);
    }

    public Infoseek(WebDriver browser) {
        driver = browser;
    }

    public Infoseek() {
        try {
            driver = initDriver();
        } catch (Exception e) {
            LOGGER.error("Error creating Infoseek Driver", e);
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
    
    /**
     * Gets point from searching via point page's search bar (once daily)
     */
    public void executeSearchItemViaPoint() {

    	navigateToUrl("http://point.infoseek.co.jp/", driver);
        try {
        	WebElement searchField = driver.findElement(By.className("inputWordTop"));
            searchField.sendKeys("ダーツ");
            searchField.submit();
            
            // Already received points
            if (driver.findElements(By.xpath("//img[@alt='ログインして補助券GET！']")).size() > 0) {
            	LOGGER.debug("ログインして補助券GET！ detected. Earned 1 point.");
            }
            else if (driver.findElements(By.xpath("//img[@alt='本日の補助券獲得済み！']")).size() > 0) {
            	LOGGER.warn("Points already received via Point Group item search.");
            }
            
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        LOGGER.info("Infoseek: Finished Execute Search Item Via Point Site.");
    }

    /**
     * Gets points from Mail de Point service
     */
    public void executeMailPoint() {

        try {
            InfoseekMailbox infoseekMailbox = navigateToNotAcquiredMailbox();
            List<WebElement> mailList = infoseekMailbox.getAllMails();
            LOGGER.info(String.format("Number of unclaimed point mails: %d", mailList.size()));

            for (int mailIndex = 0; mailIndex < mailList.size(); mailIndex++) {
                InfoseekMail infoseekMail = infoseekMailbox.openMailLink(mailList.get(mailIndex));
                if (infoseekMail.clickAllPromotionLinks(driver.getCurrentUrl()) == 0) {
                    mailIndex = -1;
                }
                infoseekMailbox = navigateToNotAcquiredMailbox();
                mailList = infoseekMailbox.getAllMails();
            }
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        LOGGER.info("Infoseek: Finished Mail de Point.");
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    private InfoseekMailbox navigateToNotAcquiredMailbox() throws Exception {

        navigateToUrl("https://member.pointmail.rakuten.co.jp/box/", driver);
        InfoseekMailbox infoseekMailbox = new InfoseekMailbox(driver);
        infoseekMailbox.clickNotAcquiredTab();
        Thread.sleep(2000);
        return infoseekMailbox;
    }

    private void executeLuckyLot() throws Exception {
        
        navigateToUrl("http://www.infoseek.co.jp/Luckylot", driver);
        LuckyLots luckyLots = new LuckyLots(driver);
        luckyLots.play();
        
        LOGGER.info("Infoseek: Finished playing Lucky Lot.");
    }
    
    /**
     * Executes lucky kuji
     */
    public void executeLuckyKuji() {

        try {
            navigateToUrl("http://www.rakuten.co.jp/", driver);
            // before clicking on link, scroll to bottom of page to avoid the annoying bottom banner.
            JavascriptExecutor js = ((JavascriptExecutor) driver);
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            driver.findElement(By.linkText("楽天ラッキーくじ")).click();
            CustomKuji customKuji = new CustomKuji(driver);
            customKuji.play(customKuji.getKujiIds());
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        LOGGER.info("Infoseek: Finished playing lucky kuji.");
    }

    /**
     * Executes Bingo
     * @throws InterruptedException
     */
    public void executeBingo() throws InterruptedException {

        navigateToUrl("http://isbingo.www.infoseek.co.jp/", driver);
        driver.findElement(By.id("bingoStartBtn")).click();
        new RakutenLogin(driver).executeLogin(LoginDialog.getUsername(), LoginDialog.getPassword());
        int count = 0;
        while (!"http://www.infoseek.co.jp/".equals(driver.getCurrentUrl())) {
            Thread.sleep(5000);
            if (++count > 60) {
            	LOGGER.error("Playing bingo timed out (300 seconds)...");
            	return;
            }
        }
        LOGGER.info("Infoseek: Finished playing bingo.");
    }

    /**
     * Gets Vouchers after all 5 requirements for Infoseek has been completed.
     * Vouchers can be used to play the "KezuLots" game:
     * http://point.infoseek.co.jp/point/?md=KezuLots&ac=KezuLotsIntro
     * @throws Exception
     */
    public void executeGetVouchers() throws Exception {

        navigateToUrl("http://point.infoseek.co.jp/", driver);
        try {
            driver.findElement(By.linkText("ログイン")).click();
        } catch (NoSuchElementException e) {}
        navigateToUrl("http://point.infoseek.co.jp/point/?md=Point&ac=Complete", driver);
        driver.findElement(By.xpath("//a[@href='javascript:buttonLoad();']")).click();
        // Seems that it takes some time to register. Wait 10 seconds.
        Thread.sleep(10000);
        LOGGER.info("Infoseek: Finished getting vouchers.");
    }
    
    public void executeKezulots() throws Exception {
        navigateToUrl("http://point.infoseek.co.jp/",driver);
        try {
            driver.findElement(By.linkText("ログイン")).click();
        } catch (NoSuchElementException e) { }
        navigateToUrl("http://point.infoseek.co.jp/point/?md=KezuLots&ac=KezuLotsIntro", driver);
        int numTimes = Integer.parseInt(driver.findElement(By.id("u_thisScratch")).getText());
        LOGGER.info(String.format("Infoseek: You have enough tickets to play KezuLots %d times.", numTimes));
        Kezulots kezulots = new Kezulots(driver);
        for (int i=0;i < numTimes; i++) {
            LOGGER.debug(kezulots.play().trim());
        }
        LOGGER.info("Infoseek: Finished playing scratchcard (KezuLots).");
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
            executeSearchItemViaPoint();
            executeMailPoint();
            executeLuckyKuji();
            executeBingo();
            executeLuckyLot();
            executeGetVouchers();
            executeKezulots();

        } catch (Exception e) {
            LOGGER.error("Exception caught in run execution: ", e);
        }
        LOGGER.info("Finished running Infoseek.");
        destroyDriver(driver);
    }

    public static void main(String[] args) throws Exception {

        LoginDialog.setUsername("");
        LoginDialog.setPassword("");

        new Infoseek().run();
    }
}
