package businessLogic;

import dataModel.Server;
import dataModel.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task task) {
        if(servers == null || servers.isEmpty()) {
            return;
        }

        Server server = servers.get(0);
        for(Server s : servers) {
            if(s.getWaitingPeriod().get() < server.getWaitingPeriod().get()) {
                server = s;
            }
        }

        server.addTask(task);
    }
}
