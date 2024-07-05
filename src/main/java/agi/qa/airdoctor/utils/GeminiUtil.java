package agi.qa.airdoctor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import agi.qa.airdoctor.constants.AppConstants;

public class GeminiUtil {

    public static String prompt = "give me 27 fictional addresses in which each fields like street1, street2, city, state, and zip (total 5 fields) should be separated by a comma";

    public static String getResponse(String prompt) {
        String jsonString = "{\"contents\":[" +
                "{\"role\": \"user\"," +
                "\"parts\":[{\"text\": \"" + prompt + "\"}]}]}";

        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(AppConstants.GEMINI_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                reader.close();
                return response.toString();
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                reader.close();
                System.out.println("Error: HTTP response code " + responseCode + " - " + response.toString());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> extractMainContent(String response) {
        List<String> addresses = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        if (candidates.length() > 0) {
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject content = candidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            if (parts.length() > 0) {
                JSONObject part = parts.getJSONObject(0);
                String text = part.getString("text");
                String[] lines = text.split("\n");
                for (String line : lines) {
                    addresses.add(line);
                }
            }
        }
        return addresses;
    }

    public static void writeDataToGoogleSheet(String sheetName, List<String> addresses)
            throws IOException, GeneralSecurityException {
        // Map to store column index based on header name
        Map<String, Integer> columnIndexMap = new HashMap<>();

        Sheets service = SheetUtil.getSheetsService();

        List<List<Object>> headerData = getSheetData(service, AppConstants.GOOGLE_SPREADSHEET_ID, sheetName + "!1:1");

        // first row
        if (headerData.isEmpty() || headerData.get(0).isEmpty()) {
            throw new IllegalArgumentException("No headers found in the first row of the sheet.");
        }

        List<Object> headerRow = headerData.get(0);
        for (int colNum = 0; colNum < headerRow.size(); colNum++) {
            String header = headerRow.get(colNum).toString().trim(); 
            columnIndexMap.put(header, colNum);
        }

        List<List<Object>> data = new ArrayList<>();
        int currentRowNum = 1; // Start from the next available row
        for (String address : addresses) {
            String addressWithoutNumbering = address.replaceFirst("^\\d+\\.\\s*", "");
            String[] parts = addressWithoutNumbering.split("\\s*,\\s*"); // Split by comma and trim whitespace
            List<Object> row = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                String header = getHeader(i); // Get header based on index
                if (columnIndexMap.containsKey(header)) {
                    row.add(parts[i].trim()); // Trim to remove extra spaces
                }
            }
            data.add(row);
            currentRowNum++; 
        }

        writeToGoogleSheets(service, AppConstants.GOOGLE_SPREADSHEET_ID, sheetName + "!H2", data);
    }

    private static void writeToGoogleSheets(Sheets service, String spreadsheetId, String range, List<List<Object>> data)
            throws IOException {
        ValueRange body = new ValueRange().setValues(data);
        service.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
        System.out.println("Data written to Google Sheets successfully.");
    }

    private static List<List<Object>> getSheetData(Sheets service, String spreadsheetId, String range)
            throws IOException {
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        return response.getValues();
    }

    private static String getHeader(int index) {
        switch (index) {
            case 0:
                return "Billing_Address_1";
            case 1:
                return "Billing_Address_2";
            case 2:
                return "City";
            case 3:
                return "State";
            case 4:
                return "zipcode";
            default:
                return "";
        }
    }

    public static void main(String[] args) {
        try {
            String resp = getResponse(prompt);
            System.out.println(resp + "<==== response");
            List<String> addresses = extractMainContent(resp);
            writeDataToGoogleSheet(AppConstants.GOOGLE_SHEET_NAME, addresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
