package framework.driver;

// Selenium WebDriver
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

// Automation framework
import framework.utilities.FW_ConfigMgr;
import framework.assertions.FW_CustomAssertJU;

/**
 * The Browser class is a utility class that provides a method to create a WebDriver instance
 * based on the browser type and headless mode specified in the test configuration file.
 */
public class FW_Browser {

    /**
     * Returns a WebDriver instance based on the browser type and headless mode specified.
     *
     * @param browser  the browser type (chrome, firefox, safari)
     * @param headless the headless mode (true, false)
     * 
     * @return a WebDriver instance
     */
    public static WebDriver createWebDriver() {

        // Get the browser name and headless options from testConfig.properties
        String browser = FW_ConfigMgr.getBrowser().toLowerCase();
        String headless = FW_ConfigMgr.getHeadless().toLowerCase();
        
        WebDriver driver;
        if ("chrome".equals(browser)) {
            ChromeOptions options = new ChromeOptions();
            if ("true".equals(headless)) options.addArguments("--headless");
            driver = new ChromeDriver(options);
        } else if ("firefox".equals(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            if ("true".equals(headless)) options.addArguments("--headless");
            driver = new FirefoxDriver(options);
        } else if ("safari".equals(browser)) {
            SafariOptions options = new SafariOptions();
            // Safari doesn't support headless mode as of now
            driver = new SafariDriver(options);
        } else {
            // Default to Chrome if no matching browser is found
            ChromeOptions options = new ChromeOptions();
            if ("true".equals(headless)) options.addArguments("--headless");
            driver = new ChromeDriver(options);
        }
        FW_CustomAssertJU.setDriver(driver);
        return driver;
    }

    /**
     * Quits the WebDriver instance, effectively closing the browser window.
     *
     * @param driver the WebDriver instance to quit
     */
    public static void disposeWebDriver(WebDriver driver) {
        // Quit the driver, effectively closing the browser window
        if (driver != null) {
            driver.quit();
        }
    }

}
