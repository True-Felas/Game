package juegoprog.sistema;

import juegoprog.graficos.Pantalla;
import juegoprog.audio.GestorMusica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JPanel {
    private Image fondo; // Imagen de fondo
    private final GestorMusica gestorMusica = new GestorMusica(); // Instancia de música
    private JLabel titulo; // 🔹 Referencia al título para animarlo

    public MenuPrincipal(Pantalla pantalla) {
        // Cargar la imagen de fondo desde resources
        fondo = new ImageIcon(getClass().getResource("/resources/menu/Fondo2_4.png")).getImage();

        setLayout(null); // Usamos diseño absoluto para posicionar los botones manualmente

        // MÚSICA DEL MENÚ
        gestorMusica.reproducirMusica("/resources/audio/Intro_NoirCity_Find Me Again.wav"); // Música del menú

        // 🔹 Crear y animar el título
        titulo = new JLabel("NOIR CITY GAME", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 50));
        titulo.setForeground(new Color(250, 240, 230));
        titulo.setBounds(300, 20, 700, 80);
        add(titulo);
        animarTitulo(); // 🔹 Llamamos al método de animación

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
                gestorMusica.fadeOutMusica(2000); // 🔹 Fade out en 2 segundos
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

    // MÉTODO para animar el título con parpadeo suave
    private void animarTitulo() {
        Timer timer = new Timer(100, new ActionListener() {
            float alpha = 1.0f;
            boolean bajando = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += bajando ? -0.05f : 0.05f;
                if (alpha <= 0.7f || alpha >= 1.0f) bajando = !bajando;

                titulo.setForeground(new Color(250, 240, 230, (int) (alpha * 255))); // 🔹 Ajustamos transparencia
            }
        });
        timer.start();
    }
}
