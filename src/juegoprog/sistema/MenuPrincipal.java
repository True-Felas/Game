package juegoprog.sistema;

import juegoprog.graficos.Pantalla;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JPanel {
    private Image fondo; // Imagen de fondo

    public MenuPrincipal(Pantalla pantalla) {
        // Cargar la imagen de fondo desde resources
        fondo = new ImageIcon(getClass().getResource("/resources/menu/Fondo2_4.png")).getImage();

        setLayout(null); // Usamos diseño absoluto para posicionar los botones manualmente

        // Crear el título
        JLabel titulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        titulo.setFont(new Font("Tahoma", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE); // Para que se vea bien sobre la imagen
        titulo.setBounds(400, 50, 500, 50);
        add(titulo);

        // Cargar imágenes para los botones
        ImageIcon imgJugar = new ImageIcon(getClass().getResource("/resources/menu/Iniciar2.png"));
        ImageIcon imgSalir = new ImageIcon(getClass().getResource("/resources/menu/Salir2.png"));

        // Botón Iniciar Juego
        JButton botonJugar = new JButton(imgJugar);
        botonJugar.setBounds(500, 300, 300, 80);
        botonJugar.setBorderPainted(false);
        botonJugar.setContentAreaFilled(false);
        botonJugar.setFocusPainted(false);
        botonJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pantalla.cambiarPantalla("JUEGO");
            }
        });
        add(botonJugar);

        // Botón Salir
        JButton botonSalir = new JButton(imgSalir);
        botonSalir.setBounds(500, 400, 300, 80);
        botonSalir.setBorderPainted(false);
        botonSalir.setContentAreaFilled(false);
        botonSalir.setFocusPainted(false);
        botonSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Cierra el juego
            }
        });
        add(botonSalir);
    }

    // Sobrescribimos paintComponent para dibujar la imagen de fondo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }
}
