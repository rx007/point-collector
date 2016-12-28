package services.pageobjects;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/**
 * Images should be stored in a separate "img" directory so it is easier to configure.
 * Types of images should be separated based on some rule or nomenclature
 * 
 * @author tang.yen
 *
 */
public class CustomKuji extends AbstractPageObject {

    private static final Logger LOGGER    = Logger.getLogger(CustomKuji.class);

    public static final String  USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:32.0) Gecko/20100101 Firefox/32.0";

    public static final String  FLASHVER  = "undefined";

    public static final String  OS        = "MacIntel";

    private static String       memberKey;

    public CustomKuji(WebDriver driver) {

        super(driver);
    }

    public void play(String kujiInfo) throws Exception {

        // return immediately if kuji is either expired or already played
        if (driver.getCurrentUrl().contains("close") || driver.getCurrentUrl().contains("already")
                        || kujiInfo.isEmpty() || kujiInfo == null) {
            return;
        }
        String cryptedId = getCryptedId(kujiInfo);
        memberKey = getMemberKey(kujiInfo);
        String response = callKujiApi(cryptedId, memberKey, "decide", FLASHVER, OS, USERAGENT);
        // LOGGER.info(String.format("Url: %s | response: %s", driver.getCurrentUrl(), response));
        if (!wonKuji(response)) {
            LOGGER.info("[LOSE/ERROR] -> " + response);
        } else {
            LOGGER.info("[WON] -> " + response);
        }
        callKujiApi(cryptedId, memberKey, "accept", FLASHVER, OS, USERAGENT);
    }

    public boolean wonKuji(String response) {

        if (response.contains("win")) {
            return true;
        }
        return false;
    }

    /**
     * cryptedId = id of the kuji
     * memberKey = unique member ID (key)
     * apiType:
     * "decide" <-- to determining outcome of the kuji
     * "accept" <-- to "accept" and register the decision
     * FLASHVER = flash version
     * os = OS type and version
     * useragent = user agent of browser
     * 
     * @param type
     * @return result
     */
    public String callKujiApi(String cryptedId, String memberKey, String apiType, String flashVer, String os, String useragent) {

        String charset = "UTF-8";
        String url = "https://kuji.rakuten.co.jp/%s/%s/%s";
        String apiParams = "flash_version=%s&os=%s&useragent=%s";

        try {
            String builtUrl = String.format(url, URLEncoder.encode(cryptedId, charset), URLEncoder.encode(memberKey, charset), URLEncoder.encode(apiType, charset));
            String builtParams = String.format(apiParams, URLEncoder.encode(flashVer, charset), URLEncoder.encode(os, charset), URLEncoder.encode(useragent, charset));
            URLConnection connection = new URL(builtUrl + "?" + builtParams).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            return IOUtils.toString(response, charset);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Should return something like:
     * crypted_id=8ea1d408aa&member_key=Sno2VERuT0J6L2VIemNnVm5BcDdqQT09Cg==
     * 
     * @return unparsedString
     */
    public String getKujiIds() {
        try {
            return driver.findElement(By.name("FlashVars")).getAttribute("value");
        } catch (NoSuchElementException e) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String format = "crypted_id=%s&member_key=%s";
                String crypted_id = (String) js.executeScript("return c");
                String member_key = (String) js.executeScript("return m");
                return String.format(format, crypted_id, member_key);
            } catch (WebDriverException e1) {
                return "";
            }
        }
    }

    /**
     * gets member_key associated with the account.
     * 
     * @param unparsedString
     * @return
     */
    private String getMemberKey(String unparsedString) {

        String memberString = unparsedString.split("&")[1];
        // "member_key=" is 11 characters, so get from 12th (11th index)
        return memberString.substring(11);
    }

    /**
     * gets ID of the kuji used for API call
     * 
     * @param unparsedString
     * @return
     */
    private String getCryptedId(String unparsedString) {

        String memberString = unparsedString.split("&")[0];
        // "crypted_id=" is 11 characters, so get from 12th (11th index)
        return memberString.substring(11);
    }

    public String getMasterMemberKey() {

        return memberKey;
    }

    public void setMasterMemberKey(String key) {

        memberKey = key;
    }

}
