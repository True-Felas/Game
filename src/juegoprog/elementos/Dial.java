package juegoprog.elementos;

import juegoprog.graficos.Pantalla;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/** Representa el minijuego de la caja fuerte con un dial giratorio.
 * El jugador gira el dial con las flechas izquierda/derecha e ingresa la combinación
 * correcta pulsando Enter (o Espacio). Tras completarla, se desbloquea y vuelve al juego. */

public class Dial extends JPanel {

    /** Ángulo actual del dial (por defecto, 270°).
     * Indica la posición de la "flecha roja" sobre el círculo. */

    private double angulo = 270;

    /** Arreglo con la secuencia (en grados) que compone la combinación correcta.
     * Cada valor debe coincidir exactamente para avanzar de paso. */
    private final int[] combinacion = {75, 35, 10};

    /** Índice del paso de la combinación que se está intentando resolver.
     * Si acierta un valor de 'combinacion', se incrementa. Al completarse,
     * la caja fuerte se desbloquea. */
    private int pasoActual = 0;

    /** Bandera para indicar si la caja fuerte ya se encuentra desbloqueada.
     * Si es true, se ignoran los movimientos del dial. */
    private boolean desbloqueado = false;

    /** Referencia a la ventana principal (Pantalla).
     * Permite reproducir sonidos, cambiar de pantalla, etc. */
    private final Pantalla ventana;

    /** Imagen de fondo (la caja fuerte). Se dibuja detrás del dial. */
    private BufferedImage imagenFondo;

    /** Guarda el valor (en grados) del último giro registrado al pulsar Enter o Espacio.
     * Se compara con el valor esperado en la secuencia 'combinacion'. */
    private int ultimoNumero = -1;

    /** Contador de intentos fallidos: si falla 3 veces consecutivas en un paso,
     * se reinicia toda la secuencia y muestra un mensaje de error. */
    private int intentosFallidos = 0;

    /** Crea el panel del dial, carga la imagen de la caja fuerte y
     * registra los listeners de teclado y ratón. */
    public Dial(Pantalla ventana) {
        this.ventana = ventana;
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(400, 400));

        // Cargar la imagen de fondo
        try {
            imagenFondo = ImageIO.read(
                    Objects.requireNonNull(getClass().getResource("/resources/graficos/Caja_fuerte.png"))
            );
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la imagen de la caja fuerte: " + e.getMessage());
        }

        // Forzar el foco cuando el usuario hace clic en el panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // Configuración de las teclas para girar el dial y confirmar pasos
        addKeyListener(new KeyAdapter() {
            private long ultimaReproduccionSonido = 0;  // Controla el intervalo mínimo entre sonidos
            private final long delaySonidoCaja = 150;   // En milisegundos

            @Override
            public void keyPressed(KeyEvent e) {
                if (!desbloqueado) {
                    long tiempoActual = System.currentTimeMillis();

                    // Girar a la derecha
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        angulo += 5;
                        if (tiempoActual - ultimaReproduccionSonido > delaySonidoCaja) {
                            String sonidoDerecha = new Random().nextBoolean()
                                    ? "/audio/NoirOpenSafeDer.wav"
                                    : "/audio/NoirOpenSafeDer2.wav";
                            ventana.getGestorSonidos().reproducirEfecto(sonidoDerecha);
                            ultimaReproduccionSonido = tiempoActual;
                        }
                    }

                    // Girar a la izquierda
                    else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        angulo -= 5;
                        if (tiempoActual - ultimaReproduccionSonido > delaySonidoCaja) {
                            String sonidoIzquierda = new Random().nextBoolean()
                                    ? "/audio/NoirOpenSafeIZ.wav"
                                    : "/audio/NoirOpenSafeIZ2.wav";
                            ventana.getGestorSonidos().reproducirEfecto(sonidoIzquierda);
                            ultimaReproduccionSonido = tiempoActual;
                        }
                    }

                    // Registrar la posición actual en la secuencia
                    else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                        ultimoNumero = ajustarAngulo(angulo);
                        comprobarCombinacion();
                    }

                    // Esc para salir del minijuego
                    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.out.println("🔹 Saliendo del minijuego...");
                        ventana.getMovimiento().setEnMinijuego(false);
                        ventana.cambiarPantalla("JUEGO");
                    }

                    // Ajustar el ángulo dentro de 0..359
                    if (angulo < 0) {
                        angulo += 360;
                    }
                    if (angulo >= 360) {
                        angulo -= 360;
                    }

                    repaint();
                }
            }
        });
    }

    /** Ajusta el ángulo para que sea un valor entre 0 y 359. */
    private int ajustarAngulo(double angulo) {
        int anguloNormalizado = (int) ((angulo + 360) % 360);
        return anguloNormalizado % 360;
    }

    /** Comprueba si el último número registrado coincide con el de la combinación en el paso actual.
     * Si acierta, avanza de paso; si falla 3 veces, se reinicia la secuencia. */
    private void comprobarCombinacion() {
        if (ultimoNumero == combinacion[pasoActual]) {
            pasoActual++;
            intentosFallidos = 0;

            // Si ya completó toda la combinación
            if (pasoActual >= combinacion.length) {
                desbloquearCajaFuerte();
            }
        } else {
            intentosFallidos++;
            if (intentosFallidos >= 3) {
                pasoActual = 0;
                intentosFallidos = 0;
                JOptionPane.showMessageDialog(
                        null,
                        "Combinación incorrecta. Inténtalo de nuevo.",
                        "NO HUBO SUERTE",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /** Lógica al completar la combinación de la caja.
     * Reproduce sonido de desbloqueo, muestra un mensaje y un diálogo con los documentos,
     * y finalmente regresa a la pantalla del juego. */
    private void desbloquearCajaFuerte() {
        desbloqueado = true;
        ventana.getGestorSonidos().reproducirEfecto("/audio/NoirOpenSafeF.wav");

        JOptionPane.showMessageDialog(
                null,
                "<html><center>La caja cede con un clic metálico…<br>"
                        + "Dentro, los secretos que alguien no quería que vieras.</center></html>",
                "CAJA FUERTE DESBLOQUEADA",
                JOptionPane.INFORMATION_MESSAGE
        );

        ventana.getGestorSonidos().reproducirEfecto("/audio/NoirPaper.wav");

        // Mostrar imagen de documentos
        JLabel texto = new JLabel("¡Has encontrado los documentos que buscabas!", SwingConstants.CENTER);
        texto.setFont(new Font("Arial", Font.BOLD, 25));
        texto.setForeground(Color.WHITE);

        JPanel panelMensaje = new JPanel();
        panelMensaje.setBackground(Color.BLACK);
        panelMensaje.add(texto);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(panelMensaje, BorderLayout.NORTH);
        panel.add(new JLabel(new ImageIcon(
                        getClass().getResource("/resources/graficos/documentos.png"))),
                BorderLayout.CENTER
        );

        JDialog dialogo = new JDialog();
        dialogo.setTitle("Documentos encontrados");
        dialogo.setModal(true);
        dialogo.setContentPane(panel);
        dialogo.pack();
        dialogo.setLocationRelativeTo(null);

        // Permitir cerrar con ESC
        dialogo.getRootPane().registerKeyboardAction(
                e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        dialogo.setVisible(true);

        // Volver al juego
        ventana.getMovimiento().setEnMinijuego(false);
        ventana.cambiarPantalla("JUEGO");
    }

    /** Renderiza el panel de la caja fuerte y el dial en su posición actual.
     *  @param g objeto Graphics para dibujar en pantalla. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dibujar la imagen de la caja fuerte (fondo)
        if (imagenFondo != null) {
            g2.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        int radio = 100;

        // Dial circular
        g2.setColor(Color.GRAY);
        g2.fillOval(centroX - radio, centroY - radio, 2 * radio, 2 * radio);
        g2.setColor(Color.BLACK);
        g2.drawOval(centroX - radio, centroY - radio, 2 * radio, 2 * radio);

        // Flecha roja
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.RED);
        int puntaX = (int) (centroX + radio * Math.cos(Math.toRadians(angulo)));
        int puntaY = (int) (centroY + radio * Math.sin(Math.toRadians(angulo)));
        g2.drawLine(centroX, centroY, puntaX, puntaY);

        // Texto del ángulo
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String textoActual = "Ángulo: " + ajustarAngulo(angulo) + "°";
        FontMetrics metrics = g2.getFontMetrics();
        int xTexto = (getWidth() - metrics.stringWidth(textoActual)) / 2;
        g2.drawString(textoActual, xTexto, 30);

        // Texto del último valor registrado
        if (ultimoNumero != -1) {
            String textoUltimo = "Último registrado: " + ultimoNumero + "°";
            g2.drawString(textoUltimo, xTexto, 60);
        }
    }
}
