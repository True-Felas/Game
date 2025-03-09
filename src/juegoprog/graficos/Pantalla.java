package juegoprog.graficos;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.jugador.Personaje;
import juegoprog.sistema.MenuPrincipal;
import juegoprog.controles.Movimiento;

import javax.swing.*;
import java.awt.*;

public class Pantalla extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contenedorPrincipal;
    private final JLayeredPane capaJuego;
    private final Movimiento movimiento;
    private final EscenarioDistritoSombrio escenario;
    private final ColisionesPanel colisiones;
    private final Minimapa minimapa;

    private int frameCount = 0; // Contador de frames
    private long lastTime = System.nanoTime(); // Última medición de tiempo

    /** Configura la ventana del juego, las pantallas y las capas de la interfaz. */
    public Pantalla() {
        setTitle("Juego - Pantalla Principal");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 Configuración de CardLayout para gestionar pantallas
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);
        setContentPane(contenedorPrincipal);

        // 🔹 Menú principal
        contenedorPrincipal.add(new MenuPrincipal(this), "MENU");

        // 🔹 Configuración de la pantalla de juego con capas
        capaJuego = new JLayeredPane();
        capaJuego.setPreferredSize(new Dimension(1280, 720));

        // 🔹 Fondo del escenario
        escenario = new EscenarioDistritoSombrio();
        escenario.setBounds(0, 0, 4472, 4816);
        capaJuego.add(escenario, JLayeredPane.DEFAULT_LAYER);

        // 🔹 PNG de colisiones
        colisiones = new ColisionesPanel();
        colisiones.setBounds(0, 0, 4472, 4816);
        capaJuego.add(colisiones, JLayeredPane.PALETTE_LAYER);

        // 🔹 Crear el objeto Personaje para pasarlo a Movimiento
        Personaje personaje = new Personaje();

        // 🔹 Movimiento (Personaje), pasando referencias de escenario y colisiones
        movimiento = new Movimiento(escenario, colisiones, personaje);
        movimiento.setBounds(0, 0, 1280, 720);
        capaJuego.add(movimiento, JLayeredPane.MODAL_LAYER);

        // 🔹 Minimapa
        minimapa = new Minimapa(escenario.getFondo(), personaje, 4472, 4816); // Dimensiones del mapa completo
        minimapa.setBounds(20, 20, 200, 200); // Coloca el minimapa en una esquina.
        capaJuego.add(minimapa, JLayeredPane.MODAL_LAYER); // Capas superiores.

        // 🔹 Agregar la pantalla de juego al contenedor de pantallas
        contenedorPrincipal.add(capaJuego, "JUEGO");

        // 🔹 Inicia el bucle del juego
        iniciarBucle();

        setVisible(true);
    }

    /** Cambia entre pantallas (Menú o Juego) dentro del CardLayout. */
    public void cambiarPantalla(String pantalla) {
        cardLayout.show(contenedorPrincipal, pantalla);

        if ("JUEGO".equals(pantalla)) {
            SwingUtilities.invokeLater(movimiento::requestFocusInWindow);
        }
    }

    /** Inicia un bucle para gestionar los FPS y renderizar continuamente. */
    private void iniciarBucle() {
        new Thread(() -> {
            while (true) {
                // Actualiza el estado del juego
                actualizar();
                try {
                    Thread.sleep(16); // Aproximadamente 60 FPS (1000 ms / 60 = 16.66 ms por frame)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /** Actualiza cualquier lógica del juego que necesite cambiar entre frames. */
    private void actualizar() {
        movimiento.moverJugador(); // Mueve al personaje (incluye lógica de colisiones)
        calcularYActualizarFPS(); // Calcula y actualiza FPS
        repaint(); // Redibuja todas las capas principales
    }

    /** Calcula los FPS y actualiza el título de la ventana. */
    private void calcularYActualizarFPS() {
        frameCount++;
        long currentTime = System.nanoTime();

        // Si ha pasado más de 1 segundo, actualiza el título
        if (currentTime - lastTime >= 1_000_000_000L) {
            double fps = frameCount / ((currentTime - lastTime) / 1e9); // Cuántos frames en 1 segundo
            frameCount = 0; // Reiniciar el contador
            lastTime = currentTime; // Reiniciar el tiempo

            // Actualizar el título con los FPS
            SwingUtilities.invokeLater(() -> setTitle("Juego - FPS: " + String.format("%.2f", fps)));
        }
    }
}