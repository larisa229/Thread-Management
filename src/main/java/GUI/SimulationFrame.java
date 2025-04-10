package GUI;
import dataModel.Server;
import dataModel.Client;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import businessLogic.SimulationManager;
import businessLogic.SelectionPolicy;

import java.util.List;

public class SimulationFrame extends Application {
    private TextField simIntervalField;
    private TextField minServiceField;
    private TextField maxServiceField;
    private TextField minArrivalField;
    private TextField maxArrivalField;
    private TextField clientsField;
    private TextField serversField;
    private ComboBox<SelectionPolicy> strategyCombo;
    private TextArea logArea;
    private Button startButton;
    private Button resetButton;

    private VBox resultsBox;
    private Label avgWaitingLabel;
    private Label avgServiceLabel;
    private Label peakHourLabel;

    private SimulationManager simulationManager;
    private Thread simulationThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Queue Simulation System");
        GridPane inputGrid = createInputGrid();

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(300);

        HBox buttonBox = new HBox(10);
        startButton = new Button("Start Simulation");
        resetButton = new Button("Reset");
        buttonBox.getChildren().addAll(startButton, resetButton);

        resultsBox = new VBox(5);
        resultsBox.setPadding(new Insets(10));
        resultsBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");
        avgWaitingLabel = new Label("Average Waiting Time -");
        avgServiceLabel = new Label("Average Service Time -");
        peakHourLabel = new Label("Peak Hour -");
        resultsBox.getChildren().addAll(
                new Label("Simulation Results"),
                avgWaitingLabel,
                avgServiceLabel,
                peakHourLabel
        );

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));
        mainLayout.getChildren().addAll(new Label("Simulation Parameters:"), inputGrid, buttonBox, new Label("Simulation Log:"), logArea, resultsBox);
        setupEventHandlers();
        primaryStage.setScene(new Scene(mainLayout, 700, 700));
        primaryStage.show();
    }

    private GridPane createInputGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        simIntervalField = new TextField("60");
        minServiceField = new TextField("2");
        maxServiceField = new TextField("4");
        minArrivalField = new TextField("2");
        maxArrivalField = new TextField("30");
        clientsField = new TextField("4");
        serversField = new TextField("2");

        strategyCombo = new ComboBox<>();
        strategyCombo.getItems().addAll(SelectionPolicy.SHORTEST_QUEUE, SelectionPolicy.SHORTEST_TIME);
        strategyCombo.setValue(SelectionPolicy.SHORTEST_TIME);

        grid.add(new Label("Number of Clients:"), 0, 0);
        grid.add(clientsField, 1, 0);
        grid.add(new Label("Number of Servers:"), 0, 1);
        grid.add(serversField, 1, 1);
        grid.add(new Label("Simulation Interval (sec):"), 0, 2);
        grid.add(simIntervalField, 1, 2);
        grid.add(new Label("Min Arrival Time:"), 0, 3);
        grid.add(minArrivalField, 1, 3);
        grid.add(new Label("Max Arrival Time:"), 0, 4);
        grid.add(maxArrivalField, 1, 4);
        grid.add(new Label("Min Service Time:"), 0, 5);
        grid.add(minServiceField, 1, 5);
        grid.add(new Label("Max Service Time:"), 0, 6);
        grid.add(maxServiceField, 1, 6);
        grid.add(new Label("Selection Strategy:"), 0, 7);
        grid.add(strategyCombo, 1, 7);

        return grid;
    }

    private void setupEventHandlers() {
        startButton.setOnAction(e -> startSimulation());
        resetButton.setOnAction(e -> resetSimulation());
    }

    private void startSimulation() {
        try {
            int simInterval = Integer.parseInt(simIntervalField.getText());
            int minService = Integer.parseInt(minServiceField.getText());
            int maxService = Integer.parseInt(maxServiceField.getText());
            int minArrival = Integer.parseInt(minArrivalField.getText());
            int maxArrival = Integer.parseInt(maxArrivalField.getText());
            int clients = Integer.parseInt(clientsField.getText());
            int servers = Integer.parseInt(serversField.getText());
            SelectionPolicy strategy = strategyCombo.getValue();

            logArea.clear();
            logArea.appendText("Starting simulation with parameters:\n");
            logArea.appendText(String.format("- Simulation Interval: %d\n", simInterval));
            logArea.appendText(String.format("- Service Time Range: %d-%d\n", minService, maxService));
            logArea.appendText(String.format("- Arrival Time Range: %d-%d\n", minArrival, maxArrival));
            logArea.appendText(String.format("- Clients: %d, Servers: %d\n", clients, servers));
            logArea.appendText(String.format("- Strategy: %s\n\n", strategy));

            startButton.setDisable(true);

            simulationManager = new SimulationManager(simInterval, minService, maxService, minArrival, maxArrival, clients, servers, strategy);
            simulationManager.setFrame(this);

            simulationThread = new Thread(simulationManager);
            simulationThread.start();

        } catch (NumberFormatException ex) {
            logArea.appendText("Error: Please enter valid numbers for all fields.\n");
        }
    }

    private void resetSimulation() {
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }

        simIntervalField.setText("60");
        minServiceField.setText("2");
        maxServiceField.setText("4");
        minArrivalField.setText("2");
        maxArrivalField.setText("30");
        clientsField.setText("4");
        serversField.setText("2");
        strategyCombo.setValue(SelectionPolicy.SHORTEST_TIME);

        logArea.clear();
        resultsBox.getChildren().forEach(node -> {
            if (node instanceof Label) {
                ((Label)node).setText(((Label)node).getText().replaceAll(": .+", ": -"));
            }
        });

        startButton.setDisable(false);
    }

    public void updateUI(int currentTime, List<Server> servers, List<Client> waitingTasks) {
        javafx.application.Platform.runLater(() -> {
            logArea.appendText(String.format("\nTime: %d\n", currentTime));

            logArea.appendText("Waiting clients: ");
            if (waitingTasks.isEmpty()) {
                logArea.appendText("none\n");
            } else {
                for (Client client : waitingTasks) {
                    logArea.appendText(client.toString() + " ");
                }
                logArea.appendText("\n");
            }

            int serverId = 1;
            for (Server server : servers) {
                logArea.appendText(String.format("Queue %d: ", serverId));
                if (server.isClosed()) {
                    logArea.appendText("Closed\n");
                } else {
                    Client[] clients = server.getClients();
                    if (clients.length == 0) {
                        logArea.appendText("empty\n");
                    } else {
                        for (Client client : clients) {
                            logArea.appendText(client.toString() + " ");
                        }
                        logArea.appendText("\n");
                    }
                }
                serverId++;
            }
        });
    }

    public void displayResults(float avgWaitingTime, float avgServiceTime, int peakHour) {
        javafx.application.Platform.runLater(() -> {
            avgWaitingLabel.setText(String.format("Average Waiting Time: %.2f", avgWaitingTime));
            avgServiceLabel.setText(String.format("Average Service Time: %.2f", avgServiceTime));
            peakHourLabel.setText(String.format("Peak Hour: %d", peakHour));
            startButton.setDisable(false);
        });
    }
}
