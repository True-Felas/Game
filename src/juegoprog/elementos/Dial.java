package juegoprog.elementos;

import juegoprog.graficos.Pantalla;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;

public class Dial extends JPanel {
    private double angulo = 270; // 🔹 Ajuste inicial
    private final int[] combinacion = {30, 180, 120}; // Secuencia correcta
    private int pasoActual = 0;
    private boolean desbloqueado = false;
    private final Pantalla ventana; // Referencia a la pantalla principal
    private BufferedImage imagenFondo; // Imagen de la caja fuerte
    private int ultimoNumero = -1; // Último número registrado

    public Dial(Pantalla ventana) {
        this.ventana = ventana;
        setFocusable(true);
        requestFocusInWindow(); // 🔹 Asegurar el foco en la ventana
        setPreferredSize(new Dimension(400, 400));

        // Cargar imagen de fondo
        try {
            imagenFondo = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/graficos/Caja_fuerte.png")));
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la imagen de la caja fuerte: " + e.getMessage());
        }

        // 🔹 Agregar esto para forzar que el panel tome el foco
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow(); // 🔹 Forzar el foco cuando el usuario haga clic
            }
        });

        addKeyListener(new KeyAdapter() {
            private long ultimaReproduccionSonido = 0; // 🔹 Guarda la última vez que sonó un efecto de la caja
            private final long delaySonidoCaja = 150; // 🔹 Intervalo mínimo en milisegundos entre sonidos

            @Override
            public void keyPressed(KeyEvent e) {
                if (!desbloqueado) {
                    long tiempoActual = System.currentTimeMillis(); // 🔹 Obtener tiempo actual

                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        angulo += 5;

                        // 🔹 Solo reproducir sonido si ha pasado el delay
                        if (tiempoActual - ultimaReproduccionSonido > delaySonidoCaja) {
                            String sonidoDerecha = new Random().nextBoolean() ? "/audio/NoirOpenSafeDer.wav" : "/audio/NoirOpenSafeDer2.wav";
                            ventana.getGestorSonidos().reproducirEfecto(sonidoDerecha);
                            ultimaReproduccionSonido = tiempoActual; // 🔹 Actualizar el último sonido reproducido
                        }

                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        angulo -= 5;

                        // 🔹 Solo reproducir sonido si ha pasado el delay
                        if (tiempoActual - ultimaReproduccionSonido > delaySonidoCaja) {
                            String sonidoIzquierda = new Random().nextBoolean() ? "/audio/NoirOpenSafeIZ.wav" : "/audio/NoirOpenSafeIZ2.wav";
                            ventana.getGestorSonidos().reproducirEfecto(sonidoIzquierda);
                            ultimaReproduccionSonido = tiempoActual; // 🔹 Actualizar el último sonido reproducido
                        }

                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                        ultimoNumero = ajustarAngulo(angulo);
                        comprobarCombinacion();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.out.println("🔹 Saliendo del minijuego...");
                        ventana.getMovimiento().setEnMinijuego(false);
                        ventana.cambiarPantalla("JUEGO");
                    }

                    if (angulo < 0) angulo += 360;
                    if (angulo >= 360) angulo -= 360;

                    repaint();
                }
            }


        });
    }

    /** Ajusta el ángulo para que coincida con los valores correctos */
    private int ajustarAngulo(double angulo) {
        int anguloNormalizado = (int) ((angulo + 360) % 360);
        return (anguloNormalizado) % 360; // 🔹 Ajuste final para que 270° esté arriba
    }

    /** Comprueba si la combinación es correcta */
    private int intentosFallidos = 0; // Contador de intentos fallidos

    /** Comprueba si la combinación es correcta */
    private void comprobarCombinacion() {
        if (ultimoNumero == combinacion[pasoActual]) { // 🔹 Solo avanza si el número es EXACTO
            pasoActual++;
            intentosFallidos = 0; // 🔹 Reinicia el contador de errores si acierta

            if (pasoActual >= combinacion.length) {
                desbloqueado = true;

                // 🔹 Reproducir sonido de apertura de caja fuerte
                ventana.getGestorSonidos().reproducirEfecto("/audio/NoirOpenSafeF.wav");


                // 🔹 Mostrar mensaje narrativo
                JOptionPane.showMessageDialog(
                        null,
                        "<html><center>La caja cede con un clic metálico…<br>Dentro, los secretos que alguien no quería que viera.</center></html>",
                        "CAJA FUERTE DESBLOQUEADA",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // 🔹 Reproducir sonido de papeles al abrir los documentos
                ventana.getGestorSonidos().reproducirEfecto("/audio/NoirPaper.wav");

                // 🔹 Mostrar la imagen de los documentos
                JLabel texto = new JLabel("¡Has encontrado los documentos que buscabas!", SwingConstants.CENTER);
                texto.setFont(new Font("Arial", Font.BOLD, 25));
                texto.setForeground(Color.WHITE);

                JPanel panelMensaje = new JPanel();
                panelMensaje.setBackground(Color.BLACK);
                panelMensaje.add(texto);

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(panelMensaje, BorderLayout.NORTH);
                panel.add(new JLabel(new ImageIcon(getClass().getResource("/resources/graficos/documentos.png"))), BorderLayout.CENTER);

                JDialog dialogo = new JDialog();
                dialogo.setTitle("Documentos encontrados");
                dialogo.setModal(true);
                dialogo.setContentPane(panel);
                dialogo.pack();
                dialogo.setLocationRelativeTo(null);

                // 🔹 Habilitar ESC para cerrar
                dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

                dialogo.setVisible(true);

                ventana.getMovimiento().setEnMinijuego(false);
                ventana.cambiarPantalla("JUEGO");
            }
        } else {
            intentosFallidos++; // 🔹 Aumenta el contador de intentos fallidos

            if (intentosFallidos >= 3) { // 🔹 Si falla 3 veces, reinicia todo y muestra error
                pasoActual = 0;
                intentosFallidos = 0;
                JOptionPane.showMessageDialog(null, "Combinación incorrecta. Inténtalo de nuevo.", "NO HUBO SUERTE", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    /** Dibuja la caja fuerte con el dial y los grados numerados */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dibujar imagen de fondo si está cargada
        if (imagenFondo != null) {
            g2.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        int radio = 100;

        // Dibujar dial
        g2.setColor(Color.GRAY);
        g2.fillOval(centroX - radio, centroY - radio, 2 * radio, 2 * radio);
        g2.setColor(Color.BLACK);
        g2.drawOval(centroX - radio, centroY - radio, 2 * radio, 2 * radio);

        // Dibujar marcador rojo
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.RED);
        int puntaX = (int) (centroX + radio * Math.cos(Math.toRadians(angulo)));
        int puntaY = (int) (centroY + radio * Math.sin(Math.toRadians(angulo)));
        g2.drawLine(centroX, centroY, puntaX, puntaY);

        // Dibujar número actual arriba del dial
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String textoActual = "Ángulo: " + ajustarAngulo(angulo) + "°";
        FontMetrics metrics = g2.getFontMetrics();
        int xTexto = (getWidth() - metrics.stringWidth(textoActual)) / 2;
        g2.drawString(textoActual, xTexto, 30);

        // Dibujar último número registrado
        if (ultimoNumero != -1) {
            String textoUltimo = "Último registrado: " + ultimoNumero + "°";
            g2.drawString(textoUltimo, xTexto, 60);
        }
    }
}
