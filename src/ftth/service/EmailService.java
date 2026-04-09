package ftth.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailService {

    // 🔐 Use environment variable (set this in your system)
    private static final Dotenv dotenv = Dotenv.configure()
        .ignoreIfMissing()
        .load();
    private static final String API_TOKEN = dotenv.get("MAILTRAP_API_TOKEN");
    private static final String INBOX_ID  = dotenv.get("MAILTRAP_INBOX_ID");

    private static final String API_URL =
            "https://sandbox.api.mailtrap.io/api/send/" + INBOX_ID;

    // ✅ Send OLT Alert
    public void sendNoOLTEmail(int pincode) {
        String subject = "OLT Capacity Full - Pincode " + pincode;

        String text = "Pincode " + pincode +
                " has no available ports. Please install additional splitters or OLT.";

        String jsonBody = buildJson(
                "alert@aaha-telecom.fake",
                "Aaha Telecom",
                "olt-provider@network.fake",
                subject,
                text
        );

        sendRequest(jsonBody, "OLT Alert");
    }

    // ✅ Send Order Confirmation
    public void sendOrderConfirmationEmail(String toEmail, String name,
                                           int pincode, String service, int price) {

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

    // ✅ Common JSON builder (reusable)
    private String buildJson(String fromEmail, String fromName,
                             String toEmail, String subject, String text) {

        return "{"
                + "\"from\": {\"email\": \"" + fromEmail + "\", \"name\": \"" + fromName + "\"},"
                + "\"to\": [{\"email\": \"" + toEmail + "\"}],"
                + "\"subject\": \"" + subject + "\","
                + "\"text\": \"" + text.replace("\n", "\\n") + "\""
                + "}";
    }

    // ✅ Core API call
    private void sendRequest(String jsonBody, String emailType) {
        try {
            if (API_TOKEN == null || API_TOKEN.isEmpty()) {
                System.out.println("❌ API Token not set. Please set MAILTRAP_API_TOKEN");
                return;
            }

            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + API_TOKEN);

            OutputStream os = con.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = con.getResponseCode();

            if (code == 200 || code == 201) {
                System.out.println("✅ " + emailType + " email sent! Check Mailtrap inbox.");
            } else {
                System.out.println("❌ Failed (" + emailType + ") - Code: " + code);
            }

        } catch (Exception e) {
            System.out.println("❌ Error sending " + emailType + ": " + e.getMessage());
        }
    }
}