package com.majadamarcial;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Hello world!
 *
 */
public class App {

    private static Lock candado = new ReentrantLock();
    private static Condition relevo = candado.newCondition();
    private static int turn = 1;

    public static void main(String[] args) {
        final Corredor corredor1 = new Corredor("A");
        final Corredor corredor2 = new Corredor("B");

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    candado.lock();
                    corredor1.avanzar();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                corredor1.retroceder();
                turn = 2;
                relevo.signalAll();
                candado.unlock();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    candado.lock();
                    while (turn != 2) {
                        relevo.await();
                    }
                    corredor2.avanzar();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                corredor2.retroceder();
                candado.unlock();
            }
        });

        t1.start();
        t2.start();
    }

    public static final void clearConsole() throws InterruptedException, IOException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();  
    }
}