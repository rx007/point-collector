package services.pageobjects;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Images should be stored in a separate "img" directory so it is easier to configure.
 * Types of images should be separated based on some rule or nomenclature
 * 
 * @author tang.yen
 *
 */
public class Kezulots extends AbstractPageObject {

    private static final Logger LOGGER            = Logger.getLogger(Kezulots.class);
    
    public static final String userAgent         = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:32.0) Gecko/20100101 Firefox/32.0";
    
    public static final String flashVer          = "undefined";
    
    public static final String os                = "MacIntel";
    
    private static final By BY_CHKELEMENT        = By.id("sk");

    public Kezulots(WebDriver driver) {

        super(driver);
    }

    public String play() throws Exception {

        String chkKey = getChkKey();
        return callKezuLotsApi(chkKey, flashVer, os, userAgent);
    }
    
    /**
     * cryptedId = id of the kuji
     * memberKey = unique member ID (key)
     * apiType:
     *     "decide" <-- to determing outcome of the kuji
     *     "accept" <-- to "accept" and register the decision
     * flashVer  = flash version
     * os        = os type and version
     * useragent = user agent of browser
     * @param type
     * @return result
     */
    public String callKezuLotsApi(String chkKey, String flashVer, String os, String useragent) {
        String charset = "UTF-8";
        String url = "http://point.infoseek.co.jp/";
        String apiParams = "md=%s&ac=%s&play=1&chkKey=%s&%s";

        try {
            String builtParams = String.format( apiParams
                          , URLEncoder.encode("KezuLots", charset)
                          , URLEncoder.encode("KezuLotsIntro", charset)
                          , URLEncoder.encode(chkKey, charset)
                          , URLEncoder.encode(String.valueOf((int)Math.floor(Math.random()*10000000)), charset)
                          );
            URLConnection connection = new URL(url + "?" + builtParams).openConnection();
            connection.setRequestProperty("Host", "point.infoseek.co.jp");
            connection.setRequestProperty("User-Agent", useragent);
            connection.setRequestProperty("Referer", "http://point.infoseek.co.jp/point/?md=KezuLots&ac=KezuLotsIntro");
            connection.setRequestProperty("Cookie", convertSeleniumCookieToCurl(driver));
            InputStream response = connection.getInputStream();
            return IOUtils.toString(response, charset);
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * gets ID of the flash game used for API call
     * @param unparsedString
     * @return
     */
    private String getChkKey() {
        return driver.findElement(BY_CHKELEMENT).getAttribute("value");
    }

}
