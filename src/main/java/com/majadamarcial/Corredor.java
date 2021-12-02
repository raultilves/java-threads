package com.majadamarcial;

import java.io.IOException;

public class Corredor {
    private int position;
    private String s;
    private String nombre;

    public Corredor(String nombre) {
        this.position = 0;
        this.s = "";
        this.nombre = nombre;
    }

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
