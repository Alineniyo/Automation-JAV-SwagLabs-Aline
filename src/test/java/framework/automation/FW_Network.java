package framework.automation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a wrapper around common networking utility methods.
 */
public class FW_Network {

    /**
     * This method checks if a host is reachable and returns a verbose result.
     *
     * @param host The host to check.
     * @param pingTimeout The timeout for the ping in milliseconds.
     *
     * @return A string including [PASS] or [FAIL] along with specific result details.
     */
    public String pingTest(String host, int pingTimeout) {

        // Extract the hostname from the host string
        String targetHostname = hostname(host);
    
        // If the hostname contains "[Fail]", return result immediately
        if (targetHostname.toUpperCase().contains("[FAIL]")) {
            return targetHostname;
        }

        // Perform the ping operation and get the results
        HashMap<Integer, HashMap<String, String>> combinedPingResults = ping(targetHostname, pingTimeout);
    
        // Check if the ping operation returned any results
        if (combinedPingResults != null && !combinedPingResults.isEmpty()) {

            // Get the individual ping result for the first ICMP sequence
            HashMap<String, String> individualPingResult = combinedPingResults.get(0);
    
            // Check if there is a result for the first ICMP sequence
            if (individualPingResult != null) {

                // Get the status of the ping operation
                String pingStatus = individualPingResult.get("status").toUpperCase();
    
                // Check if the ping operation was successful
                if (pingStatus.contains("[PASS]")) {

                    // Get the IP address and time from the individual ping result
                    String ipAddress = individualPingResult.get("ip");
                    String time = individualPingResult.get("time");
    
                    // Return a success message with the ping details
                    return String.format("[Pass] - Pinged host '%s' at IP:%s within %s milliseconds.", targetHostname, ipAddress, time);
                } else {
                    // Get the error details from the individual ping result
                    String errorDetails = individualPingResult.get("details");
    
                    // Return a failure message with the error details
                    return String.format("[Fail] - Tried to ping host '%s' but encountered error '%s'.", targetHostname, errorDetails);
                }
            }
        }
    
        // Return a failure message for an unknown error
        return String.format("[Fail] - Tried to ping host '%s' but encountered an unknown error.", targetHostname);
    }

    /**
     * This method checks the traceroute to a host and returns a verbose result.
     *
     * @param host The host to check.
     * @param tracerouteTimeout The timeout for the traceroute in milliseconds.
     *
     * @return A string including [PASS] or [FAIL] along with specific result details.
     */
    public String tracerouteTest(String host, int tracerouteTimeout) {

        // Extract the hostname from the host string
        String targetHostname = hostname(host);

        // If the hostname contains "[Fail]", return result immediately
        if (targetHostname.toUpperCase().contains("[FAIL]")) {
            return targetHostname;
        }

        // Perform the traceroute operation and get the results
        HashMap<Integer, HashMap<String, String>> combinedTracerouteResults = traceroute(targetHostname, tracerouteTimeout);

        // Check if the traceroute operation returned any results
        if (combinedTracerouteResults != null && !combinedTracerouteResults.isEmpty()) {

            // Get the individual traceroute result for the first hop
            HashMap<String, String> individualTracerouteResult = combinedTracerouteResults.get(0);

            // Check if there is a result for the first hop
            if (individualTracerouteResult != null) {

                // Get the status of the traceroute operation
                String tracerouteStatus = individualTracerouteResult.get("status").toUpperCase();

                // Check if the traceroute operation was successful
                if (tracerouteStatus.contains("[PASS]")) {

                    // Get the IP address and time from the individual traceroute result
                    String ipAddress = individualTracerouteResult.get("ip");
                    String time = individualTracerouteResult.get("time");

                    // Return a success message with the traceroute details
                    return String.format("[Pass] - Traceroute to host '%s' reached IP:%s within '%s' milliseconds.", targetHostname, ipAddress, time);
                } else {
                    // Get the error details from the individual traceroute result
                    String errorDetails = individualTracerouteResult.get("details");

                    // Return a failure message with the error details
                    return String.format("[Fail] - Tried to traceroute to host '%s' within '%s' milliseconds but encountered error '%s'.", targetHostname, tracerouteTimeout, errorDetails);
                }
            }
        }

        // Return a failure message for an unknown error
        return String.format("[Fail] - Tried to traceroute to host '%s' within '%s' milliseconds but encountered an unknown error.", targetHostname, tracerouteTimeout);
    }

    /**
     * This method chechs for network jitter.
     *
     * @param host The host to check.
     * @param pingCount The number of times to ping the host.
     * @param pingTimeout The timeout for the ping in milliseconds.
     * @param acceptableDeviation The acceptable deviation in milliseconds.
     *
     * @return A string including [PASS] or [FAIL] along with specific result details.
     */
    public String jitterTest(String host, int pingCount, int pingTimeout, int acceptableDeviation) {
        // Perform the jitter test and get the results
        HashMap<String, String> jitterResult = jitter(host, pingCount, pingTimeout);
    
        // Get the status of the jitter test
        String jitterStatus = jitterResult.get("status").toUpperCase();
    
        // Check if the jitter test was successful
        if (jitterStatus.contains("[PASS]")) {
            // Get the standard deviation from the jitter result
            double standardDeviation = Double.parseDouble(jitterResult.get("standard_deviation"));
    
            // Check if the standard deviation is within the acceptable range
            if (standardDeviation <= acceptableDeviation) {
                // Return a success message with the standard deviation
                return String.format("[Pass] - Jitter test passed with standard deviation of %s milliseconds.", standardDeviation);
            } else {
                // Return a failure message with the standard deviation
                return String.format("[Fail] - Jitter test failed with standard deviation of %s milliseconds.", standardDeviation);
            }
        } else {
            // Return a failure message with the jitter test status
            return String.format("[Fail] - Jitter test failed with status '%s'.", jitterStatus);
        }
    }

    /**
     * This method checks the download speed of a file.
     *
     * @return A string including [PASS] or [FAIL] along with specific result details.
     */
    public String speedTest() {
        try {
            URI uri = new URI("http://speedtest.tele2.net/1MB.zip");
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
    
            // Start timer
            Instant start = Instant.now();
    
            // Download file
            try (InputStream in = connection.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    // Do nothing, just read the file
                }
                if (bytesRead > 0) {
                    // Do something
                }
            }
    
            // Stop timer
            Instant end = Instant.now();
    
            // Calculate speed
            long dataSizeKB = connection.getContentLengthLong() / 1024;
            long timeTakenMS = Duration.between(start, end).toMillis();
            double speedKBps = (double) dataSizeKB / (timeTakenMS / 1000.0);
    
            String results = String.format("Download speed: %.2f KB/s", speedKBps);
            return "[Pass] - " + results;
        } catch (Exception e) {
            e.printStackTrace();
            return "[Fail] - Error performing speed test";
        }
    }

    /**
     * Performs a jitter test by pinging a host multiple times and calculating the standard deviation of the ping times.
     *
     * @param host The host to ping.
     * @param pingCount The number of times to ping the host.
     * @param pingTimeout The timeout in milliseconds for each ping.
     * 
     * @return A HashMap with the following keys:
     *         "status" - "[Pass]" if all pings were successful, "[Fail]" otherwise.
     *         "host" - The host that was pinged.
     *         "ping_count" - The number of times the host was pinged.
     *         "standard_deviation" - The standard deviation of the ping times, formatted to two decimal places.
     */
    public HashMap<String, String> jitter(String host, int pingCount, int pingTimeout) {
        List<Double> pingTimes = new ArrayList<>();
        int successfulPings = 0;
    
        for (int i = 0; i < pingCount; i++) {
            HashMap<Integer, HashMap<String, String>> pingResult = this.ping(host, pingTimeout);
            if (pingResult.get(0).get("status").equals("[Pass]")) {
                successfulPings++;
                pingTimes.add(Double.parseDouble(pingResult.get(0).get("time")));
            }
        }
    
        double mean = pingTimes.stream().mapToDouble(val -> val).average().orElse(0.0);
        double standardDeviation = Math.sqrt(pingTimes.stream().mapToDouble(val -> (val - mean) * (val - mean)).average().orElse(0.0));
    
        HashMap<String, String> result = new HashMap<>();
        result.put("status", successfulPings == pingCount ? "[Pass]" : "[Fail]");
        result.put("host", host);
        result.put("ping_count", String.valueOf(pingCount));
        result.put("standard_deviation", String.format("%.2f", standardDeviation));
    
        return result;
    }

    /**
     * Pings a specified host and returns a detailed report of the ping response.
     * The report includes the status, host, number of bytes, IP address, ICMP sequence, TTL, and response time.
     *
     * @param host The host to ping.
     * @param pingTimeout The timeout for the ping in milliseconds.
     * 
     * @return A HashMap where the key is the ICMP sequence number and the value is another HashMap with keys:
     *         "status" - "[Pass]" if the ping was successful, "[Fail]" otherwise.
     *         "host" - The host that was pinged.
     *         "bytes" - The number of bytes in the ping response.
     *         "ip" - The IP address from the ping response.
     *         "icmp_seq" - The ICMP sequence number from the ping response.
     *         "ttl" - The TTL from the ping response.
     *         "time" - The response time from the ping response.
     *         "details" - Additional details about the ping response or any errors that occurred.
     */
    public HashMap<Integer, HashMap<String, String>> ping(String host, int pingTimeout) {
        // Initialize result HashMap to store the ping response details
        HashMap<Integer, HashMap<String, String>> result = new HashMap<>();
    
        try {
            // Create a new process to execute the ping command
            ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "1", "-W", String.valueOf(pingTimeout), host);
            Process process = processBuilder.start();
    
            // Create a BufferedReader to read the output of the ping command
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    
            // Define a pattern to extract the required details from the ping output
            Pattern pingOutputPattern = Pattern.compile("(\\d+) bytes from (\\S+): icmp_seq=(\\d+) ttl=(\\d+) time=(\\d+\\.?\\d*) ms");
    
            String outputLine;
            // Read the output of the ping command line by line
            while ((outputLine = stdInput.readLine()) != null) {
                // Process only the lines that contain "icmp_seq=0"
                if (outputLine.contains("icmp_seq=0")) {
                    // Match the line against the pattern
                    Matcher pingOutputMatcher = pingOutputPattern.matcher(outputLine);
                    // If the line matches the pattern, extract the required details
                    if (pingOutputMatcher.find()) {
                        int seq = Integer.parseInt(pingOutputMatcher.group(3));
    
                        // Prepare the HashMap to return the ping response details
                        HashMap<String, String> pingOutput = new HashMap<>();
    
                        // Build hashmap with ping details
                        pingOutput.put("status", "[Pass]");
                        pingOutput.put("host", host);
                        pingOutput.put("bytes", pingOutputMatcher.group(1));
                        pingOutput.put("ip", pingOutputMatcher.group(2));
                        pingOutput.put("icmp_seq", pingOutputMatcher.group(3));
                        pingOutput.put("ttl", pingOutputMatcher.group(4));
                        pingOutput.put("time", pingOutputMatcher.group(5));
                        pingOutput.put("details", "");
    
                        // Add the HashMap to the result HashMap with the sequence number as the key
                        result.put(seq, pingOutput);
                    }
                }
            }
    
            // Read the error output of the ping command line by line
            while ((outputLine = stdError.readLine()) != null) {
                HashMap<String, String> pingOutput = new HashMap<>();
                pingOutput.put("status", "[Fail]");
    
                // Process only the lines that contain "unknown host"
                if (outputLine.toLowerCase().contains("unknown host")) {
                    pingOutput.put("details", "Unknown Host: " + host);
                } else if (outputLine.toLowerCase().contains("some other error")) {
                    pingOutput.put("details", "Some Other Error: " + outputLine);
                } else {
                    pingOutput.put("details", "Unrecognized Error: " + outputLine);
                }
    
                result.put(0, pingOutput);
            }
    
        } catch (Exception e) {
            HashMap<String, String> pingOutput = new HashMap<>();
            pingOutput.put("status", "[Fail]");
            pingOutput.put("details", "Exception: " + e.getMessage());
            result.put(0, pingOutput);
        }
    
        // Return the result HashMap
        return result;
    }

    /**
     * Executes a traceroute command to the specified host and returns the result.
     * The report includes hop, hostname, ip, and time.
     *
     * @param host The host to which the traceroute command will be executed.
     * @param traceTimeout The timeout for the traceroute in milliseconds.
     * 
     * @return A HashMap where the key is the hop number and the value is another HashMap with keys:
     *          "status" - "[Pass]" if the ping was successful, "[Fail]" otherwise.
     *          "host" - The host that was pinged.
     *          "hop" - The hop number.
     *          "hostname" - The hostname of the hop.
     *          "ip" - The IP address of the hop.
     *          "time" - The time taken to reach the hop.
     *          "details" - Additional details about the traceroute response or any errors that occurred.
     */
    public HashMap<Integer, HashMap<String, String>> traceroute(String host, long traceTimeout) {
        // Initialize result HashMap to store the traceroute response details
        final HashMap<Integer, HashMap<String, String>> result = new HashMap<>();
    
        // Create a new thread to execute the traceroute command
        Thread tracerouteThread = new Thread(new Runnable() {

            // Run method to execute the traceroute command
            public void run() {
                try {
                    // Create a new process to execute the traceroute command
                    ProcessBuilder processBuilder = new ProcessBuilder("traceroute", "-d", host);
                    final Process process = processBuilder.start();
    
                    // Create a BufferedReader to read the output of the traceroute command
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    
                    // Define a pattern to extract the required details from the traceroute output
                    Pattern tracerouteOutputPattern = Pattern.compile("(\\d+)\\s+(\\S+)\\s+\\((\\S+)\\)\\s+(\\d+\\.?\\d*) ms");
    
                    String outputLine;
                    // Read the output of the traceroute command line by line
                    while ((outputLine = stdInput.readLine()) != null) {
                        // Match the line against the pattern
                        Matcher tracerouteOutputMatcher = tracerouteOutputPattern.matcher(outputLine);
                        // If the line matches the pattern, extract the required details
                        if (tracerouteOutputMatcher.find()) {
                            int hop = Integer.parseInt(tracerouteOutputMatcher.group(1));
    
                            // Prepare the HashMap to return the traceroute response details
                            HashMap<String, String> tracerouteOutput = new HashMap<>();

                            // Build hashmap with traceroute details
                            tracerouteOutput.put("status", "[Pass]");
                            tracerouteOutput.put("host", host);
                            tracerouteOutput.put("hop", tracerouteOutputMatcher.group(1));
                            tracerouteOutput.put("hostname", tracerouteOutputMatcher.group(2));
                            tracerouteOutput.put("ip", tracerouteOutputMatcher.group(3));
                            tracerouteOutput.put("time", tracerouteOutputMatcher.group(4));
                            tracerouteOutput.put("details", "");
    
                            // Print the traceroute details to the console (For Debugging)
                            System.out.println("Hop: " + tracerouteOutput.get("hop") + ", Hostname: " + tracerouteOutput.get("hostname") + ", IP: " + tracerouteOutput.get("ip") + ", Time: " + tracerouteOutput.get("time"));

                            // Add the HashMap to the result HashMap with the hop number as the key
                            result.put(hop, tracerouteOutput);
                        }
                    }

                    StringBuilder errorDetails = new StringBuilder();

                    // Read the error output of the traceroute command line by line
                    while ((outputLine = stdError.readLine()) != null) {
                        errorDetails.append(outputLine).append(" ");
                    }
                
                    HashMap<String, String> tracerouteOutput = new HashMap<>();
                    tracerouteOutput.put("status", "[Fail]");
                
                    // Check for known error messages
                    if (errorDetails.toString().contains("Name or service not known")) {
                        tracerouteOutput.put("details", "Host not found: " + errorDetails.toString());
                    } else if (errorDetails.toString().contains("Operation not permitted")) {
                        tracerouteOutput.put("details", "Operation not permitted, are you running as root?: " + errorDetails.toString());
                    } else {
                        tracerouteOutput.put("details", "Unrecognized Error: " + errorDetails.toString());
                    }
                
                    result.put(0, tracerouteOutput);
    
                } catch (Exception e) {
                    HashMap<String, String> tracerouteOutput = new HashMap<>();
                    tracerouteOutput.put("status", "[Fail]");
                    tracerouteOutput.put("details", "Exception: " + e.getMessage());
                    result.put(0, tracerouteOutput);
                }
            }
        });
    
        // Start the traceroute thread
        tracerouteThread.start();
    
        try {
            tracerouteThread.join(traceTimeout);
    
            if (tracerouteThread.isAlive()) {
                tracerouteThread.interrupt();
                HashMap<String, String> tracerouteOutput = new HashMap<>();
                tracerouteOutput.put("status", "[Fail]");
                tracerouteOutput.put("details", "Timeout: Traceroute command did not complete within the specified time.");
                result.put(0, tracerouteOutput);
            }
        } catch (InterruptedException e) {
            HashMap<String, String> tracerouteOutput = new HashMap<>();
            tracerouteOutput.put("status", "[Fail]");
            tracerouteOutput.put("details", "Exception: " + e.getMessage());
            result.put(0, tracerouteOutput);
        }
    
        return result;
    }

    /**
     * This method resolves a host to an IP address.
     *
     * @param host The host to resolve.
     *
     * @return The IP address of the host or null if the host cannot be resolved.
     */
    public String ip(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method gets the IP info details of a host.
     *
     * @param host The host or ip to get the IP info details.
     *
     * @return A HashMap where the key is the hop number and the value is another HashMap with keys:
     *          "status"    [Pass] if the info lookup was successful, [Fail] otherwise.
     *          "ip"        208.113.156.58
     *          "hostname"  iad1-cr-2.sd.dreamhost.com
     *          "city"      Ashburn
     *          "region"    Virginia
     *          "country"   US
     *          "loc"       39.0437,-77.4875 (latitude, longitude)
     *          "org"       AS26347 New Dream Network, LLC
     *          "postal"    20149
     *          "timezone"  America/New_York
     *          "readme"    https://ipinfo.io/missingauth
     *          "details"   Additional details about the IP info response or any errors that occurred.
     */
    public Map<Integer, Map<String, String>> ipInfo(String host) {

        // Get the IP address of the host
        String ipAddress = ip(host);
    
        // Initialize result HashMap to store the IP info details
        HashMap<Integer, Map<String, String>> result = new HashMap<>();
    
        try {
            // Create a new process to execute the curl command
            ProcessBuilder processBuilder = new ProcessBuilder("curl", "http://ipinfo.io/" + ipAddress);
            Process process = processBuilder.start();
    
            // Create a BufferedReader to read the output of the curl command
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
            // Prepare the HashMap to return the IP info details
            HashMap<String, String> ipInfoOutput = new HashMap<>();

            // Set default values
            ipInfoOutput.put("status", "[Pass]");
            ipInfoOutput.put("details", "");

            Pattern ipInfoOutpuPattern = Pattern.compile("\"(.*?)\"\\s*:\\s*(\".*?\"|\\d+)");

            String outputLine;
            // Read the output of the curl command line by line
            while ((outputLine = stdInput.readLine()) != null) {

                // Match the line against the ipInfoOutpuPattern
                Matcher ipInfoOutoutMatcher = ipInfoOutpuPattern.matcher(outputLine);
                // If the line matches the ipInfoOutpuPattern, extract the required details
                if (ipInfoOutoutMatcher.find()) {

                    // Add the key-value pair to the response HashMap
                    String key = ipInfoOutoutMatcher.group(1);
                    String value = ipInfoOutoutMatcher.group(2);
                    ipInfoOutput.put(key, value);
                }
            }

            // Check if "message" contains "Please provide a valid IP address"
            if (ipInfoOutput.get("status") != null && ipInfoOutput.get("status").contains("404")) {

                // Set status to [Fail] and add details
                ipInfoOutput.put("status", "[Fail]");
                ipInfoOutput.put("details", String.format("404 Error - Please provide a valid host or IP address - '%s'.", host));
            }
    
            // Add the HashMap to the result HashMap with the sequence number as the key
            result.put(0, ipInfoOutput);
    
        // Read the error output of the curl command line by line
        } catch (IOException e) {
            HashMap<String, String> ipInfoOutput = new HashMap<>();
            ipInfoOutput.put("status", "[Fail]");
            ipInfoOutput.put("details", "Exception: " + e.getMessage());
            result.put(0, ipInfoOutput);
        }
    
        return result;
    }

    /**
     * This method resolves a host or IP to a hostname.
     *
     * @param host The host or IP to resolve.
     *
     * @return The hostname of the host or null if the host cannot be resolved.
     */
    public String hostname(String host) {
        try {
            String hostName;
    
            if (host.startsWith("http://") || host.startsWith("https://")) {
                URI uri = new URI(host);
                hostName = uri.getHost();
            } else {
                hostName = host;
            }
    
            InetAddress address = InetAddress.getByName(hostName);
    
            return address.getHostName();
        } catch (UnknownHostException e) {
            return "[Fail] - Unknown Host '" + host + "'";
        } catch (Exception e) {
            return "Error: An unexpected error occurred: " + e.getMessage();
        }
    }

}
