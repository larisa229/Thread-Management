package businessLogic;

import GUI.SimulationFrame;
import dataAccess.Logger;
import dataModel.Server;
import dataModel.Client;

import java.util.*;

public class SimulationManager implements Runnable {

    public int simulationInterval;
    public int maxServiceTime;
    public int minServiceTime;
    public int numberOfServers;
    public int numberOfClients;
    public int minArrivalTime;
    public int maxArrivalTime;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Client> generatedClients;

    private int totalServiceTime = 0;
    private int peakHour = 0;
    private int maxClientAtTime = 0;
    private int totalWaitingTime = 0;

    public SimulationManager(int simulationInterval, int minServiceTime, int maxServiceTime,
                             int minArrivalTime, int maxArrivalTime, int numberOfClients, int numberOfServers, SelectionPolicy selectionPolicy) {
        this.simulationInterval = simulationInterval;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.scheduler = new Scheduler(numberOfServers, numberOfClients);
        this.scheduler.changeStrategy(selectionPolicy);
        this.frame = new SimulationFrame();
        generateNRandomClients();
    }

    public void setFrame(SimulationFrame frame) {
        this.frame = frame;
    }

    @Override
    public void run() {
        Logger.init("log.txt");
        int currentTime = 0;
        while(currentTime <= simulationInterval && !(generatedClients.isEmpty() && allServersEmpty())) {
            for (Server server : scheduler.getServers()) {
                server.updateSimulationTime(currentTime);
            }
            dispatchArrivedClients(currentTime);
            updateUIAndTrackPeak(currentTime);
            printCurrentState(currentTime);
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            currentTime++;
            if(generatedClients.isEmpty() && allServersEmpty()) break;
        }
        computeWaitingTime();
        frame.displayResults(getAverageWaitingTime(), getAverageServiceTime(), peakHour);
        printStatistics(totalWaitingTime);
        scheduler.stopServers();
        Logger.close();
    }

    private void generateNRandomClients() {
        generatedClients = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < numberOfClients; i++) {
            int arrivalTime = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = rand.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            Client newClient = new Client(i + 1, arrivalTime, serviceTime);
            generatedClients.add(newClient);
        }
        Collections.sort(generatedClients, Comparator.comparingInt(Client::getArrivalTime));
    }

    private void computeWaitingTime() {
        totalWaitingTime = 0;
        int totalServedClients = 0;
        for(Server server : scheduler.getServers()) {
            for(Client client : server.getServedClients()) {
                totalWaitingTime += client.getWaitingTime();
                totalServedClients++;
            }
        }
    }

    private void dispatchArrivedClients(int currentTime) {
        List<Client> clientsToRemove = new ArrayList<>();
        for (Client client : generatedClients) {
            if (client.getArrivalTime() == currentTime) {
                scheduler.dispatchClient(client);
                totalServiceTime += client.getServiceTime();
                clientsToRemove.add(client);
            }
        }
        generatedClients.removeAll(clientsToRemove);
    }

    private void updateUIAndTrackPeak(int currentTime) {
        frame.updateUI(currentTime, scheduler.getServers(), generatedClients);
        int currentClients = 0;
        for (Server server : scheduler.getServers()) {
            currentClients += server.getClients().length;
        }
        if (currentClients > maxClientAtTime) {
            maxClientAtTime = currentClients;
            peakHour = currentTime;
        }
    }

    private float getAverageWaitingTime() {
        return numberOfClients > 0 ? (float) totalWaitingTime / numberOfClients : 0;
    }

    private float getAverageServiceTime() {
        return numberOfClients > 0 ? (float) totalServiceTime / numberOfClients : 0;
    }

    private boolean allServersEmpty() {
        for(Server server : scheduler.getServers()) {
            if(!server.isClosed()) {
                return false;
            }
        }
        return true;
    }

    private void printCurrentState(int currentTime) {
        Logger.log("Current time: " + currentTime + " / " + simulationInterval + "\n");
        Logger.log("Waiting clients: ");
        if(generatedClients.isEmpty()) {
            Logger.log("None");
        }
        for(Client c : generatedClients) {
            Logger.log(c + "; ");
        }
        Logger.log("\n");
        int serverId = 1;
        for(Server server : scheduler.getServers()) {
            Logger.log("Queue " + serverId + ": ");
            if(server.isClosed()) {
                Logger.log("Closed");
            } else {
                for(Client c : server.getClients()) {
                    Logger.log(c + "; ");
                }
            }
            Logger.log("\n");
            serverId++;
        }
        Logger.log("\n");
    }

    private void printStatistics(int totalWaitingTime) {
        Logger.log("\nSimulation results:\n");
        Logger.log("Average waiting time: " + (float)totalWaitingTime / numberOfClients + "\n");
        Logger.log("Average service time: " + (float)totalServiceTime / numberOfClients + "\n");
        Logger.log("Peak hour: " + peakHour + "\n");
    }

    public static void main(String[] args) {
        SimulationManager gen = new SimulationManager(60, 2, 4,
                2, 30, 4, 2, SelectionPolicy.SHORTEST_TIME);
        Thread t = new Thread(gen);
        t.start();
    }
}
