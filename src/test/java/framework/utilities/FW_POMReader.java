package framework.utilities;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import java.io.FileReader;

/**
 * This class provides methods for reading the POM file.
 */
public class FW_POMReader {

    /**
     * Retrieves the value of a specified property from the POM file.
     *
     * @param propertyName The name of the property to retrieve.
     *
     * @return The value of the specified property, or null if the property is not found.
     */
    public static String getPomProperty(String propertyName) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            Model model = reader.read(new FileReader("./pom.xml"));
            return model.getProperties().getProperty(propertyName);
        } catch (Exception e) {
            return "pom.xml not found";
        }
    }

}