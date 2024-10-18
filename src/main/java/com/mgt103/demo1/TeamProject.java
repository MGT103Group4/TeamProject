package com.mgt103.demo1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import okhttp3.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import okhttp3.*;

public class TeamProject extends Application {

    // Mock data for patients, staff, and wait times
    private ObservableList<String> patientQueue = FXCollections.observableArrayList();
    private ObservableList<String> staffList = FXCollections.observableArrayList("Doctor A", "Doctor B", "Nurse A", "Nurse B");

    // TextArea for displaying AI optimization suggestions
    private TextArea aiSuggestionsArea = new TextArea();

    // Replace with your actual ChatGPT API key


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ER Patient Flow Optimization");

        // Setup layout
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(15));

        // Add patient queue
        Label patientQueueLabel = new Label("Patient Queue:");
        ListView<String> patientListView = new ListView<>(patientQueue);
        root.getChildren().addAll(patientQueueLabel, patientListView);

        // Add staff list
        Label staffListLabel = new Label("Available Staff:");
        ListView<String> staffListView = new ListView<>(staffList);
        root.getChildren().addAll(staffListLabel, staffListView);

        // Button to generate AI suggestion
        Button suggestButton = new Button("Generate AI Suggestions");
        suggestButton.setOnAction(e -> {
            try {
                String suggestions = getChatGPTResponse();
                aiSuggestionsArea.setText(suggestions);
            } catch (Exception ex) {
                aiSuggestionsArea.setText("Error retrieving suggestions: " + ex.getMessage());
            }
        });

        // AI Suggestions Area
        Label aiSuggestionsLabel = new Label("AI Suggestions:");
        aiSuggestionsArea.setEditable(false);
        aiSuggestionsArea.setPrefHeight(200);

        root.getChildren().addAll(suggestButton, aiSuggestionsLabel, aiSuggestionsArea);

        // Button to add new patient (mocking real-time patient arrival)
        Button addPatientButton = new Button("Add Patient");
        addPatientButton.setOnAction(e -> addNewPatient());

        root.getChildren().add(addPatientButton);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Function to connect to ChatGPT API and get optimization suggestions
    private String getChatGPTResponse() throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-3.5-turbo";
        String prompt = "Hello World";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return extractMessageFromJSONResponse(response.toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }

    // Function to add a new patient to the queue
    private void addNewPatient() {
        Random random = new Random();
        String newPatient = "Patient " + (patientQueue.size() + 1) + " (Severity: " + (random.nextBoolean() ? "High" : "Low") + ")";
        patientQueue.add(newPatient);
    }
}
