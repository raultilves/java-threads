package com.majadamarcial;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class App {
    // Candado
    private static Lock candado = new ReentrantLock();

    // INICIO CONDICIONES //
    private static Condition arbitroPresente = candado.newCondition();  // Cuando el arbitro se encuentra en el campo
    private static Condition relevo = candado.newCondition();           // Cuando el relevo llega el corredor2
    private static Condition c2Ended = candado.newCondition();          // Cuando el corredor 2 llega a la meta
    // FIN CONDICIONES

    // INICIO VARIABLES DE EVENTO
    private static int turn = 0;            // El turno de ejecucion que separa los eventos que deben estar sincronizados. Condiciones: ArbitroPresente y Relevo
    private static boolean fin = false;     // El final de la carrera. Condicion: C2Ended
    // FIN VARIABLES DE EVENTO

    public static void main(String[] args) throws InterruptedException, IOException {
        final Corredor corredor1 = new Corredor("A");
        final Corredor corredor2 = new Corredor("B");
        final Arbitro arbitro = new Arbitro();

        // Hilo para el corredor1
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    candado.lock();                 // Adquiere el candado
                    while (turn != 1) {             
                        arbitroPresente.await();    // Espera a que el arbitro comience la carrera de relevos
                    }
                    corredor1.avanzar();            // Suena el pistoletazo y empieza a correr
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                corredor1.retroceder();             // Toca la pared y vuelve a darle el relevo al compañero
                turn = 2;                           // Le das el relevo
                relevo.signalAll();                 // Le avisas
                candado.unlock();                   // Suelta el candado
            }
        });

        // Hilo para el corredor2
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    candado.lock();                 // Adquiere el candado
                    while (turn != 2) {
                        relevo.await();             // Espera a que el corredor1 te de el relevo
                    }
                    corredor2.avanzar();            // Te da el relevo y corres
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                corredor2.retroceder();             // Tocas la pared y vuelves a la meta
                fin = true;                         // Finalizas tu carrera

                try {
                    clearConsole();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

                c2Ended.signalAll();                // Avisa al arbitro de que has terminado
                candado.unlock();                   // Suelta el candado
            }
        });

        // Hilo para el arbitro
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                candado.lock();                     // Adquiere el candado
                try {
                    arbitro.presentarse();          // Te presentas ante los corredores y ...
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                turn = 1;                           // ... das el pistoletazo de salida
                arbitroPresente.signalAll();        // "Suena el pistoletazo"
                candado.unlock();                   // Suelta el candado para que los corredores vean que ya comenzo la carrera

                candado.lock();                     // Aquiere el candado
                try {
                    while (!fin) {
                        c2Ended.await();            // Espera a que termine la carrera
                    }
                    arbitro.checkeredFlag();        // Cuando termina ondeas la bandera a cuadros
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                candado.unlock();                   // Sueltas el candado
            }
        });

        clearConsole();

        t1.start();     // Corredor 1 vivo
        t2.start();     // Corredor 2 vivo
        t3.start();     // Corredor 3 vivo

        t1.join();      // Corredor 1 debe finalizar
        t2.join();      // Corredor 2 debe finalizar

        // El Corredor 1 va al vestuario y se va a casa
        if (!t1.interrupted()) {
            t1.interrupt(); // Solo cuando han finalizado los corredores, despedimos al arbitro
            if (t1.isInterrupted()) {
                System.out.println("El Corredor A abandona el campo.");
            }
        }

        // El Corredor 2 va al vestuario y se va a casa
        if (!t2.interrupted()) {
            t2.interrupt(); // Solo cuando han finalizado los corredores, despedimos al arbitro
            if (t2.isInterrupted()) {
                System.out.println("El Corredor B abandona el campo.");
            }
        }

        // Avisamos de que el arbitro ya esta en su casa
        if (!t3.interrupted()) {
            t3.interrupt(); // Solo cuando han finalizado los corredores, despedimos al arbitro
            if (t3.isInterrupted()) {
                System.out.println("El arbitro ha abandonado el campo. No se admiten reclamaciones.");
            }
        }
    }

    // Metodo para limpiar la cosola. Finalidad: Formatear el texto de avanzar y retroceder.
    public static final void clearConsole() throws InterruptedException, IOException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();  
    }
}