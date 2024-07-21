package framework.utilities;

// Automation framework
import framework.reporting.FW_AllureAnnotationNote;

// Automation Reporting
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

/**
 * This class provides utility methods related to reporting.
 */
public class FW_ReportUtils {

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
     * Record note.
     *
     * @param note The note to record
     */
    @FW_AllureAnnotationNote("Note: {0}")
    public static void recordNote(String note) {

        note = note.toString();

        System.out.println(yellow + "[Note] - " + note + reset);

        // Attach the note to the Allure report
        Allure.step("Note: " + note, Status.BROKEN); // BROKEN status is used for notes in Allure as it represents the color yellow.

    }

    /**
     * Derive the test step state based on the time taken to execute the test step.
     *
     * @param startingTime The time when the test step started
     * @param actualTime The actual time at the moment of the test step state determination
     * @param timeout The timeout for the test step to complete
     * @param timeoutWarnThreshold The threshold percentage of the timeout to consider the test step as a warning
     * 
     * @return The determined result of [Pass], [Pass Warn], or [Fail]
     */
    public static String getTestStepState(long startingTime, long actualTime, int timeout, int timeoutWarnThreshold) {
        long timeTaken = actualTime - startingTime;
        if (timeTaken <= timeout) {
            if (timeTaken <= timeout * timeoutWarnThreshold / 100.0) {
                return "[Pass]";
            } else {
                return "[Pass Warn]";
            }
        } else {
            return "[Fail]";
        }
    }

}