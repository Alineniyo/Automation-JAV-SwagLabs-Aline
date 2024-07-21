package framework.audits;

// Import class packages for this Audit Object (AO)
import framework.automation.FW_Network; // Commonly used networking utilities

/**
 * This class performs an audit of the system's connectivity.
 */
public class FW_Audit_Connectivity {

    /**
     * This method performs an audit of the system's connectivity.
     *
     * @param initialResult Any initial result details to include in the audit.
     *
     * @return Consolidated audit result details.
     */
    public String runAuditConnectivity (String initialResult) {

        String host = "www.gregpaskal.com";
        int pingCount = 10;
        int pingTimeout = 5000;
        int acceptableDeviation = 1000;

        FW_Network fwNetwork = new FW_Network(); // Instantiate the FW_Network class

        String auditDescription = "Audit - Connectivity"; // Description of the audit
        String tabString = "    "; // Tab string for formatting
        StringBuilder auditResult;

        if (initialResult == null || initialResult.length() == 0) {
            auditResult = new StringBuilder(auditDescription);
        } else {
            auditResult = new StringBuilder(initialResult + " - " + auditDescription);
        }

        auditResult.append("\n").append(tabString + fwNetwork.pingTest(host, pingTimeout));
        auditResult.append("\n").append(tabString + fwNetwork.jitterTest(host,pingCount, pingTimeout, acceptableDeviation));
        // auditResult.append("\n").append(tabString + fwNetwork.speedTest());

        if (auditResult.toString().contains("[Fail]")) {
            auditResult.insert(0, "[Audit Fail] - ");
        } else {
            auditResult.insert(0, "[Audit Pass] - ");
        }

        return auditResult.toString();
    }
    
}
