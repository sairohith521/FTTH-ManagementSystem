package ftth.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailService {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String API_TOKEN = dotenv.get("MAILTRAP_API_TOKEN");
    private static final String INBOX_ID = dotenv.get("MAILTRAP_INBOX_ID");

    public void sendNoOLTEmail(int pincode) {
        String subject = "OLT Capacity Full - Pincode " + pincode;
        String text = "Pincode " + pincode
                + " has no available ports. Please install additional splitters or OLT.";

        String jsonBody = buildJson(
                "alert@aaha-telecom.fake",
                "Aaha Telecom",
                "olt-provider@network.fake",
                subject,
                text
        );

        sendRequest(jsonBody, "OLT Alert");
    }

    public void sendOrderConfirmationEmail(String toEmail, String name, int pincode, String service, int price) {
        String subject = "Order Confirmed - Aaha Telecom FTTH";
        String text = "Dear " + name + ",\n\n"
                + "Your FTTH connection is confirmed!\n"
                + "Service: " + service + "\n"
                + "Price: Rs." + price + "/month\n"
                + "Pincode: " + pincode + "\n\n"
                + "Your ONT will be shipped tomorrow.\n\n"
                + "Thank you,\nAaha Telecom";

        String jsonBody = buildJson(
                "noreply@aaha-telecom.fake",
                "Aaha Telecom",
                toEmail,
                subject,
                text
        );

        sendRequest(jsonBody, "Order Confirmation");
    }

    public void sendBillEmail(String name, String custID, String billNo,
                              String service, int planCharge, int gst, int total,
                              String billDate, String dueDate) {
        String subject = "Your Aaha Telecom Bill - " + billNo;
        String text = "Dear " + name + ",\n\n"
                + "Your Aaha Telecom bill has been generated.\n\n"
                + "Bill No     : " + billNo + "\n"
                + "Customer ID : " + custID + "\n"
                + "Service     : " + service + "\n"
                + "Bill Date   : " + billDate + "\n"
                + "Due Date    : " + dueDate + "\n\n"
                + "Plan Charge : Rs." + planCharge + "\n"
                + "GST (18%)   : Rs." + gst + "\n"
                + "----------------------\n"
                + "Total Due   : Rs." + total + "\n\n"
                + "Please pay by " + dueDate + " to avoid interruption.\n\n"
                + "Thank you,\nAaha Telecom";

        String body = buildJson(
                "billing@aaha-telecom.fake",
                "Aaha Telecom Billing",
                "customer@inbox.fake",
                subject,
                text
        );

        sendRequest(body, "Bill Email");
    }

    private String buildJson(String fromEmail, String fromName,
                             String toEmail, String subject, String text) {
        return "{"
                + "\"from\":{\"email\":\"" + escapeJson(fromEmail) + "\",\"name\":\"" + escapeJson(fromName) + "\"},"
                + "\"to\":[{\"email\":\"" + escapeJson(toEmail) + "\"}],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"text\":\"" + escapeJson(text) + "\""
                + "}";
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
    }

    private void sendRequest(String jsonBody, String emailType) {
        if (API_TOKEN == null || API_TOKEN.isBlank()) {
            System.out.println("ERROR: MAILTRAP_API_TOKEN is not set.");
            return;
        }
        if (INBOX_ID == null || INBOX_ID.isBlank()) {
            System.out.println("ERROR: MAILTRAP_INBOX_ID is not set.");
            return;
        }

        String apiUrl = "https://sandbox.api.mailtrap.io/api/send/" + INBOX_ID;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + API_TOKEN);

            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int code = con.getResponseCode();

            if (code == 200 || code == 201) {
                System.out.println("SUCCESS: " + emailType + " email sent. Check Mailtrap inbox.");
            } else {
                System.out.println("ERROR: Failed (" + emailType + ") - Code: " + code);
            }

        } catch (Exception e) {
            System.out.println("ERROR: Error sending " + emailType + ": " + e.getMessage());
        }
    }
}
