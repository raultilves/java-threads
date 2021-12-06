package com.majadamarcial;

import java.io.IOException;

public class Corredor {
    private int position;       // Define la posicion en el circuito de forma logica
    private String s;           // Define como se muestra en la consola la posicion de forma visual
    private String nombre;      // Nombre del corredor

    // En el constructor ...
    public Corredor(String nombre) {
        this.position = 0;          // ... se inicializa su posicion inicial a 0
        this.s = "";                // ... se inicializa el mensaje por consola como cadena vacia
        this.nombre = nombre;       // ... se inicializa el nombre segun los parametros de la instancia 
    }

    // Anvanza una posicion
    public synchronized void avanzar() throws InterruptedException, IOException {        
        App.clearConsole();
       
        System.out.print("Corredor " + this.nombre + ": ");

        while (this.position < 10) {
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  
            System.out.print("#");
            this.position++;
        }
    }

    // Retrocede una posicion
    public synchronized void retroceder() {
        while (this.position >= 0) {
            try {
                wait(500);
                App.clearConsole();
                this.s = "";
            } catch (InterruptedException | IOException e1) {
                e1.printStackTrace();
            }
            System.out.print("Corredor " + this.nombre + ": ");
            for (int i = 0; i < position; i++) {
                this.s += "#"; 
            }
            System.out.print(this.s);
            this.position--;
        }
    }
}
