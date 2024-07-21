package framework.audits;

// Import class packages for this Audit Object (AO)

/**
 * This class performs a sandbox audit for experimentation.
 */
public class FW_Audit_Sandbox {

    /**
     * This method is a sandbox for developing and testing audit concepts.
     *
     * @param initialResult Any initial result details to include in the audit.
     *
     * @return Consolidated audit result details.
     */
    public String runAuditSandbox (String initialResult) {

        String auditDescription = "Audit - Sandbox"; // Description of the audit
        String tabString = "    "; // Tab string for formatting
        StringBuilder auditResult;
    
        if (initialResult == null || initialResult.length() == 0) {
            auditResult = new StringBuilder(auditDescription);
        } else {
            auditResult = new StringBuilder(initialResult + " - " + auditDescription);
        }

        auditResult.append("\n").append(tabString + "[Pass] - Hello from FW_Audit_Sandbox!");
        auditResult.append("\n").append(tabString + "[Fail] - 123 Testing ABC.");

        if (auditResult.toString().contains("[Fail]")) {
            auditResult.insert(0, "[Audit Fail] - ");
        } else {
            auditResult.insert(0, "[Audit Pass] - ");
        }

        return auditResult.toString();
    }
    
}
