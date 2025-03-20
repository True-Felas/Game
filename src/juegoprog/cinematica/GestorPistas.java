package juegoprog.cinematica;

import juegoprog.elementos.Enemigo;
import juegoprog.elementos.GestorEnemigos;
import juegoprog.graficos.Pantalla;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GestorPistas {
    private final Pantalla ventana;
    private boolean enPista = false; // Controla si el jugador está en una pista
    private String pistaActual = null; // Guarda la pista en la que estamos
    private final Map<String, Pista> pistas = new HashMap<>();
    private final Map<String, Boolean> pistasVistas = new HashMap<>(); // Controla qué pistas ya fueron vistas
    private final GestorEnemigos gestorEnemigos; // Referencia al GestorEnemigos


    // 🔹 Constructor
    public GestorPistas(Pantalla ventana,GestorEnemigos gestorEnemigos) {
        this.ventana = ventana;
        this.gestorEnemigos= gestorEnemigos;


        // 🔹 Definir las pistas con coordenadas e imágenes

        // ──────—─────── PISTA UNO: 76 ──────────────

        pistas.put("76", new Pista(
                new Rectangle(1930, 3125, 198, 129),
                new String[]{"/resources/pistas/76A.png", "/resources/pistas/76B.png"}
        ));

        // ────────—───── NUEVA PISTA AÑADIDA: SEGUNDA 190 ──────────────

        pistas.put("190", new Pista(
                new Rectangle(2823, 1283, 251, 228),
                new String[]{"/resources/pistas/190A.png", "/resources/pistas/190B.png", "/resources/pistas/190C.png"}
        ));
        // ──────────────────────────────────────────────────

        // ────────────── NUEVA PISTA: TERCERA 35 ──────────────
        pistas.put("35", new Pista(
                new Rectangle(2670, 3079, 230, 120), // Coordenadas basadas en los datos proporcionados
                new String[]{"/resources/pistas/35A.png", "/resources/pistas/35B.png", "/resources/pistas/35C.png"}
        ));
// ───────────────────────────────────────────────────


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

        // Detener a los enemigos al entrar en una pista
        detenerEnemigos();

        JFrame pistaVentana = new JFrame();
        pistaVentana.setUndecorated(true);
        pistaVentana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pistaVentana.setSize(ventana.getWidth(), ventana.getHeight());
        pistaVentana.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagenes[0]))));

        // 🔹 Seleccionar la locución según la pista
        String[] locuciones;
        switch (clave) {
            case "76": // Pista 1
                locuciones = new String[]{"/audio/NoirPist1A.wav", "/audio/NoirPist1B.wav"};
                break;
            case "190": // Pista 2
                locuciones = new String[]{"/audio/NoirPist2A.wav", "/audio/NoirPist2B.wav", "/audio/NoirPist2C.wav"};
                break;
            case "35": // Pista 3 (cuando la tengas)
                locuciones = new String[]{"/audio/NoirPist3A.wav", "/audio/NoirPist3B.wav", "/audio/NoirPist3C.wav"};
                break;
            default:
                locuciones = new String[]{}; // Si no hay locuciones, no se reproduce nada
                break;
        }


        // 🔹 Reproducimos la locución de la primera imagen
        if (locuciones.length > 0) {
            ventana.getGestorSonidos().reproducirEfecto(locuciones[0]);
        }

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
                    // 🔹 Cambia a la siguiente imagen
                    indiceImagen++;
                    label.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagenes[indiceImagen]))));

                    // 🔹 Reproducir la locución correspondiente a esta imagen
                    if (indiceImagen < locuciones.length) {
                        ventana.getGestorSonidos().reproducirEfecto(locuciones[indiceImagen]);
                    }

                    // 🔹 Cambia el mensaje cuando sea la última imagen
                    if (indiceImagen == imagenes.length - 1) {
                        texto.setText("Pulsa ESCAPE para salir");
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // 🔹 Cierra la pista y permite volver al juego
                    enPista = false;
                    pistaActual = null;
                    reanudarEnemigos();
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
    // Metodo para detener todos los enemigos usando GestorEnemigos
    private void detenerEnemigos() {
        for (Enemigo enemigo : gestorEnemigos.getEnemigos()) {
            enemigo.detener(); // Llamamos al metodo detener() del enemigo
        }
    }

    // Metodo para reanudar todos los enemigos usando GestorEnemigos
    private void reanudarEnemigos() {
        for (Enemigo enemigo : gestorEnemigos.getEnemigos()) {
            enemigo.reanudar(); // Volvemos a habilitar el movimiento
        }
    }


}
