package businessLogic;

import dataModel.Server;
import dataModel.Client;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {

    @Override
    public void addClient(List<Server> servers, Client client) {
        if(servers == null || servers.isEmpty()) {
            return;
        }
        Server server = servers.get(0);
        for(Server s : servers) {
            if(s.getClients().length < server.getClients().length) {
                server = s;
            }
        }
        server.addClient(client);
    }
}
