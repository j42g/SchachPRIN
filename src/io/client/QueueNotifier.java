package io.client;

public class QueueNotifier implements Runnable {

    private volatile boolean shouldRun = true;

    @Override
    public void run() {
        Verbinder v = Verbinder.getInstance();
        try {
            while (shouldRun && !v.queueReady()) {
                Thread.sleep(100);
            }
            if(shouldRun){
                System.out.println("GEGNER GEFUNDEN. \"AKZEPTIEREN\" UM DAS SPIEL ZU BEGINNEN");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stoppe() {
        shouldRun = false;
    }

}
