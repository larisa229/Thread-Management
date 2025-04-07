package dataModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {

    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;
    private boolean isRunning;

    public Server() {
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
        isRunning = true;
    }

    @Override
    public void run() {
        System.out.println("Server thread started: " + Thread.currentThread().getName());
        while(isRunning) {
            try {
                Task t = tasks.peek();
                if(t != null) {
                    t.setServiceTime(t.getServiceTime() - 1);
                    if(t.getServiceTime() <= 0) {
                        tasks.poll();
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

    public void stop() {
        isRunning = false;
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
        System.out.println("Task " + newTask.getId() + " added to server. New waiting period: " + waitingPeriod.get());
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public boolean isClosed() {
        return tasks.isEmpty();
    }
}
