package juegoprog.sistema;

import javax.swing.*;

public class Pantalla extends JFrame {
    public Pantalla() {
        setTitle("Pantalla");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Movimiento movimiento = new Movimiento();
        add(movimiento);

        setVisible(true); // Solo llamamos a esto una vez
        movimiento.requestFocus(); // Enfoca correctamente el área de juego
    }
}
