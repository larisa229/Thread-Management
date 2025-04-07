package businessLogic;

import dataModel.Server;
import dataModel.Task;
import java.util.List;

public interface Strategy {

    void addTask(List<Server> servers, Task t);
}
