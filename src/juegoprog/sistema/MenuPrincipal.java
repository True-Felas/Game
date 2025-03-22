package juegoprog.sistema;

import juegoprog.graficos.Pantalla;
import juegoprog.audio.GestorMusica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class MenuPrincipal extends JPanel {
    private final Image fondo; // Imagen de fondo
    private final GestorMusica gestorMusica = new GestorMusica(); // Instancia de música
    private final JLabel titulo; // 🔹 Referencia al título para animarlo

    private Font cargarFuentePersonalizada(String ruta, float tamaño) {
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream(ruta)));
            return fuente.deriveFont(Font.BOLD, tamaño);
        } catch (Exception e) {
            System.err.println("Error cargando la fuente: " + ruta);
            return new Font("Serif", Font.BOLD, (int) tamaño);
        }
    }

    public MenuPrincipal(JFrame ventana) {

        // Cargar la imagen de fondo desde resources
        fondo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Fondo_Menu.png"))).getImage();

        setLayout(null); // Usamos diseño absoluto para posicionar los botones manualmente

        // MÚSICA DEL MENÚ
        gestorMusica.reproducirMusica("/resources/audio/Intro_NoirCity_Find Me Again.wav"); // Música del menú

        // 🔹 Crear y animar el título
        titulo = new JLabel("NOIR CITY", SwingConstants.CENTER);
        titulo.setFont(cargarFuentePersonalizada("/resources/fonts/Noir_medium.otf", 50));
        titulo.setForeground(new Color(250, 240, 230));
        titulo.setBounds(300, 20, 700, 80);
        add(titulo);
        animarTitulo(); // 🔹 Llamamos al metodo de animación


        // Cargar imágenes para los botones
        ImageIcon imgJugar = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Iniciar2.png")));

        // Botón Controles
        ImageIcon imgControles = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Controles2.png")));
        JButton botonControles = new JButton(imgControles);
        botonControles.setBounds(500, 400, 300, 80); // Posición intermedia
        botonControles.setBorderPainted(false);
        botonControles.setContentAreaFilled(false);
        botonControles.setFocusPainted(false);
        botonControles.addActionListener(_ -> {
            JOptionPane.showMessageDialog(this,
                    """
                    CONTROLES DEL JUEGO:
                    
                    - W A S D: Mover personaje
                    - Ratón: Apuntar
                    - Click izquierdo: Acción / Disparo
                    - ESC: Pausar / Menú
                    """,
                    "Controles",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        add(botonControles);

        ImageIcon imgSalir = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Salir2.png")));


        JButton botonJugar = getJugar((Pantalla) ventana, imgJugar);
        add(botonJugar);

        // Botón Salir
        JButton botonSalir = new JButton(imgSalir);
        botonSalir.setBounds(500, 480, 300, 80);
        botonSalir.setBorderPainted(false);
        botonSalir.setContentAreaFilled(false);
        botonSalir.setFocusPainted(false);
        botonSalir.addActionListener(_ -> {
            System.exit(0); // Cierra el juego
        });
        add(botonSalir);
    }

    private JButton getJugar(Pantalla ventana, ImageIcon imgJugar) {
        JButton botonJugar = new JButton(imgJugar);
        botonJugar.setBounds(500, 320, 300, 80);
        botonJugar.setBorderPainted(false);
        botonJugar.setContentAreaFilled(false);
        botonJugar.setFocusPainted(false);
        botonJugar.addActionListener(_ -> {
            gestorMusica.fadeOutMusica(2000); // 🔹 Fade out en 2 segundos
            ventana.cambiarPantalla("CINEMATICA"); // Crea esta pantalla en Pantalla.java

        });
        return botonJugar;
    }

    // Sobrescribimos paintComponent para dibujar la imagen de fondo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }

    // METODO para animar el título con parpadeo suave
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
