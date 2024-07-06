package agi.qa.airdoctor.utils;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agi.qa.airdoctor.constants.AppConstants;

public class GeminiUtil {

    public static String prompt = "give me 15 fictional addresses in which each fields  street1, street2,  city state and zip (total 5 fields) should be separated by comma  and it should contains following states : Alabama Alaska Arizona Arkansas California Colorado Connecticut Delaware District Columbia Florida Georgia Hawaii Idaho Illinois Indiana Iowa Kansas Kentucky Louisiana Maine Maryland Massachusetts Michigan Minnesota Mississippi Missouri Montana Nebraska Nevada New Hampshire New Jersey New Mexico New York North Carolina North Dakota Ohio Oklahoma Oregon Pennsylvania Rhode South Carolina South Dakota Tennessee Texas Utah Vermont Virginia Washington West Virginia Wisconsin Wyoming in json format";

    public static String getResponse(String prompt) {
        String jsonString = "{\"contents\":[" +
                "{\"role\": \"user\"," +
                "\"parts\":[{\"text\": \"" + prompt + "\"}]}]}";

        try {
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
                String resp = response.toString();
                System.out.println("Returning gemini response...");
                String jsonResponse = resp.replace("```json", "").replace("```", "").trim();
                return "[" + jsonResponse + "]";
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

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeDataToGoogleSheet(String sheetName, String jsonResponse)
            throws IOException, GeneralSecurityException {
        Sheets service = SheetUtil.getSheetsService(); // Ensure you have this utility method

        JSONArray responseArray = new JSONArray(jsonResponse);

        // Ensure the structure matches expectations
        if (responseArray.length() > 0) {
            JSONObject mainObject = responseArray.getJSONObject(0); // Assuming there's only one main object

            JSONArray candidates = mainObject.getJSONArray("candidates");

            List<List<Object>> headerData = getSheetData(service, AppConstants.GOOGLE_SPREADSHEET_ID,
                    sheetName + "!1:1");

            // First row
            if (headerData.isEmpty() || headerData.get(0).isEmpty()) {
                throw new IllegalArgumentException("No headers found in the first row of the sheet.");
            }

            Map<String, Integer> columnIndexMap = getColumnIndexMap(headerData.get(0));

            List<List<Object>> data = new ArrayList<>();
            int currentRowNum = 1; // Start from the next available row

            for (int i = 0; i < candidates.length(); i++) {
                JSONObject candidate = candidates.getJSONObject(i);

                JSONObject content = candidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    JSONObject part = parts.getJSONObject(0);
                    String text = part.getString("text");

                    JSONArray addresses = new JSONArray(text);
                    for (int j = 0; j < addresses.length(); j++) {
                        JSONObject address = addresses.getJSONObject(j);

                        // Extract address details
                        String street1 = address.getString("street1");
                        String street2 = address.optString("street2", "");
                        String city = address.getString("city");
                        String state = address.getString("state");
                        String zip = address.getString("zip");

                        // Assuming your headers in the sheet are "Billing_Address_1",
                        // "Billing_Address_2", "City",
                        // "State", "zipcode"
                        List<Object> row = new ArrayList<>();
                        row.add(street1);
                        row.add(street2);
                        row.add(city);
                        row.add(state);
                        row.add(zip);

                        data.add(row);
                        currentRowNum++;
                    }
                }
            }
            writeToGoogleSheets(service, AppConstants.GOOGLE_SPREADSHEET_ID, sheetName + "!H2", data);
        }
    }

    private static Map<String, Integer> getColumnIndexMap(List<Object> headerRow) {
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (int colNum = 0; colNum < headerRow.size(); colNum++) {
            String header = headerRow.get(colNum).toString().trim();
            columnIndexMap.put(header, colNum);
        }
        return columnIndexMap;
    }

    private static List<List<Object>> extractDataFromJson(String jsonResponse) {
        System.out.println("Extracting the content...!");
        List<List<Object>> data = new ArrayList<>();
        JSONArray candidates = new JSONArray(jsonResponse);
        for (int i = 0; i < candidates.length(); i++) {
            JSONObject candidate = candidates.getJSONObject(i);
            String street1 = candidate.getString("street1");
            String street2 = candidate.optString("street2", "");
            String city = candidate.getString("city");
            String state = candidate.getString("state");
            String zip = candidate.getString("zip");

            List<Object> row = new ArrayList<>();
            row.add(street1);
            row.add(street2);
            row.add(city);
            row.add(state);
            row.add(zip);

            data.add(row);
        }
        return data;
    }

    private static void writeToGoogleSheets(Sheets service, String spreadsheetId, String range, List<List<Object>> data)
            throws IOException {
        System.out.println("Writing data to sheet..!");
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

    // public static void main(String[] args) {
    //     try {
    //         String resp = getResponse(prompt);
    //         System.out.println("Got the data from Gemini , Feeding to Google Sheet");
    //         writeDataToGoogleSheet(AppConstants.GOOGLE_SHEET_NAME, resp);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
