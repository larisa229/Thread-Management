package dataModel;

public class Client {

    private int id;
    private int arrivalTime;
    private int serviceTime;
    private volatile int waitingTime = 0;
    private volatile int startTime = -1;

    public Client(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getId() {
        return id;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    @Override
    public String toString() {
        return "(" + getId() + ", " + getArrivalTime() + ", " + getServiceTime() + ")";
    }
}
