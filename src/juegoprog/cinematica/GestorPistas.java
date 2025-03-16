package juegoprog.cinematica;

import juegoprog.graficos.Pantalla;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class GestorPistas {
    private final Pantalla ventana;
    private boolean enPista = false; // Controla si el jugador está en una pista
    private String pistaActual = null; // Guarda la pista en la que estamos
    private final Map<String, Pista> pistas = new HashMap<>();
    private final Map<String, Boolean> pistasVistas = new HashMap<>(); // Controla qué pistas ya fueron vistas

    // 🔹 Constructor
    public GestorPistas(Pantalla ventana) {
        this.ventana = ventana;

        // 🔹 Definir las pistas con coordenadas e imágenes
        pistas.put("76", new Pista(
                new Rectangle(1930, 3125, 198, 129),
                new String[]{"/resources/pistas/76A.png", "/resources/pistas/76B.png"}
        ));

        // 🔹 Inicializar el mapa de pistas vistas (todas comienzan como no vistas)
        for (String clave : pistas.keySet()) {
            pistasVistas.put(clave, false);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 🔹 Verifica si el jugador está sobre una pista y muestra el mensaje
    // ──────────────────────────────────────────────────────────────────────
    public void verificarPistas(int x, int y) {
        if (enPista) return; // No hacer nada si ya estamos en una pista

        for (Map.Entry<String, Pista> entry : pistas.entrySet()) {
            String clave = entry.getKey();
            Pista pista = entry.getValue();

            if (pista.area.contains(x, y) && !pistasVistas.get(clave)) {
                // 🔹 Mostrar el mensaje en pantalla como en la caja fuerte
                ventana.getMovimiento().setMostrarMensajePista(true);

                // 🔹 Al pulsar ENTER, se oculta el mensaje y se muestra la pista
                ventana.getMovimiento().agregarEventoEnter(() -> {
                    ventana.getMovimiento().setMostrarMensajePista(false);
                    mostrarPista(clave, pista.imagenes);
                });
                return; // Evita que se sigan revisando pistas
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 🔹 Muestra la pista en pantalla y permite navegar entre imágenes
    // ──────────────────────────────────────────────────────────────────────
    private void mostrarPista(String clave, String[] imagenes) {
        enPista = true;
        pistaActual = clave;
        pistasVistas.put(clave, true); // Marcar como vista para que no se repita

        JFrame pistaVentana = new JFrame();
        pistaVentana.setUndecorated(true);
        pistaVentana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pistaVentana.setSize(ventana.getWidth(), ventana.getHeight());
        pistaVentana.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon(getClass().getResource(imagenes[0])));

        // 🔹 Reproducimos sonido al mostrar la primera imagen
        ventana.getGestorSonidos().reproducirEfecto("/audio/NoirPista.wav");

        panel.add(label, BorderLayout.CENTER);

        JLabel texto = new JLabel("Pulsa ENTER para continuar", SwingConstants.CENTER);
        texto.setFont(new Font("Arial", Font.BOLD, 24));
        texto.setForeground(Color.WHITE);
        panel.add(texto, BorderLayout.NORTH);

        pistaVentana.add(panel);
        pistaVentana.setVisible(true);

        pistaVentana.addKeyListener(new KeyAdapter() {
            private int indiceImagen = 0;

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && indiceImagen < imagenes.length - 1) {
                    // 🔹 Cambia a la siguiente imagen si hay más de una
                    indiceImagen++;
                    label.setIcon(new ImageIcon(getClass().getResource(imagenes[indiceImagen])));

                    // 🔹 Cambia el mensaje cuando sea la última imagen
                    if (indiceImagen == imagenes.length - 1) {
                        texto.setText("Pulsa ESCAPE para salir");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // 🔹 Cierra la pista y permite volver al juego
                    enPista = false;
                    pistaActual = null;
                    pistaVentana.dispose();
                }
            }
        });
    }

    // ──────────────────────────────────────────────────────────────────────
    // 🔹 Clase interna para manejar las pistas (coordenadas + imágenes)
    // ──────────────────────────────────────────────────────────────────────
    private static class Pista {
        Rectangle area;
        String[] imagenes;

        public Pista(Rectangle area, String[] imagenes) {
            this.area = area;
            this.imagenes = imagenes;
        }
    }
}
