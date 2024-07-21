package framework.automation;

import java.time.Instant;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.nio.file.Files;

import framework.utilities.FW_ConfigMgr;
import framework.utilities.FW_ReportUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;

/**
 * This class extends FW_Driver and provides additional methods for interacting
 * with a web page.
 */
public class FW_Page extends FW_Driver {
    WebDriver driver;

    /**
     * Constructs a new FW_Page with the specified WebDriver.
     *
     * @param driver the WebDriver to be used
     */
    public FW_Page(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    /**
     * Returns a set containing all the cookies.
     * 
     * @return A Set of all the cookies.
     */
    public Set<Cookie> getAllCookies() {
        var cookies = driver.manage().getCookies();
        return cookies;
    }

    /**
     * Delete all the cookies and returns a string indicating the result of the
     * operation.
     *
     * @return A string containing the result of the operation.
     */
    public String deleteAllCookies() {
        try {
            var manage = driver.manage();
            manage.deleteAllCookies();
            return "[Pass] - Deleted all cookies.";
        } catch (Exception e) {
            return "[Fail] - Failed to delete all cookies.";
        }
    }

    /**
     * Set a cookie.
     *
     * @param cookieName  The cookie name to set.
     * @param cookieValue The value to set the named cookie.
     * 
     * @return A string containing the result of the operation.
     */
    public String setCookie(String cookieName, String cookieValue) {
        try {
            var cookie = new Cookie.Builder(cookieName, cookieValue).build();
            driver.manage().addCookie(cookie);
            return "[Pass] - Set cookie '" + cookieName + "' to value '" + cookieValue + "'.";
        } catch (Exception e) {
            return "[Fail] - Could not set cookie '" + cookieName + "' to value '" + cookieValue + "'.";
        }
    }

    /**
     * Navigates to a URL and returns a string indicating the result of the
     * operation.
     *
     * @param url The URL to load.
     * @return A string containing the result of the operation.
     */
    public String navigateTo(String url) {
        try {
            boolean result = loadUrl(url);
            if (result == true) {
                return "[Pass] - Navigated to URL: '" + url + "'.";
            } else {
                return "[Fail] - Could not navigate to URL: '" + url + "'.";
            }
        } catch (Exception e) {
            return "[Fail] - An error occurred: " + e.getMessage();
        }
    }

    /**
     * Validates that the URL contains the specified string. Useful for verifying environment.
     *
     * @param withinURL The string to check for existance within the URL.
     * 
     * @return A string containing the result of the operation.
     */
    public String validateWithinCurrentURL(String withinURL) {

        String currentURL = driver.getCurrentUrl();

        if (currentURL.contains(withinURL)) {
            return "[Pass] - '" + withinURL + "' found within URL '" + currentURL + "'.";
        } else {
            return "[Fail] - '" + withinURL + "' not found within URL '" + currentURL + "'.";
        }

    }

    /**
     * Validates that the locator exists on the page and returns a string indicating
     * the result of the operation.
     *
     * @param locator  The XPath locator to check for.
     * @param timeout  The number of seconds to wait before timing out.
     * @param interval The number of seconds to wait between checks.
     * 
     * @return A string containing the result of the operation.
     */
    public String validateLocatorExists(String locator, int timeout, int interval) {
        boolean result = waitToExist(locator, timeout, interval);
        if (result == true) {
            return "[Pass] - Validated locator: '" + locator + "' exists.";
        } else {
            return "[Fail] - Validated locator: '" + locator + "' does not exist.";
        }
    }

    /**
     * Validates that the locator does not exist on the page and returns a string
     * indicating
     * the result of the operation.
     *
     * @param locator  The XPath locator to check does not exist.
     * @param timeout  The number of seconds to wait before timing out.
     * @param interval The number of seconds to wait between checks.
     * 
     * @return A string containing the result of the operation.
     */
    public String validateLocatorNotExists(String locator, int timeout, int interval) {
        boolean result = waitToNotExist(locator, timeout, interval);
        if (result == true) {
            return "[Pass] - Validated locator: '" + locator + "' does not exists.";
        } else {
            return "[Fail] - Validated locator: '" + locator + "' exist when it should not.";
        }
    }

    /**
     * Validates if an alert exists on the page.
     *
     * @param timeout  The number of seconds to wait for alert to appear.
     * @param interval The number of seconds to wait between checks.
     * 
     * @return A string containing the result of the operation.
     */
    public String validateAlertExists(int timeout, int interval) {
        boolean result = waitForAlertToExist(timeout, interval);
        if (result == true) {
            return "[Pass] - Alert was present.";
        } else {
            return "[Fail] - Alert was never present";
        }
    }

    /**
     * Validates if an alert does not exist on the page.
     *
     * @param timeout  The number of seconds to wait for alert to disappear.
     * @param interval The number of seconds to wait between checks.
     * 
     * @return A string containing the result of the operation.
     */
    public String validateAlertDoesntExist(int timeout, int interval) {
        boolean result = waitForAlertToNotExist(timeout, interval);
        if (result == true) {
            return "[Pass] - An alert was not present.";
        } else {
            return "[Fail] - An alert was present even after timeout.";
        }
    }

    /**
     * Get the text from a locator
     *
     * @param locator  The XPath locator to check for.
     * @param timeout  The number of seconds to wait before timing out.
     * @param interval The number of seconds to wait between checks.
     * 
     * @return A string containing the result of the operation.
     */
    public String getLocatorText(String locator, int timeout, int interval) {
        boolean result = waitToExist(locator, timeout, interval);
        if (result == true) {

            String attributeValue = valueFromLocatorAttribute(locator, "value");
            String textValue = textFromLocator(locator);

            boolean hasText = textValue != null && !textValue.isEmpty();
            boolean hasAttribute = attributeValue != null && !attributeValue.isEmpty();

            if (hasText && hasAttribute) {
                return textValue;
            } else if (hasText) {
                return textValue;
            } else if (hasAttribute) {
                return attributeValue;
            } else {
                return "";
            }

        } else {
            return "";
        }
    }

    /**
     * Validates if the given search string is present within the text at the specified locator.
     *
     * @param locator The locator to check.
     * @param search The string to search for within the text at the locator.
     * @param timeout The maximum time to wait for the locator to exist.
     * @param interval The interval at which to check if the locator exists.
     * @param verbose If true, the full text found at the locator will be included in the result.
     *
     * @return A string indicating whether the search string was found within the text at the locator.
     * If the locator does not exist, a failure message is returned.
     */
    public String validateWithinLocatorText(String locator, String search, int timeout, int interval, boolean verbose) {
        boolean result = waitToExist(locator, timeout, interval);
        if (result == true) {

            var startingTime = Instant.now().getEpochSecond();
            String locatorText = null;
            String testStepState = null;

            int maxAttempts = timeout / interval;
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                testStepState = FW_ReportUtils.getTestStepState(startingTime, Instant.now().getEpochSecond(), timeout, Integer.parseInt(FW_ConfigMgr.getDefaultTimeoutWarnThreshold()));
                locatorText = getLocatorText(locator, timeout, interval);

                // Check if the search string is within the text at the locator.
                if (locatorText.toLowerCase().contains(search.toLowerCase())) { // Convert to lower case here so return message is consistent.
                    String message;
                    if ("[Pass]".equals(testStepState)) {
                        message = "[Pass] - '" + search + "' found within text at locator: '" + locator + "' in " + (Instant.now().getEpochSecond() - startingTime) + " seconds.";
                        if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightCSS());}
                    } else {
                        message = "[Pass Warn] - '" + search + "' found within text at locator: '" + locator + "' in " + (Instant.now().getEpochSecond() - startingTime) + " seconds.";
                        if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightWarnCSS());}
                    }
                    if (verbose) { message += "\nEntire text found: '" + locatorText + "'."; }
                    return message;
                }

                // If the search string is not found, highlight the locator.
                if ("[Pass Warn]".equals(testStepState)) {
                    if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightWarnCSS());}
                }

                if ("[Fail]".equals(testStepState)) {
                    if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightErrorCSS());}
                    String message = "[Fail] - '" + search + "' not found within text at locator: '" + locator + "' after timeout of '" + timeout + "' seconds.";
                    if (verbose == true) { message += "\nEntire text found: '" + locatorText + "'."; }
                    return message;
                }

                try {
                    Thread.sleep(interval * 1000); // sleep method accepts milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Prevent an infinite loop state.
            if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightErrorCSS());}
            String message = "[Fail] - '" + search + "' not found within text at locator: '" + locator + "' after timeout of '" + timeout + "' seconds.";
            if (verbose == true) { message += "\nEntire text found: '" + locatorText + "'."; }
            return message;

        } else {
            return "[Fail] - Locator: '" + locator + "' does not exist.";
        }
    }

    /**
     * Validates if the given search string is not present within the text at the specified locator.
     *
     * @param locator The locator to check.
     * @param search The string to search for within the text at the locator.
     * @param timeout The maximum time to wait for the locator to exist.
     * @param interval The interval at which to check if the locator exists.
     * @param verbose If true, the full text found at the locator will be included in the result.
     *
     * @return A string indicating whether the search string was not found within the text at the locator.
     * If the locator does not exist, a failure message is returned.
     */
    public String validateNotWithinLocatorText(String locator, String search, int timeout, int interval, boolean verbose) {
        boolean result = waitToExist(locator, timeout, interval);
        if (result) {
    
            var startingTime = Instant.now().getEpochSecond();
            String locatorText = null;
            String testStepState = null;
    
            int maxAttempts = timeout / interval;
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                testStepState = FW_ReportUtils.getTestStepState(startingTime, Instant.now().getEpochSecond(), timeout, Integer.parseInt(FW_ConfigMgr.getDefaultTimeoutWarnThreshold()));
                locatorText = getLocatorText(locator, timeout, interval);
    
                // Check if the search string is not within the text at the locator.
                if (!locatorText.toLowerCase().contains(search.toLowerCase())) {
                    String message;
                    if ("[Pass]".equals(testStepState)) {
                        message = "[Pass] - '" + search + "' not found within text at locator: '" + locator + "' in " + (Instant.now().getEpochSecond() - startingTime) + " seconds.";
                        if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightCSS());}
                    } else {
                        message = "[Pass Warn] - '" + search + "' not found within text at locator: '" + locator + "' in " + (Instant.now().getEpochSecond() - startingTime) + " seconds.";
                        if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightWarnCSS());}
                    }
                    if (verbose) { message += "\nEntire text found: '" + locatorText + "'."; }
                    return message;
                }
    
                // If the search string is found, highlight the locator.
                if ("[Pass Warn]".equals(testStepState)) {
                    if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightWarnCSS());}
                }
    
                if ("[Fail]".equals(testStepState)) {
                    if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightErrorCSS());}
                    String message = "[Fail] - '" + search + "' found within text at locator: '" + locator + "' after timeout of '" + timeout + "' seconds.";
                    if (verbose) { message += "\nEntire text found: '" + locatorText + "'."; }
                    return message;
                }
    
                try {
                    Thread.sleep(interval * 1000); // sleep method accepts milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    
            // Prevent an infinite loop state.
            if ("true".equals(FW_ConfigMgr.getLocatorHighlight().toLowerCase())) {locatorHighlight(locator, FW_ConfigMgr.getLocatorHighlightErrorCSS());}
            String message = "[Fail] - '" + search + "' found within text at locator: '" + locator + "' after timeout of '" + timeout + "' seconds.";
            if (verbose) { message += "\nEntire text found: '" + locatorText + "'."; }
            return message;
    
        } else {
            return "[Fail] - Locator: '" + locator + "' does not exist.";
        }
    }

    /**
     * Sets the text of the locator and returns a string indicating the result of
     * the operation.
     * 
     * @param locator    The XPath locator to check for.
     * @param value      The value to set the locator to.
     * @param timeout    The number of seconds to wait before timing out.
     * @param javascript Whether or not to use the Javascript approach to set the
     *                   text.
     * 
     * @return A string containing the result of the operation.
     */
    public String setText(String locator, String value, int timeout, boolean javascript) {
        boolean result = waitToExist(locator, timeout, 1);
        if (result == false) {
            return "[Fail] - Tried to enter text into locator: '" + locator + "' but locator not found. Check if locator changed or missing?";
        }

        if (javascript == true) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                WebElement element = driver.findElement(By.xpath(locator));
                js.executeScript("arguments[0].value='" + value + "';", element);
            } catch (Exception e) {
                return "[Fail] - Tried to enter text using the Javascript approach into locator: '" + locator + "' but encountered error. Is element active and able to receive text from a user?";
            }
        } else {
            try {
                WebElement element = driver.findElement(By.xpath(locator));
                element.sendKeys(value);
            } catch (NoSuchElementException e) {
                return "[Fail] - No element found with locator: '" + locator + "'. Has the locator been changed or removed?";
            } catch (Exception e) {
                return "[Fail] - Tried to enter text into locator: '" + locator + "' but encountered an error: " + e.getMessage() + ". Is the element active and able to receive text from a user?";
            }
        }

        return "[Pass] - Successfully entered '" + value + "' into locator: '" + locator + "'.";
    }

    /**
     * Clicks the locator and returns a string indicating the result of the
     * operation.
     *
     * @param locator The XPath locator to check for.
     * @param timeout The number of seconds to wait before timing out.
     * @return A string containing the result of the operation.
     */
    public String clickLocator(String locator, int timeout) {
        boolean result = waitToExist(locator, timeout, 1);
        if (result) {
            try {
                WebElement element = driver.findElement(By.xpath(locator));
                element.click();
                return "[Pass] - Clicked locator: '" + locator + "'.";
            } catch (NoSuchElementException e) {
                return "[Fail] - Element with locator: '" + locator + "' does not exist.";
            } catch (WebDriverException e) {
                return "[Fail] - Failed to click locator: '" + locator + "'. Error: " + e.getMessage();
            }
        } else {
            return "[Fail] - Tried to click locator: '" + locator + "' but it does not exist within the given timeout.";
        }
    }

    /**
     * Scrolls to the locator.
     *
     * @param locator The XPath locator to check for.
     * 
     * @return A string containing the result of the operation.
     */
    public String scrollToLocator(String locator) {
        boolean result = waitToExist(locator, 10, 1);
        if (result == true) {
            result = scrollTo(locator);
            if (result == true) {
                return "[Pass] - Scrolled to locator: '" + locator + "'.";
            } else {
                return "[Fail] - Failed to scroll to locator: '" + locator + "'.";
            }
        } else {
            return "[Fail] - Could not scroll to locator: '" + locator + "' does not exist.";
        }
    }

    /**
     * Mouse over the element specified by the locator.
     *
     * @param locator The locator of the element.
     * @param timeout The number of seconds to wait before timing out.
     * 
     * @return A string containing the result of the operation.
     */
    public String mouseOverLocator(String locator, int timeout) {
        // Check if the locator exists.
        boolean result = waitToExist(locator, timeout, 1);
        if (result) {
            try {
                Actions actions = new Actions(driver);
                WebElement element = driver.findElement(By.xpath(locator));
                actions.moveToElement(element).perform();
                return "[Pass] - Successfully moused over locator '" + locator + "'.";
            } catch (Exception e) {
                return "[Fail] - Could not mouse over locator '" + locator + "'.";
            }
        } else {
            return "[Fail] - Could not mouse over because locator '" + locator + "' does not exist.";
        }
    }

    /**
     * Drag and Drop the locatorStarting to locatorDestination.
     *
     * @param locatorStarting The XPath locator of the source element.
     * @param locatorDestination The XPath locator of the target element.
     * @param timeout       The number of seconds to wait before timing out.
     *
     * @return A string containing the result of the operation.
     */
    public String dragAndDropLocators(String locatorStarting, String locatorDestination, int timeout) {
        // Check if the starting locator exists.
        boolean resultStarting = waitToExist(locatorStarting, timeout, 1);
        if (resultStarting == true) {
            // Check if the destination locator exists.
            boolean resultDestination = waitToExist(locatorDestination, timeout, 1);
            if (resultDestination == true) {
                try {
                    Actions actions = new Actions(driver);
                    WebElement elementStarting = driver.findElement(By.xpath(locatorStarting));
                    WebElement elementDestination = driver.findElement(By.xpath(locatorDestination));
                    actions.dragAndDrop(elementStarting, elementDestination).perform();
                    return "[Pass] - Drag and drop successful ";
                } catch (Exception e) {
                    return "[Fail] - Could not drag from starting locator '" + locatorStarting + "' to destination locator '" + locatorDestination + "'.";
                }
            } else {
                return "[Fail] - Could not drag to destination locator '" + locatorDestination + "' does not exist.";
            }
        } else {
            return "[Fail] - Could not drag for starting locator '" + locatorStarting + "' does not exist.";
        }
    }

    /**
     * Sets the value of a dropdown.
     *
     * @param locator The XPath locator of the dropdown.
     * @param dropdownValue The value to select from the dropdown
     * 
     * @return A string containing the result of the operation.
     */
    public String setDropdown(String locator, String dropdownValue) {
        try {
            Select dropdown = new Select(driver.findElement(By.xpath(locator)));
            dropdown.selectByVisibleText(dropdownValue);
            return "[Pass] - Selected dropdown '" + dropdownValue + "' from locator '" + locator + "'.";
        } catch (NoSuchElementException e) {
            return "[Fail] - Dropdown '" + dropdownValue + "' does not exist for locator '" + locator + "'.";
        }
    }

    /**
     * Switches to the iframe specified by the locator.
     *
     * @param iframeLocator The locator of the iframe.
     * @param timeout The time to wait before switching to the iframe.
     * 
     * @return A string containing the result of the operation.
     */
    public String switchToIframe(String iframeLocator, int timeout) {
        try {
            Thread.sleep(timeout * 1000);
            driver.switchTo().frame(iframeLocator);
            return "[Pass] - Successfully switched to the iframe " + iframeLocator + ".";
        } catch (Exception e) {
            return "[Fail] - Could not switch to iframe " + iframeLocator + ". Has it been loaded and does it exist?";
        }
    }

    /**
     * Attempts to switch the WebDriver's context from an iFrame back to the parent
     * frame.
     *
     * @return A string containing the result of the operation.
     */
    public String switchToParentFrame() {
        try {
            driver.switchTo().defaultContent();
            return "[Pass] - Successfully switched out of iFrame back to the parent.";
        } catch (WebDriverException e) {
            return "[Fail] - Could not switch to the parent of the iFrame. Did an error occur within the iFrame?";
        }
    }

    /**
     * Switches to the given window position, which is zero-based
     *
     * @param position The index of the window to switch to
     * 
     * @return A string containing the result of the operation
     */
    public String switchToWindowByPosition(int position) {
        try {
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(position));
            return "[Pass] - Successfully switched to new window.";
        } catch (Exception e) {
            return "[Fail] - Could not switch to the new window. Did the correct window open after the previous action?";
        }
    }

    /**
     * Switches back to the originating, parent window
     *
     * @return A string containing the result of the operation
     */
    public String switchToParentWindow() {
        try {
            driver.switchTo().window(driver.getWindowHandles().iterator().next());
            return "[Pass] - Successfully switched to the original window.";
        } catch (Exception e) {
            return "[Fail] - Could not switch to the original window. Is it still open?";
        }
    }

    /**
     * Changes the size of the current window
     *
     * @param size represents sizes typical for mobile, tablet, and desktop
     * 
     * @return A string containing the result of the operation
     */
    public String resizeWindow(String size) {
        try {
            switch (size) {
                case "mobile":
                    driver.manage().window().setSize(new Dimension(540, 960));
                    break;
                case "tablet":
                    driver.manage().window().setSize(new Dimension(768, 1024));
                    break;
                case "tablet_large":
                    driver.manage().window().setSize(new Dimension(912, 1368));
                    break;
                case "desktop":
                    driver.manage().window().setSize(new Dimension(1440, 900));
                    break;
                case "desktop_large":
                    driver.manage().window().setSize(new Dimension(1920, 1080));
                    break;
                default:
                    return "[Fail] - " + size + " isn't a valid window size. Please choose mobile, tablet, tablet_large, desktop, or desktop_large";
            }
            return "[Pass] - the window has been resized to " + size;
        } catch (Exception e) {
            return "[Fail] - the window wasn't resized to " + size;
        }
    }

    /**
     * Executes a script with a given locator.
     *
     * @param javascript The JavaScript to execute.
     * @param locator The locator to execute the JavaScript upon.
     * 
     * @return A string containing the result of the operation.
     */
    public String executeScript(String javascript, String locator) {
        try {
            executeJavascript(javascript, locator);
            return "[Pass] - Successfully executed JavaScript with " + locator;
        } catch (Exception e) {
            return "[Fail] - Encountered error executing JavaScript with " + locator;
        }
    }

    /**
     * Validate is the actual value is empty.
     *
     * @param actual The actual string.
     * @param description Description to include with the returned result for context
     * 
     * @return A string containing the result of the operation.
     */
    public String validateEmpty(String actual, String description) {

        // Check for null values.
        if (actual == null) {
            return "[Pass] - " + description + " - Actual value is null.";
        }

        // Get length now that we know we don't have a null value.
        int actualLength = actual.length();

        // Check if actual length is greater than 0.
        if (actualLength != 0) {
            return "[Fail] - " +  description + " - Actual '" + actual + "' (" + actualLength + ") value is not empty.";
        }

        // Default return is pass as value is empty.
        return "[Pass] - " +  description + " - Actual '" + actual + "' (" + actualLength + ") value is empty.";
    }

    /**
     * Compare two strings and return a string indicating the result of the operation.
     *
     * @param expected The expected string.
     * @param actual The actual string.
     * @param description Description to include with the returned result for context
     * 
     * @return A string containing the result of the operation.
     */
    public String validateExactMatch(String expected, String actual, String description) {
        
        // Check if expected and actual are equal.
        if (Objects.equals(expected, actual)) {
            if (expected == null) {
                return "[Pass] - " +  description + " - Expected and actual values are null and identical.";
            } else {
                return "[Pass] - " +  description + " - Expected '" + expected + "' (" + expected.length() + ") and actual '" + actual + "' (" + actual.length() + ") values are identical.";
            }
        }
    
        // If they are not equal, check if either is null.
        if (expected == null) {
            return "[Fail] - " +  description + " - Expected is null and actual is not null.";
        }
    
        if (actual == null) {
            return "[Fail] - " +  description + " - Expected is not null and actual is null.";
        }
    
        // If neither is null and they are not equal, they must be of different lengths.
        return "[Fail] - " +  description + " - Expected '" + expected + "' (" + expected.length() + ") and actual '" + actual + "' (" + actual.length() + ") values are different.";
    }

    /**
     * Validates if the expected string is within the actual string.
     *
     * @param search The string to search for.
     * @param full The string to search within.
     * @param description Description to include with the returned result for context
     * 
     * @return A string containing the result of the operation.
     */
    public String validateWithinMatch(String search, String full, String description) {

        if (full.contains(search)) {

            // Get index of expected within actual.
            int indexStart = full.indexOf(search);
            int indexEnd = indexStart + search.length() - 1;
            return "[Pass] - " + description + " - Search value '" + search + "' is within full value '" + full + "' at position " + indexStart + " to " + indexEnd;
        } else {
            return "[Fail] - " + description + " - Search value '" + search + "' is not within full value '" + full + "'";
        }
    }

    /**
     * Validates if the expected integer matches the actual integer.
     *
     * @param expected The expected integer.
     * @param actual The actual integer.
     * @param description The description of the validation.
     * 
     * @return A string containing the result of the validation.
     */
    public String validateIntegerMatch(int expected, int actual, String description) {

        if (expected == actual) {
            return "[Pass] - " + description + " - Expected integer value '" + expected + "' matches actual integer value '" + actual + "'";
        } else {
            return "[Fail] - " + description + " - Expected integer value '" + expected + "' does not match actual integer value '" + actual + "'";
        }
    }

    /**
     * Validates if the expected float matches the actual float.
     *
     * @param expected The expected float.
     * @param actual The actual float.
     * @param description The description of the validation.
     * 
     * @return A string containing the result of the validation.
     */
    public String validateFloatMatch(float expected, float actual, String description) {

        if (expected == actual) {
            return "[Pass] - " + description + " - Expected float value '" + expected + "' matches actual float value '" + actual + "'";
        } else {
            return "[Fail] - " + description + " - Expected float value '" + expected + "' does not match actual float value '" + actual + "'";
        }
    }

    /**
     * Checks for the alert to exist, and then tries to accept it.
     *
     * @return A string containing the result of the validation.
     */
    public String acceptAlert() {

        if (!alertExist()) {
            return "[Fail] - No alert is present to accept.";
        }

        if (!alertAccept()) {
            return "[Fail] - Could not accept the alert. Is it able to be accepted?";
        }

        return "[Pass] - Accepted the alert.";
    }

    /**
     * Checks for the alert to exist, and then tries to dismiss it.
     *
     * @return A string containing the result of the validation.
     */
    public String dismissAlert() {

        if (!alertExist()) {
            return "[Fail] - No alert is present to dismiss.";
        }

        if (!alertDismiss()) {
            return "[Fail] - Could not dismiss the alert. Is it able to be dismissed?";
        }

        return "[Pass] - Dismissed the alert.";
    }

    /**
     * Sets the text of an alert if it exists and is interactable.
     *
     * @param value The text to be set in the alert text field.
     * 
     * @return A string containing the result of the validation.

     */
    public String setAlertText(String value) {

        if (!alertExist() || !alertInteractable()) {
            return "[Fail] - No alert is present to dismiss.";
        }

        try {
            driver.switchTo().alert().sendKeys(value);
            return "[Pass] - Sent '" + value + "' to alert.";
        } catch (Exception e) {
            return "[Fail] - Could not send '" + value + "' to the alert. Is it interactable?";
        }

    }

    /**
     * Upload a file once the file path and locator have been verified
     * 
     * @param locator The XPath locator related to the upload element
     * @param file The file path and filename to upload.
     * @param timeout The number of seconds to wait before timing out.
     * 
     * @return A string containing the result of the validation.
     */
    public String fileUpload(String locator, String file, int timeout) {
        if (!Files.exists(Paths.get(file))) {
            return "[Fail] - Expected the file path '" + file + "'' to be a valid file path.";
        }

        boolean result = waitToExist(locator, timeout, 1);
        if (result == false) {
            return "[Fail] - Expected to upload a file into '" + locator + "'' but it could not be found. Has the locator been changed or removed?";
        }

        try {
            driver.findElement(By.xpath(locator)).sendKeys(file);
        } catch (Exception e) {
            return "[Fail] - Expected to upload the file '" + file + "'' into " + locator + " but encountered an error. Is the file upload active and able to receive input from the user?";
        }

        return "[Pass] - Successfully uploaded the file '" + file + "'' into " + locator + ".";
    }


}