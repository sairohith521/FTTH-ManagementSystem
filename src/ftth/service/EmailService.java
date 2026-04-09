package ftth.service;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailService {

    private static final String API_TOKEN = "c06d61c2bb4d0b808e44cfc854ebf578";
    private static final String INBOX_ID  = "4517662"; // your Mailtrap inbox ID

    // ✅ Correct Mailtrap Sandbox API URL
    private static final String API_URL = "https://sandbox.api.mailtrap.io/api/send/" + INBOX_ID;

    // ✅ Alert when OLT has no available ports
    public void sendNoOLTEmail(int pincode) {
        String body = "{"
            + "\"from\": {\"email\": \"alert@aaha-telecom.fake\", \"name\": \"Aaha Telecom\"},"
            + "\"to\": [{\"email\": \"olt-provider@network.fake\"}],"
            + "\"subject\": \"OLT Capacity Full - Pincode " + pincode + "\","
            + "\"text\": \"Pincode " + pincode + " has no available ports. Please install additional splitters or OLT.\""
            + "}";

        sendRequest(body, "OLT Alert");
    }

    // ✅ Confirmation when order is placed
    public void sendOrderConfirmationEmail(String name, int pincode, String service, int price) {
        String body = "{"
            + "\"from\": {\"email\": \"noreply@aaha-telecom.fake\", \"name\": \"Aaha Telecom\"},"
            + "\"to\": [{\"email\": \"customer@inbox.fake\"}],"
            + "\"subject\": \"Order Confirmed - Aaha Telecom FTTH\","
            + "\"text\": \"Dear " + name + ",\\n\\nYour FTTH connection is confirmed!\\nService: " + service + "\\nPrice: Rs." + price + "/month\\nPincode: " + pincode + "\\n\\nYour ONT will be shipped tomorrow.\\n\\nThank you,\\nAaha Telecom\""
            + "}";

        sendRequest(body, "Order Confirmation");
    }
    
    public void sendBillEmail(String name, String custID, String billNo,
                              String service, int planCharge, int gst, int total,
                              String billDate, String dueDate) {
        String text = "Dear " + name + ","
            + "\\n\\nYour Aaha Telecom bill has been generated."
            + "\\n\\nBill No     : " + billNo
            + "\\nCustomer ID : " + custID
            + "\\nService     : " + service
            + "\\nBill Date   : " + billDate
            + "\\nDue Date    : " + dueDate
            + "\\n\\nPlan Charge : Rs." + planCharge
            + "\\nGST (18%)   : Rs." + gst
            + "\\n----------------------"
            + "\\nTotal Due   : Rs." + total
            + "\\n\\nPlease pay by " + dueDate + " to avoid interruption."
            + "\\n\\nThank you,\\nAaha Telecom";

        String body = "{"
            + "\"from\": {\"email\": \"billing@aaha-telecom.fake\", \"name\": \"Aaha Telecom Billing\"},"
            + "\"to\": [{\"email\": \"customer@inbox.fake\"}],"
            + "\"subject\": \"Your Aaha Telecom Bill - " + billNo + "\","
            + "\"text\": \"" + text + "\""
            + "}";

        sendRequest(body, "Bill Email");
    }

    // ✅ Internal method - correct Authorization header format
    private void sendRequest(String jsonBody, String emailType) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + API_TOKEN); // ✅ Fixed header

            OutputStream os = con.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = con.getResponseCode();
            if (code == 200 || code == 201) {
                System.out.println("✅ Email sent! (" + emailType + ") Check: https://mailtrap.io");
            } else {
                System.out.println("❌ Email failed. Response code: " + code);
            }

        } catch (Exception e) {
            System.out.println("❌ Error sending email: " + e.getMessage());
        }
    }
}
