package businessLogic;

import dataModel.Client;
import dataModel.Server;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public void addClient(List<Server> servers, Client client) {
        if(servers == null || servers.isEmpty()) {
            return;
        }
        Server server = servers.get(0);
        for(Server s : servers) {
            if(s.getWaitingPeriod().get() < server.getWaitingPeriod().get()) {
                server = s;
            }
        }
        server.addClient(client);
    }
}
