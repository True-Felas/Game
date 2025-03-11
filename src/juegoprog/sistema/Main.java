package juegoprog.sistema;

import juegoprog.graficos.Pantalla;

import java.awt.*;

public class Main {
    public static void main(String[] args) {

        /*🔹 Desactiva el escalado de Windows en Java
        (Lo dejo como comentario y ya probáis a usarlo)*/
         System.setProperty("sun.java2d.uiScale", "1.0");

        // 🔹 Iniciar la pantalla del juego
        Pantalla pantalla = new Pantalla();

        /* 🔹 Si se desactiva el escalado se le da un *1.5 a la pantalla para que aumente un poco.
        pantalla.setSize((int)(pantalla.getWidth() * 1.5), (int)(pantalla.getHeight() * 1.5));
        pantalla.setPreferredSize(new Dimension(pantalla.getWidth(), pantalla.getHeight()));*/
    }
}

