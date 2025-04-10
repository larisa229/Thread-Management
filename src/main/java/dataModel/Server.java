package dataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {

    private final BlockingQueue<Client> clients;
    private final AtomicInteger waitingPeriod;
    private volatile boolean isRunning;
    private final List<Client> servedClients = new ArrayList<>();
    private volatile int currentSimulationTime;

    public Server() {
        clients = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
        isRunning = true;
    }

    public void updateSimulationTime(int time) {
        this.currentSimulationTime = time;
    }

    @Override
    public void run() {
        System.out.println("Server thread started: " + Thread.currentThread().getName());
        while(isRunning) {
            try {
                Client c = clients.peek();
                if(c != null) {
                    if (c.getStartTime() == -1) {
                        c.setStartTime(currentSimulationTime);
                        int waitingTime = currentSimulationTime - c.getArrivalTime();
                        c.setWaitingTime(waitingTime);
                        System.out.println("Client " + c.getId() + " starts at " + currentSimulationTime + " (waited " + waitingTime + ")");
                    }
                    c.setServiceTime(c.getServiceTime() - 1);
                    if(c.getServiceTime() <= 0) {
                        servedClients.add(clients.poll());
                        //clients.poll();
                    }
                waitingPeriod.decrementAndGet();
                }
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public List<Client> getServedClients() {
        return servedClients;
    }

    public void stop() {
        isRunning = false;
    }

    public Client[] getClients() {
        return clients.toArray(new Client[0]);
    }

    public void addClient(Client newClient) {
        clients.add(newClient);
        waitingPeriod.addAndGet(newClient.getServiceTime());
        System.out.println("Client " + newClient.getId() + " added to server. New waiting period: " + waitingPeriod.get());
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public boolean isClosed() {
        return clients.isEmpty();
    }
}
