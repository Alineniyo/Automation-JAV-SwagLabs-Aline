// This code for the JUnit custom assertion
package framework.assertions;

// Selenium WebDriver
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.Assertions;

// Automation framework
import framework.utilities.FW_ConfigMgr;
import framework.utilities.FW_ScreenUtils;
import framework.reporting.FW_AllureAnnotationStepPass;
import framework.reporting.FW_AllureAnnotationStepFail;

// Automation Reporting
import io.qameta.allure.Allure;

// Java file I/O
// import java.io.IOException;
// import java.nio.file.Paths;
// import java.nio.file.Files;

/**
 * This class is used to create custom assertion specific for our autoamtion framework utilizing JUnit.
 */
public class FW_CustomAssertJU {

    // Declare ANSI escape sequences for formatting console output colors
    static String black = "\033[0;30m";         // BLACK
    static String brown = "\033[38;5;94m";      // BROWN
    static String red = "\033[0;31m";           // RED
    static String orange = "\033[38;5;208m";    // ORANGE
    static String yellow = "\033[0;33m";        // YELLOW
    static String green = "\033[0;32m";         // GREEN
    static String blue = "\033[0;34m";          // BLUE
    static String violet = "\033[0;35m";        // VIOLET
    static String grey = "\033[0;37m";          // GREY
    static String darkGrey = "\033[0;90m";      // DARK GREY
    static String white = "\033[0;97m";         // WHITE
    static String reset = "\033[0m";            // Text Reset

    /**
     * Default constructor.
     */
    public FW_CustomAssertJU() {
        // Default constructor
    }

    // Declare WebDriver instance at class level
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Modify the setDriver method
    public static void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
    }

    // Add a method to get the driver
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Asserts that the given result string contains either "[Pass]" or "[Fail]".
     * If the result is null, the test fails immediately.
     * If the result contains "[Pass]", the test passes.
     * If the result contains "[Fail]", the test fails.
     * If the result contains neither "[Pass]" nor "[Fail]", the test fails with a warning about invalid formatting.
     *
     * @param result the result string to check
     */
    public static void autoPass(String result) {

        // Handle null result
        if (result == null) {
            result = "[Fail]: Result is null.";
            stepFail(result);
            return;
        }

        // Handle [Audit Pass] result
        if (result.toUpperCase().contains("[AUDIT PASS]")) {
            System.out.println(green + result +  reset);
            Assertions.assertTrue(true);

        // Handle [Audit Fail] result
        } else if (result.toUpperCase().contains("[AUDIT FAIL]")) {
            System.out.println(red + result + reset);
            Assertions.fail(result);

        // Handle [Pass Warn] result
        } else if (result.toUpperCase().contains("[PASS WARN]")) {
            System.out.println(yellow + result +  reset);
            Assertions.assertTrue(true);

        // Handle [Pass] result
        } else if (result.toUpperCase().contains("[PASS]")) {
            stepPass(result);

        // Handle [Fail] result
        } else if (result.toUpperCase().contains("[FAIL]")) {
            stepFail(result);

        // Handle missing Meta tag
        } else {
            System.out.println(yellow + "[Warn]: Invalid formatting of test result '" + result + "' which should contain either '[Pass]' or '[Fail]'." + reset);
            Assertions.fail("[Warn]: Invalid formatting of test result '" + result + "' which should contain either '[Pass]' or '[Fail]'.");
        }
    }

    /**
     * The stepPass method is used to send the pass step details to the Allure report and listener.
     * @param result
     */
    @FW_AllureAnnotationStepPass("Step: {0}")
    private static void stepPass(String result) {
        System.out.println(green + result + reset);

        // Set pass context for the Allure reporting
        Allure.step("Step: " + result, () -> {

            // Pass the test
            Assertions.assertTrue(true);
        });
    }

    /**
     * The stepResult method is used to send the step details to the Allure report and listener.
     * @param result
     */
    @FW_AllureAnnotationStepFail("Step: {0}")
    private static void stepFail(String result) {
        System.out.println(red + result + reset);

        // Set fail context for the Allure reporting
        Allure.step("Step: " + result, () -> {

            // Take a screenshot if screenCaptureOnTestStepFail is true
            if (FW_ConfigMgr.getScreenCaptureOnTestStepFail().toLowerCase().equals("true")) {
                FW_ScreenUtils.takeScreenshot(getDriver(), "Fail", FW_ConfigMgr.getScreenCaptureDirectory());
            }
            // Fail the test
            Assertions.fail(result);
        });
    }

}