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
    private final JLabel titulo; // Título animado
    private JButton botonJugar; // ¡Ahora accesible desde otros lugares!

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
        fondo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Fondo_Menu.png"))).getImage();
        setLayout(null);

        gestorMusica.reproducirMusica("/resources/audio/Intro_NoirCity_Find Me Again.wav");

        titulo = new JLabel("NOIR CITY", SwingConstants.CENTER);
        titulo.setFont(cargarFuentePersonalizada("/resources/fonts/Noir_medium.otf", 50));
        titulo.setForeground(new Color(250, 240, 230));
        titulo.setBounds(300, 20, 700, 80);
        add(titulo);
        animarTitulo();

        ImageIcon imgJugar = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Iniciar2.png")));
        ImageIcon imgControles = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Controles2.png")));
        ImageIcon imgSalir = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/menu/Salir2.png")));
        ImageIcon icono = new ImageIcon(getClass().getResource("/resources/graficos/audio_icon.png"));

        botonJugar = getJugar((Pantalla) ventana, imgJugar);
        add(botonJugar);

        JButton botonControles = new JButton(imgControles);
        botonControles.setBounds(500, 400, 300, 80);
        botonControles.setBorderPainted(false);
        botonControles.setContentAreaFilled(false);
        botonControles.setFocusPainted(false);
        botonControles.addActionListener(_ -> {
            JTextArea areaTexto = new JTextArea("""
                
•  W A S D   | Moverse
•  RATÓN     | Apuntar
•  CLICK IZQ | Disparar
•  ESPACIO   | Correr
•  ENTER     | Acción
•  ESCAPE    | Salir
""");
            areaTexto.setFont(new Font("Courier New", Font.PLAIN, 16));
            areaTexto.setEditable(false);
            areaTexto.setOpaque(false);
            areaTexto.setFocusable(false);
            areaTexto.setBorder(null);
            areaTexto.setHighlighter(null);

            JOptionPane pane = new JOptionPane(
                    areaTexto,
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.DEFAULT_OPTION
            );
            JDialog dialog = pane.createDialog(botonJugar, "🕹️ Controles del juego");
            dialog.setVisible(true);
        });
        add(botonControles);

        JButton botonSalir = new JButton(imgSalir);
        botonSalir.setBounds(500, 480, 300, 80);
        botonSalir.setBorderPainted(false);
        botonSalir.setContentAreaFilled(false);
        botonSalir.setFocusPainted(false);
        botonSalir.addActionListener(_ -> System.exit(0));
        add(botonSalir);

        // 🔊 Icono clicable de audio en la esquina inferior derecha
        JButton botonAudio = new JButton(icono);
        botonAudio.setBounds(1200, 620, 35, 35); // Ajustado para ser visible
        botonAudio.setBorderPainted(false);
        botonAudio.setContentAreaFilled(false);
        botonAudio.setFocusPainted(false);
        botonAudio.setToolTipText("Recomendación de audio");
        botonAudio.addActionListener(e -> {
            JTextArea textoAudio = new JTextArea("¡Activa el sonido para una experiencia completa!");

            textoAudio.setFont(new Font("Courier New", Font.PLAIN, 16));
            textoAudio.setEditable(false);
            textoAudio.setOpaque(false);
            textoAudio.setFocusable(false);
            textoAudio.setBorder(null);
            textoAudio.setHighlighter(null);

            JOptionPane.showMessageDialog(
                    this,
                    textoAudio,
                    "\uD83C\uDFA7 Consejo Noir",
                    JOptionPane.PLAIN_MESSAGE
            );
        });
        add(botonAudio); // <- Añadido al final para que quede encima
    }

    private JButton getJugar(Pantalla ventana, ImageIcon imgJugar) {
        JButton boton = new JButton(imgJugar);
        boton.setBounds(500, 320, 300, 80);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.addActionListener(_ -> {
            gestorMusica.fadeOutMusica(2000);
            ventana.cambiarPantalla("CINEMATICA");
        });
        return boton;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }

    private void animarTitulo() {
        Timer timer = new Timer(100, new ActionListener() {
            float alpha = 1.0f;
            boolean bajando = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += bajando ? -0.05f : 0.05f;
                if (alpha <= 0.7f || alpha >= 1.0f) bajando = !bajando;
                titulo.setForeground(new Color(250, 240, 230, (int) (alpha * 255)));
            }
        });
        timer.start();
    }
}