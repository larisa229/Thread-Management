package dataModel;

public class Task {

    private int id;
    private int arrivalTime;
    private int serviceTime;
    private int waitingTime = 0;

    public Task(int id, int arrivalTime, int serviceTime) {
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

    public long getWaitingTime() {
        return waitingTime;
    }

    public void incrementWaitingTime() {
        waitingTime++;
    }
    @Override
    public String toString() {
        return "(" + getId() + ", " + getArrivalTime() + ", " + getServiceTime() + ")";
    }
}
