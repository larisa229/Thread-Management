package businessLogic;

import dataModel.Client;
import dataModel.Server;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<Server> servers;
    private final int maxNoServers;
    private final int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        servers = new ArrayList<>();
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;

        for(int i = 0; i < maxNoServers; i++){
            Server server = new Server();
            servers.add(server);
            Thread thread = new Thread(server, "Server-" + (i + 1));
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchClient(Client client) {
        if(strategy != null) {
            strategy.addClient(servers, client);
        } else {
            System.err.println("Strategy not set.");
        }
    }

    public List<Server> getServers() {
        return servers;
    }

    public void stopServers() {
        for(Server server : servers) {
            server.stop();
        }
    }
}
