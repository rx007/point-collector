package services.pageobjects;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Images should be stored in a separate "img" directory so it is easier to configure.
 * Types of images should be separated based on some rule or nomenclature
 * 
 * @author tang.yen
 *
 */
public class LuckyLots extends AbstractPageObject {

    private static final Logger LOGGER            = Logger.getLogger(LuckyLots.class);
    
    public static final String userAgent         = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:32.0) Gecko/20100101 Firefox/32.0";
    
    public static final String flashVer          = "undefined";
    
    public static final String os                = "MacIntel";

    public LuckyLots(WebDriver driver) {

        super(driver);
    }

    public String play() throws Exception {

        return callLuckyLotsApi(flashVer, os, userAgent);
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
    public String callLuckyLotsApi(String flashVer, String os, String useragent) {
        String charset = "UTF-8";
        String url = "http://www.infoseek.co.jp/Luckylot/result";

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("Host", "www.infoseek.co.jp");
            connection.setRequestProperty("User-Agent", useragent);
            connection.setRequestProperty("Referer", "http://www.infoseek.co.jp/Luckylot");
            connection.setRequestProperty("Cookie", convertSeleniumCookieToCurl(driver));
            InputStream response = connection.getInputStream();
            return IOUtils.toString(response, charset);
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        return null;
    }

}
