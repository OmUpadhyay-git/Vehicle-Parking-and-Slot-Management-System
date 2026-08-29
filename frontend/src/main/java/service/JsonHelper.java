package service;

public class JsonHelper {

    public static String extractField(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";
        return json.substring(startIndex, endIndex);
    }

    public static int extractJsonInt(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return 0;
        startIndex += searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length() && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        try {
            return Integer.parseInt(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double extractJsonDouble(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return 0.0;
        startIndex += searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length() && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '.' || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        try {
            return Double.parseDouble(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static String extractMessage(String json) {
        String searchKey = "\"message\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";
        return json.substring(startIndex, endIndex);
    }

    public static String formatDateTime(String isoDateTime) {
        try {
            if (isoDateTime == null || isoDateTime.isEmpty()) return "-";
            if (isoDateTime.length() >= 16) {
                String date = isoDateTime.substring(0, 10);
                String time = isoDateTime.substring(11, 16);
                return date + " " + time;
            }
            return isoDateTime;
        } catch (Exception e) {
            return isoDateTime;
        }
    }

    public static String formatDuration(int minutes) {
        if (minutes <= 0) return "-";
        long hrs = minutes / 60;
        long mins = minutes % 60;
        if (hrs > 0 && mins > 0) {
            return hrs + "h " + mins + "m";
        } else if (hrs > 0) {
            return hrs + "h";
        } else {
            return mins + "m";
        }
    }
}
