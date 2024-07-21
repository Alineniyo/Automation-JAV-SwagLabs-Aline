package framework.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The ConfigMgr class is a singleton class that reads a properties file (testConfig.properties)
 * and stores the properties as global variables in a map. This class is used to manage
 * configuration data that needs to be accessed globally within the test autoamtion and framework.
 */
public class FW_ConfigMgr {
    private static FW_ConfigMgr instance = null; // Singleton instance
    private Map<String, GlobalVariable> globalVariables = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Returns the singleton instance of the ConfigMgr class.
     * If the instance does not exist, it is created.
     *
     * @return the singleton instance of the ConfigMgr class
     */
    public static synchronized FW_ConfigMgr getInstance() {
        if (instance == null) {
            instance = new FW_ConfigMgr();
        }
        return instance;
    }

    /**
     * Constructs a new ConfigMgr object and reads the testConfig.properties file.
     */
    private FW_ConfigMgr() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/testConfig.properties");
            Properties properties = new Properties();
            properties.load(fis);

            // Initialize global variables
            for (String name : properties.stringPropertyNames()) {
                String value = properties.getProperty(name);
                String type = "String"; // default type
                Object parsedValue = value; // default parsed value

                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    type = "Boolean";
                    parsedValue = Boolean.parseBoolean(value);
                } else {
                    try {
                        parsedValue = Integer.parseInt(value);
                        type = "Integer";
                    } catch (NumberFormatException e1) {
                        try {
                            parsedValue = Long.parseLong(value);
                            type = "Long";
                        } catch (NumberFormatException e2) {
                            try {
                                parsedValue = Double.parseDouble(value);
                                type = "Double";
                            } catch (NumberFormatException e3) {
                                try {
                                    parsedValue = dateFormat.parse(value);
                                    type = "Date";
                                } catch (ParseException e4) {
                                    // value is a string, no need to parse it
                                }
                            }
                        }
                    }
                }

                globalVariables.put(name, new GlobalVariable(name, type, parsedValue));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the GlobalVariable associated with the specified key.
     *
     * @param key the key whose associated GlobalVariable is to be returned
     * @return the GlobalVariable associated with the specified key, or null if this map contains no mapping for the key
     */
    public GlobalVariable getVar(String key) {
        GlobalVariable var = globalVariables.get(key);
        if (var == null) {
            var = new GlobalVariable(key,"String",""); // Set to return an empty string if the key is not found
        }
        return var;
    }

    /**
     * Returns the value of the GlobalVariable associated with the specified key.
     *
     * @param key the key whose associated GlobalVariable's value is to be returned
     * @return the value of the GlobalVariable associated with the specified key, or null if this map contains no mapping for the key
     */
    public static Object getVarVal(String key) {
        return getInstance().getVar(key).getValue();
    }

    /**
     * Returns the version in testConfig.properties.
     *
     * @return the version of the testConfig.properties file
     */
    public static double getVersion() {
        Object value = getVarVal("version");
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new RuntimeException("version is not a Number");
        }
    }

    /**
     * Returns the defaultTimeout testConfig.properties value.
     *
     * @return the defaultTimeout value.
     */
    public static int getDefaultTimeout() {
        Object value = getVarVal("defaultTimeout");
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new RuntimeException("defaultTimeout is not an Integer");
        }
    }

    /**
     * Returns the defaultInterval testConfig.properties value.
     *
     * @return the defaultInterval value.
     */
    public static int getDefaultInterval() {
        Object value = getVarVal("defaultInterval");
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new RuntimeException("defaultInterval is not an Integer");
        }
    }

    /**
     * Returns the locatorHighlight testConfig.properties value.
     *
     * @return the locatorHighlight value.
     */
    public static String getLocatorHighlight() {
        Object value = getVarVal("locatorHighlight");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("locatorHighlight is not a String");
        }
    }

    /**
     * Returns the locatorHighlightCSS testConfig.properties value.
     *
     * @return the locatorHighlightCSS value.
     */
    public static String getLocatorHighlightCSS() {
        Object value = getVarVal("locatorHighlightCSS");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("locatorHighlightCSS is not a String");
        }
    }

    /**
     * Returns the defaultTimeoutWarnThreshold testConfig.properties value.
     *
     * @return the defaultTimeoutWarnThreshold value.
     */
    public static String getDefaultTimeoutWarnThreshold() {
        Object value = getVarVal("defaultTimeoutWarnThreshold");
        if (value == null) {
            return "0";
        }
        String strValue = value.toString().replace("%", ""); // remove the '%' sign
        try {
            int intValue = Integer.parseInt(strValue); // convert to type Integer
            return Integer.toString(intValue); // convert back to String
        } catch (NumberFormatException e) {
            throw new RuntimeException("defaultTimeoutWarnThreshold is not a valid integer", e);
        }
    }

    /**
     * Returns the locatorHighlightWarnCSS testConfig.properties value.
     *
     * @return the locatorHighlightWarnCSS value.
     */
    public static String getLocatorHighlightWarnCSS() {
        Object value = getVarVal("locatorHighlightWarnCSS");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("locatorHighlightWarnCSS is not a String");
        }
    }

    /**
     * Returns the locatorHighlightErrorCSS testConfig.properties value.
     *
     * @return the locatorHighlightCSS value.
     */
    public static String getLocatorHighlightErrorCSS() {
        Object value = getVarVal("locatorHighlightErrorCSS");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("locatorHighlightErrorCSS is not a String");
        }
    }

    /**
     * Returns the screenCaptureOnTestStepFail testConfig.properties value.
     *
     * @return the screenCaptureOnTestStepFail value.
     */
    public static String getScreenCaptureOnTestStepFail() {
        Object value = getVarVal("screenCaptureOnTestStepFail");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("screenCaptureOnTestStepFail is not a String");
        }
    }

    /**
     * Returns the screenCaptureTrim testConfig.properties value.
     *
     * @return the screenCaptureTrim value.
     */
    public static String getScreenCaptureTrim() {
        Object value = getVarVal("screenCaptureTrim");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("screenCaptureTrim is not a String");
        }
    }

    /**
     * Returns the screenCaptureDirectory testConfig.properties value.
     *
     * @return the screenCaptureDirectory value.
     */
    public static String getScreenCaptureDirectory() {
        Object value = getVarVal("screenCaptureDirectory");
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("screenCaptureDirectory is not a String");
        }
    }

    /**
     * Returns the browser testConfig.properties value.
     *
     * @return the browser value.
     */
    public static String getBrowser() {
        Object value = getVarVal("browser");
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("browser is not a String");
        }
    }

    /**
     * Returns the headless testConfig.properties value.
     *
     * @return the headless value.
     */
    public static String getHeadless() {
        Object value = getVarVal("headless");
        value = value.toString(); // convert to type String
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("headless is not a String");
        }
    }

}

/**
 * This class represents a global variable with a name, type, and value.
 */
class GlobalVariable {
    private String name;
    private String type;
    private Object value;

    /**
     * Constructs a new GlobalVariable with the specified name, type, and value.
     *
     * @param name the name of the global variable
     * @param type the type of the global variable
     * @param value the value of the global variable
     */
    public GlobalVariable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * Returns the name of this global variable.
     *
     * @return the name of this global variable
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this global variable.
     *
     * @return the type of this global variable
     */
    public String getType() {
        return type;
    }
    
    /**
     * Returns the value of this global variable.
     *
     * @return the value of this global variable
     */
    public Object getValue() {
        return value;
    }
}