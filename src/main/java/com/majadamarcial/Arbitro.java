package com.majadamarcial;

public class Arbitro {
    // Metodo para presentarse ante los corredores y dar el pistoletazo de salida
    public synchronized void presentarse() throws InterruptedException {
        System.out.println("El arbitro entra en el estadio");
        wait(2000);
        System.out.println("BAM!!!");
        wait(500);
        
    }
    // Metodo para finalizar la carrera
    public void checkeredFlag () {
        System.out.println("La carrera ha finalizado");
    }
}