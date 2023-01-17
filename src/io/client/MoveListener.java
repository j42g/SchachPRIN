package io.client;

public class MoveListener implements Runnable {

    private Client client;
    private Verbinder v;
    private volatile boolean shouldRun;

    public MoveListener(Client c, Verbinder v) {
        this.client = c;
        this.v = v;
        this.shouldRun = true;
    }

    @Override
    public void run() {
        while (shouldRun) {
            if (v.hasMove()) {
                client.amZug();
                stop();
            }
        }
    }

    public void stop() {
        this.shouldRun = false;
    }

}
