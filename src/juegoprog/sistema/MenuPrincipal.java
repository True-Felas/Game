package juegoprog.sistema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pantalla de inicio del juego.
 * Usa un botón para cambiar a la pantalla de juego.
 */
public class MenuPrincipal extends JPanel {
    public MenuPrincipal(Pantalla pantalla) {
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);

        JButton botonJugar = new JButton("Iniciar Juego");
        botonJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pantalla.cambiarPantalla("JUEGO");
            }
        });
        add(botonJugar, BorderLayout.CENTER);
    }
}
