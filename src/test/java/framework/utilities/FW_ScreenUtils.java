package framework.utilities;

// Selenium WebDriver
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

// Automation framework
import framework.reporting.FW_AllureAnnotationStepScreenShot;

// Automation Reporting
import io.qameta.allure.Allure;

// Java file I/O
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Image manipulation
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * This class provides utility methods for taking screenshots.
 */
public class FW_ScreenUtils {

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
     * Takes a screenshot of the current page.
     *
     * @param driver The WebDriver object.
     * @param fileNameReference The name of the screenshot.
     * @param screenshotDirectory The directory to save the screenshot.
     */
    @FW_AllureAnnotationStepScreenShot("Screenshot: {0}")
    public static void takeScreenshot(WebDriver driver, String fileNameReference, String screenshotDirectory) {

        // Check if driver is null
        if (driver == null) {
            // Get the class and method that made the call
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stackTraceElements[2]; // The caller is at index 2
            String callingClass = caller.getClassName();
            String callingMethod = caller.getMethodName();

            // Return the error message
            System.out.println(orange + "[Warn] - Framework error encountered, driver parameter was null. Check called code '" + callingClass + "." + callingMethod + "'." + reset);
        }

        // Get the Screen Capture options from testConfig.properties
        String screenCaptureTrim = FW_ConfigMgr.getScreenCaptureTrim();

        // Come up with a unique name for the screenshot
        String fileName = fileNameReference + "_" + System.currentTimeMillis();

        String filePath = screenshotDirectory + "/" + fileName + ".png";

        // Come up with the filepath for the screenshot
        String projectDirectory = System.getProperty("user.dir");
        String screenshotDirectoryPath = projectDirectory + "/" + screenshotDirectory;

        // Take the screenshot
        TakesScreenshot screenshotTaker = (TakesScreenshot) driver;

        // Store the screenshot in a temporary file
        File screenshotFile = screenshotTaker.getScreenshotAs(OutputType.FILE);

        System.out.println(brown + "[Screenshot] - " + fileName + ".png" + reset);

        // Trim the image if screenCaptureTrim is true
        if ("true".equals(screenCaptureTrim)) {
            screenshotFile = trimImage(screenshotFile);
        }

        // Ensure the screenshots directory exists
        File screenshotDirectoryPathObj = new File(screenshotDirectoryPath);

        // Create the screenshots destination directory if it does not exists
        if (!screenshotDirectoryPathObj.exists()) {
            screenshotDirectoryPathObj.mkdirs();
        }

        // Create the destination path
        Path destinationPath = Paths.get(screenshotDirectoryPath, fileName + ".png");

        String result = "";
        try {

            // Copy the screenshot to the destination path
            Files.copy(screenshotFile.toPath(), destinationPath);

            // Set screenshot context for the Allure reporting
            Allure.step("Screenshot: " + fileName + ".png", () -> {

                // Attach the screenshot to the Allure report
                try {
                    Allure.addAttachment("Screenshot: " + fileName + ".png", "image/png", Files.newInputStream(Paths.get(filePath)), ".png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            result = "[Fail] - Error encountered while taking a screenshot.";
            System.err.println(result);
            e.printStackTrace();
        }
     
    }

    public static File trimImage(File imageFile) {
        try {
            // Load the image
            BufferedImage image = ImageIO.read(imageFile);
    
            // Get the color of the pixel at the lower right corner
            int backgroundPixelColor = image.getRGB(image.getWidth() - 1, image.getHeight() - 1);
    
            // Find the bounding box
            int top = 0, left = 0, right = image.getWidth() - 1, bottom = image.getHeight() - 1; 
            topLoop: for (; top < bottom; top++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (image.getRGB(x, top) != backgroundPixelColor) {
                        break topLoop;
                    }
                }
            }
    
            leftLoop: for (; left < right; left++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(left, y) != backgroundPixelColor) {
                        break leftLoop;
                    }
                }
            }
    
            bottomLoop: for (; bottom >= top; bottom--) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (image.getRGB(x, bottom) != backgroundPixelColor) {
                        break bottomLoop;
                    }
                }
            }
    
            rightLoop: for (; right >= left; right--) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(right, y) != backgroundPixelColor) {
                        break rightLoop;
                    }
                }
            }
    
            // Create a new image from the non-background area
            BufferedImage croppedImage = image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    
            // Overwrite the original image with the cropped image
            ImageIO.write(croppedImage, "png", imageFile);
    
            // Calculate the original size, trimmed size, and the percentage savings
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            int originalSize = originalWidth * originalHeight;
            
            int trimmedWidth = croppedImage.getWidth();
            int trimmedHeight = croppedImage.getHeight();
            int trimmedSize = trimmedWidth * trimmedHeight;

            double savings = 100.0 * (originalSize - trimmedSize) / originalSize;

            // Print the results
            System.out.println(brown + "[Trim Image] - Original: " + originalWidth + "x" + originalHeight + " --> Trimmed: " + trimmedWidth + "x" + trimmedHeight + " --> Saved: " + String.format("%.1f", savings) + "%" + reset);

            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return null;
    }

}