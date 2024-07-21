package framework.utilities;

/**
 * This class provides methods for reading the POM file.
 */
public class FW_StringUtils {

    /**
     * Modifies original string by inserting inset string directly after first occurrence of "]" character.
     *
     * @param original The original string containing a meta which will be modified.
     * @param insert The string to insert into the original string containing thge meta
     *
     * @return The modified string containing the meta with the insert string.
     */
    public static String metaInsert(String original, String insert) {
        return original.replaceFirst("]", "] - " + insert);
    }

}