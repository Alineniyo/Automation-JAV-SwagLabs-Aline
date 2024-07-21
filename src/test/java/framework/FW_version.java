// ===============================================================
// Automation Framework
// Developed by Greg Paskal - Dec 2023
// ===============================================================
// Description
// This automation framework is designed to be used with Selenium WebDriver and Java.
// The framework utilizes the Page Object (PO) design pattern.
// It is built to be used with the JUnit testing framework along with the Maven build tool.
// ===============================================================
// Change Log
// Date        Name            Version            Description
// ----------  --------------  -------  -----------------------------------------
// 2023-12-05  G. Paskal        1.0.0   Initial creation
// 2023-12-06  G. Paskal        1.0.1   Added support for Maven
// 2023-12-07  G. Paskal        1.0.2   Added support for JUnit
// 2023-12-08  G. Paskal        1.0.3   Added support for Page Object (PO) design pattern
// 2023-12-09  G. Paskal        1.0.4   Added support for Configuration Manager
// 2023-12-10  G. Paskal        1.0.5   Added support for Allure reporting
// 2024-01-21  G. Paskal        2.0.0   Extended FW_Driver with more methods
// 2024-01-22  G. Paskal        2.0.1   Added FW_Page method to switchToIframe
// 2024-01-23  G. Paskal        2.0.2   Added FW_Page method to switch to different windows
// 2024-01-24  G. Paskal        2.0.3   Added FW_Page methods for not exist and cookies
// 2024-01-25  G. Paskal        2.0.4   Enhanced Configuration Manager with easier access to defaultTimeout and defaultInterval
// 2024-02-01  G. Paskal        2.0.5   Enhanced FW_Driver with more fundamental methods
// 2024-02-05  G. Paskal        2.0.6   Enhanced FW_Page with more fundamental methods
// 2024-02-06  G. Paskal        2.0.7   Completed setup of FW_Driver and FW_Page with fundamental methods
// 2024-02-07  G. Paskal        2.0.8   Refatored validateExactMatch to handel nulls better.
// 2024-02-27  G. Paskal        2.0.9   Began adding audits to the framework
// 2024-02-27  G. Paskal        2.0.10  Added enhancements to the custom assertion class to record step details in allure.
// 2024-02-28  G. Paskal        2.0.11  Added method to do test search within a locator text including a verbose return option.
// 2024-03-08  G. Paskal        2.0.12  Added methods for FW_Network class to gather network details. Enhanced Unit Test for FW_Network.
// 2024-03-14  G. Paskal        2.0.13  Added method to verify text was not found within a locator.
// 2024-03-18  G. Paskal        2.0.14  Added method to Drag and Drop and also refactored mouse over method with more exception handling.
// 2024-04-03  G. Paskal        2.0.15  Added capabilities for headless execution and multiple browser support. Refactored WebDriver into FW_Browser. Added screenshots and locator highlighting.
// 2024-04-15  G. Paskal        2.0.16  Added ability to highlight some failed locators (textual) in a different color.
// 2024-04-19  G. Paskal        2.0.17  Improvements made to Allure reporting with new Note feature and refactor of Screenshot.
// 
// ===============================================================

package framework;

/**
 * This class contains the version information of the framework.
 */
public class FW_version {

    /**
     * The current version of the framework.
     */
    public static final String VERSION = "2.0.17";

}
