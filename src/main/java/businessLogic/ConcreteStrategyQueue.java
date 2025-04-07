package businessLogic;

import dataModel.Server;
import dataModel.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task task) {
        if(servers == null || servers.isEmpty()) {
            return;
        }

        Server server = servers.get(0);
        for(Server s : servers) {
            if(s.getTasks().length < server.getTasks().length) {
                server = s;
            }
        }

        server.addTask(task);
    }
}
