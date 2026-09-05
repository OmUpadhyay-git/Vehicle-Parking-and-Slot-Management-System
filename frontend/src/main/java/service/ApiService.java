package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiService {

    private String baseUrl = "http://localhost:8000";

    public ApiService() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // ============ AUTH ============

    public String login(String username, String password) {
        String json = "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password) + "\"}";
        return sendRequest("POST", "/login", json);
    }

    // ============ VEHICLES ============

    public String createVehicle(String vehicleNumber, String vehicleType, String ownerName, String ownerPhone) {
        String json = "{\"vehicle_number\":\"" + escapeJson(vehicleNumber)
                + "\",\"vehicle_type\":\"" + escapeJson(vehicleType)
                + "\",\"owner_name\":\"" + escapeJson(ownerName)
                + "\",\"owner_phone\":\"" + escapeJson(ownerPhone) + "\"}";
        return sendRequest("POST", "/vehicles", json);
    }

    public String getVehicles() {
        return sendRequest("GET", "/vehicles", null);
    }

    public String getVehicle(int vehicleId) {
        return sendRequest("GET", "/vehicles/" + vehicleId, null);
    }

    public String searchVehicle(String vehicleNumber) {
        return sendRequest("GET", "/vehicles/search/" + vehicleNumber, null);
    }

    public String deleteVehicle(int vehicleId) {
        return sendRequest("DELETE", "/vehicles/" + vehicleId, null);
    }

    // ============ SLOTS ============

    public String createSlot(String slotNumber, String vehicleType) {
        String json = "{\"slot_number\":\"" + escapeJson(slotNumber)
                + "\",\"vehicle_type\":\"" + escapeJson(vehicleType) + "\"}";
        return sendRequest("POST", "/slots", json);
    }

    public String getSlots() {
        return sendRequest("GET", "/slots", null);
    }

    public String getAvailableSlots() {
        return sendRequest("GET", "/slots/available", null);
    }

    public String getOccupiedSlots() {
        return sendRequest("GET", "/slots/occupied", null);
    }

    public String updateSlot(int slotId, String status) {
        String json = "{\"status\":\"" + escapeJson(status) + "\"}";
        return sendRequest("PUT", "/slots/" + slotId, json);
    }

    // ============ PARKING ============

    public String parkVehicle(String vehicleNumber, int slotId) {
        String json = "{\"vehicle_number\":\"" + escapeJson(vehicleNumber)
                + "\",\"slot_id\":" + slotId + "}";
        return sendRequest("POST", "/parking/entry", json);
    }

    public String parkVehicleDirect(String vehicleNumber, int slotId, String vehicleType, String ownerName, String ownerPhone) {
        String json = "{\"vehicle_number\":\"" + escapeJson(vehicleNumber)
                + "\",\"slot_id\":" + slotId
                + ",\"vehicle_type\":\"" + escapeJson(vehicleType != null ? vehicleType : "")
                + "\",\"owner_name\":\"" + escapeJson(ownerName != null ? ownerName : "")
                + "\",\"owner_phone\":\"" + escapeJson(ownerPhone != null ? ownerPhone : "") + "\"}";
        return sendRequest("POST", "/parking/entry", json);
    }

    public String exitVehicle(int recordId, String paymentMethod) {
        String json = "{\"record_id\":" + recordId
                + ",\"payment_method\":\"" + escapeJson(paymentMethod) + "\"}";
        return sendRequest("POST", "/parking/exit", json);
    }

    public String getActiveParking() {
        return sendRequest("GET", "/parking/active", null);
    }

    public String getParkingHistory() {
        return sendRequest("GET", "/parking/history", null);
    }

    // ============ PAYMENTS ============

    public String createPayment(int recordId, double amount, String paymentMethod) {
        String json = "{\"record_id\":" + recordId
                + ",\"amount\":" + amount
                + ",\"payment_method\":\"" + escapeJson(paymentMethod) + "\"}";
        return sendRequest("POST", "/payments", json);
    }

    public String getPayments() {
        return sendRequest("GET", "/payments", null);
    }

    public String getPayment(int paymentId) {
        return sendRequest("GET", "/payments/" + paymentId, null);
    }

    // ============ DASHBOARD ============

    public String getDashboard() {
        return sendRequest("GET", "/dashboard", null);
    }

    // ============ USERS / STAFF ============

    public String getUsers() {
        return sendRequest("GET", "/users", null);
    }

    public String createUser(String name, String username, String password, String role) {
        String json = "{\"name\":\"" + escapeJson(name)
                + "\",\"username\":\"" + escapeJson(username)
                + "\",\"password\":\"" + escapeJson(password)
                + "\",\"role\":\"" + escapeJson(role) + "\"}";
        return sendRequest("POST", "/users", json);
    }

    public String changePassword(int userId, String newPassword) {
        String json = "{\"new_password\":\"" + escapeJson(newPassword) + "\"}";
        return sendRequest("PUT", "/users/" + userId + "/password", json);
    }

    public String deleteUser(int userId) {
        return sendRequest("DELETE", "/users/" + userId, null);
    }

    // ============ HTTP HELPER ============

    private String sendRequest(String method, String endpoint, String jsonBody) {
        try {
            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (jsonBody != null && !jsonBody.isEmpty()) {
                conn.setDoOutput(true);
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                conn.setRequestProperty("Content-Length", String.valueOf(input.length));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(input, 0, input.length);
                }
            }

            int code = conn.getResponseCode();
            BufferedReader reader;
            if (code >= 200 && code < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();
            return response.toString();

        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Unable to connect to server. Please check if the backend is running.\"}";
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
