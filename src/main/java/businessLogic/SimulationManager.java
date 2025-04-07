package businessLogic;

import GUI.SimulationFrame;
import dataAccess.Logger;
import dataModel.Server;
import dataModel.Task;

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
    private List<Task> generatedTasks;

    private int totalServiceTime = 0;
    private int peakHour = 0;
    private int maxClientAtTime = 0;

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
        generateNRandomTasks();
        //hardcodedExample();
    }

    public void setFrame(SimulationFrame frame) {
        this.frame = frame;
    }

    private void generateNRandomTasks() {
        generatedTasks = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < numberOfClients; i++) {
            int arrivalTime = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = rand.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            Task newTask = new Task(i + 1, arrivalTime, serviceTime);
            generatedTasks.add(newTask);
        }

        Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrivalTime));
    }

    private void hardcodedExample() {
        generatedTasks = new ArrayList<>();
        generatedTasks.add(new Task(1, 2, 2));
        generatedTasks.add(new Task(2, 3, 3));
        generatedTasks.add(new Task(3, 4, 3));
        generatedTasks.add(new Task(4, 10, 2));
        numberOfClients = generatedTasks.size();
    }

    @Override
    public void run() {
        Logger.init("log.txt");
        int currentTime = 0;
        while(currentTime <= simulationInterval && !(generatedTasks.isEmpty() && allServersEmpty())) {
            List<Task> tasksToRemove = new ArrayList<>();
            for(Task task : generatedTasks) {
                if(task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    totalServiceTime += task.getServiceTime();
                    tasksToRemove.add(task);
                } else if(task.getArrivalTime() < currentTime) {
                    task.incrementWaitingTime();
                }
            }
            generatedTasks.removeAll(tasksToRemove);
            frame.updateUI(currentTime, scheduler.getServers(), generatedTasks);

            int currentClients = 0;
            for(Server server : scheduler.getServers()) {
                currentClients += server.getTasks().length;
            }
            if(currentClients > maxClientAtTime) {
                maxClientAtTime = currentClients;
                peakHour = currentTime;
            }
            printCurrentState(currentTime);

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            currentTime++;

            if(generatedTasks.isEmpty() && allServersEmpty()) {
                break;
            }
        }
        int totalWaitingTime = 0;
        for (Task task : generatedTasks) {
            totalWaitingTime += task.getWaitingTime();
        }
        float avgWaitingTime = (float)totalWaitingTime / numberOfClients;
        float avgServiceTime = (float)totalServiceTime / numberOfClients;
        frame.displayResults(avgWaitingTime, avgServiceTime, peakHour);
        printStatistics(totalWaitingTime);
        scheduler.stopServers();
        Logger.close();
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
        if(generatedTasks.isEmpty()) {
            Logger.log("None");
        }
        for(Task task : generatedTasks) {
            Logger.log(task + "; ");
        }
        Logger.log("\n");

        int serverId = 1;
        for(Server server : scheduler.getServers()) {
            Logger.log("Queue " + serverId + ": ");
            if(server.isClosed()) {
                Logger.log("Closed");
            } else {
                for(Task task : server.getTasks()) {
                    Logger.log(task + "; ");
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
