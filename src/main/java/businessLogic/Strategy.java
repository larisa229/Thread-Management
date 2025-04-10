package businessLogic;

import dataModel.Client;
import dataModel.Server;
import java.util.List;

public interface Strategy {

    void addClient(List<Server> servers, Client c);
}
