package mahappdev.caresilabs.com.myfriends.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Simon on 10/2/2016.
 */

public class ThreadQueue {
    private Buffer<Runnable> queue;
    private Worker           worker;

    public ThreadQueue() {
        this.queue = new Buffer<Runnable>();
    }

    public void start() {
        if (worker == null) {
            worker = new Worker();
            worker.start();
        }
    }

    public void stop() {
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    public void enqueue(Runnable runnable) {
        queue.put(runnable);
    }

    public static class Buffer<T> {
        private LinkedList<T> buffer = new LinkedList<T>();

        public synchronized void put(T element) {
            buffer.addLast(element);
            notifyAll();
        }

        public synchronized T get() throws InterruptedException {
            while (buffer.isEmpty()) {
                wait();
            }
            return buffer.removeFirst();
        }
    }

    private class Worker extends Thread {
        public void run() {
            Runnable runnable;
            while (worker != null) {
                try {
                    runnable = queue.get();
                    runnable.run();
                } catch (InterruptedException e) {
                    worker = null;
                }
            }
        }
    }
}
