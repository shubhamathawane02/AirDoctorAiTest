package agi.qa.airdoctor.utils;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.auth.http.HttpCredentialsAdapter;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SheetUtil {
    private static Sheets sheetsService;
    private static final String SPREADSHEET_ID = "11VIehiB9_dIrQbL9dRhVQfUHqjX75ixIFLZLV6U6uao"; // original sheet => //
                                                                                                 // checkout page
    // private static final String SPREADSHEET_ID =
    // "1Nqtt6djt9jg4w81BFQx2nV5KGF2_Z_sucZf223jorvU";
    private static final String CREDENTIALS_FILE_PATH = "C:\\\\Users\\\\shubh\\\\Python_Learner\\\\Projects\\\\PDF_Reader\\\\GoogleSheet\\\\credentials2.json";

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            GoogleCredentials credentials;
            try (FileInputStream serviceAccountStream = new FileInputStream(CREDENTIALS_FILE_PATH)) {
                credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            }

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // Create an HTTP request initializer with the credentials
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            sheetsService = new Sheets.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("GenOT")
                    .build();
        }
        return sheetsService;
    }

    // public static List<List<Object>> readSheet(String range) throws IOException,
    // GeneralSecurityException {
    // ValueRange response = getSheetsService().spreadsheets().values()
    // .get(SPREADSHEET_ID, range)
    // .execute();
    // return response.getValues();
    // }

    public static Object[][] readSheet(String range) throws IOException, GeneralSecurityException {
        ValueRange response = getSheetsService().spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        System.out.println("Reading the sheet!");

        Object[][] data = new Object[values.size()][values.get(0).size()];
        for (int i = 0; i < values.size(); i++) {
            for (int j = 0; j < values.get(i).size(); j++) {
                data[i][j] = values.get(i).get(j);
                // System.out.println(data[i][j]);
            }
        }

        return data;
    }

    // public static void writeSheet(String range, List<List<Object>> values) throws
    // IOException, GeneralSecurityException {
    // ValueRange body = new ValueRange().setValues(values);
    // getSheetsService().spreadsheets().values()
    // .update(SPREADSHEET_ID, range, body)
    // .setValueInputOption("RAW")
    // .execute();
    // }

    public static void writeSheet(String sheetName, String subtotal, String shipping, String tax, String total,
            String orderId, int count) throws IOException, GeneralSecurityException {
        String range = sheetName + "!S" + (count + 2) + ":W" + (count + 2);

        List<List<Object>> values = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        row.add(subtotal);
        row.add(shipping);
        row.add(tax);
        row.add(total);
        row.add(orderId);
        values.add(row);

        ValueRange body = new ValueRange().setValues(values);

        getSheetsService().spreadsheets().values()
                .update(SPREADSHEET_ID, range, body)
                .setValueInputOption("RAW")
                .execute();

        System.out.println("Updated row " + (count + 1) + " in sheet " + sheetName + " with values: " + values);
    }
}
