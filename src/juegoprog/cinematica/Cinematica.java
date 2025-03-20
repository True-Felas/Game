package juegoprog.cinematica;

import juegoprog.graficos.Pantalla;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cinematica extends JPanel implements ActionListener {

    // ===========================================
    // 1. VARIABLES PRINCIPALES
    // ===========================================
    private final List<Image> imagenes = new ArrayList<>(); // Lista de imágenes a mostrar
    private int indiceActual = 0;           // Índice de la imagen actual
    private float alpha = 0f;              // Opacidad para el efecto de fade-in (0.0 -> 1.0)
    private float escala = 1.0f;           // Factor de zoom
    private final Timer timer;             // Timer principal para animar (fade-in/zoom)
    private final Timer cambioImagenTimer; // Timer para avanzar a la siguiente imagen
    private final Pantalla ventana;        // Referencia al contenedor principal (para cambiar a "JUEGO")

    // ---------- Nuevas variables para el "fade out" ----------
    private boolean finalizando = false;   // Indica que estamos en fase de fundido a negro
    private float alphaSalida = 0f;        // Opacidad para el rectángulo negro encima (0 -> 1)

    /**
     * Constructor de la Cinemática.
     * Recibe la ventana principal (Pantalla) donde se muestra la animación.
     */
    public Cinematica(Pantalla ventana) {
        this.ventana = ventana;
        setOpaque(true);
        setBackground(Color.BLACK);
        setSize(ventana.getSize());
        setVisible(true);
        requestFocusInWindow();

        cargarImagenes();

        ventana.getGestorMusica().reproducirMusicaSecuencial(
                "/resources/audio/Noir City - Find Me Again & Tension Intro.wav",
                "/resources/audio/Noir City - Find Me Again & Tension Loop.wav"
        );

        // Timer que actualiza la animación ~ 25 FPS (cada 40 ms)
        timer = new Timer(50, this);
        timer.start();

        // Timer que pasa a la siguiente imagen cada 20 s
        cambioImagenTimer = new Timer(10900, e -> siguienteImagen());
        cambioImagenTimer.start();

        // Si se hace clic, empieza el fade out en lugar de cortar en seco
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                iniciarFadeOut();
            }
        });
    }

    // ===========================================
    // 2. CARGAR IMÁGENES
    // ===========================================
    private void cargarImagenes() {
        String[] archivos = {
                "/cinematicas/imagen1OB.png",
                "/cinematicas/imagen2OB.PNG",
                "/cinematicas/imagen3OB.PNG",
                "/cinematicas/imagen4OB.PNG",
        };

        for (String archivo : archivos) {
            try {
                java.net.URL imgURL = getClass().getResource(archivo);
                if (imgURL != null) {
                    imagenes.add(ImageIO.read(imgURL));
                } else {
                    System.err.println("❌ ERROR: No se encontró la imagen -> " + archivo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ===========================================
    // 3. LOGICA: AVANZAR O FINALIZAR
    // ===========================================
    private void siguienteImagen() {
        new Thread(() -> {
            // Si NO es la última imagen, hacer el fade-out normal
            if (indiceActual < imagenes.size() - 1) {
                for (float i = alpha; i >= 0; i -= 0.05f) {
                    alpha = i;
                    repaint();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Cambiar de imagen después del fade-out (excepto en la última)
            indiceActual++;

            if (indiceActual >= imagenes.size()) {
                indiceActual = imagenes.size() - 1;

                // 🔹 Si es la última imagen, esperamos más tiempo ANTES del fade-out
                try {
                    Thread.sleep(8000); // ⏳ Espera 5 segundos extra antes del fade final
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                iniciarFadeOut(); // 🔹 Luego de la espera, iniciar el fade-out final
            } else {
                alpha = 0f; // Reinicia fade-in para la nueva imagen
            }

        }).start();
    }



    /**
     * Método para iniciar el proceso de “fade out” al finalizar la cinemática
     * (en vez de llamarlo directamente a terminarCinematica).
     */
    private void iniciarFadeOut() {
        // Evita que lo llamemos dos veces
        if (!finalizando) {
            finalizando = true;
            // Paramos el temporizador que avanza imágenes
            cambioImagenTimer.stop();
        }
    }

    /**
     * Finaliza la cinemática realmente (se llama tras completar el fundido a negro).
     */
    private void terminarCinematica() {
        timer.stop();

        // Reactivar la lógica normal del juego
        ventana.setEnCinematica(false);
        ventana.cambiarPantalla("JUEGO");

        SwingUtilities.invokeLater(() -> {
            // Pedimos el foco para el movimiento (lo que ya tenías)
            ventana.getMovimiento().requestFocusInWindow();

            // 1) Obtenemos el glassPane de la ventana (panel que se dibuja por encima de todo)
            JRootPane root = ventana.getRootPane();
            JPanel glass = (JPanel) root.getGlassPane();

            // 2) Configuramos el glassPane como un panel opaco y negro
            glass.setOpaque(true);
            glass.setBackground(Color.BLACK);

            // 3) Lo hacemos visible (ahora tapa todo con negro)
            glass.setVisible(true);

            // 4) Programamos un Timer de 300 ms (o el tiempo que quieras) que lo oculte
            Timer t = new Timer(500, e -> {
                // Quita el velo negro
                glass.setVisible(false);
            });
            t.setRepeats(false);
            t.start();
        });
    }


    // ===========================================
    // 4. EVENTOS DE TIMER: FADE-IN, ZOOM, FADE-OUT
    // ===========================================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!finalizando) {
            // Mientras no estemos finalizando:
            // 1) Incrementar fade in
            alpha = Math.min(alpha + 0.02f, 1f);

            // 2) Incrementar zoom, con un valor algo menor para que tiemble menos
            escala += 0.0002f;
        } else {
            // Si estamos en fase de fade out, aumentar alphaSalida
            alphaSalida = Math.min(alphaSalida + 0.08f, 1f);

            // Si ya se cubrió completamente la pantalla, terminar
            if (alphaSalida >= 1f) {
                terminarCinematica();
                return; // Evita repintar tras “terminar”
            }
        }

        repaint();
    }

    // ===========================================
    // 5. DIBUJO EN PANTALLA
    // ===========================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagenes.isEmpty()) {
            return; // Si no hay imágenes, nada que dibujar
        }

        Graphics2D g2d = (Graphics2D) g;

        // ---- Activar antialiasing e interpolación bilinear ----
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // ---- DIBUJO FADE-IN ----
        // Ajustar la opacidad de la imagen
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Calcular coordenadas para centrar la imagen con el zoom
        Image imgActual = imagenes.get(indiceActual);
        int imgWidth = imgActual.getWidth(this);
        int imgHeight = imgActual.getHeight(this);

        // Ojo con el int: un escalado muy preciso puede perder decimales
        int dibujarAncho = (int) (imgWidth * escala);
        int dibujarAlto = (int) (imgHeight * escala);

        int drawX = (getWidth() - dibujarAncho) / 2;
        int drawY = (getHeight() - dibujarAlto) / 2;

        // Dibujamos la imagen
        g2d.drawImage(imgActual, drawX, drawY, dibujarAncho, dibujarAlto, this);

        // ---- DIBUJO FADE-OUT ----
        if (finalizando) {
            // Si estamos en fade out, pintamos un rectángulo negro por encima
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaSalida));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
