package juegoprog.graficos;

import juegoprog.elementos.GestorEnemigos;
import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.jugador.Personaje;
import juegoprog.sistema.MenuPrincipal;
import juegoprog.controles.Movimiento;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal para gestionar la pantalla principal y las distintas vistas (Menú y Juego).
 * También implementa el bucle principal para la lógica y el renderizado del juego.
 */
public class Pantalla extends JFrame {

    //---------------------------------------------------
    // 🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------

    private final CardLayout cardLayout;
    private final JPanel contenedorPrincipal;
    private final JLayeredPane capaJuego;
    private final Movimiento movimiento;
    private final EscenarioDistritoSombrio escenario;
    private final ColisionesPanel colisiones;
    private final Minimapa minimapa;

    // 🔹 Nuevos atributos (modificación para enemigos)
    private Personaje personaje;  // Personaje principal
    private final GestorEnemigos gestorEnemigos; // Gestor central de enemigos

    private int frameCount = 0; // Contador de frames
    private long lastTime = System.nanoTime(); // Última medición de tiempo

    //---------------------------------------------------
    // 🔹 CONSTRUCTOR Y CONFIGURACIÓN INICIAL
    //---------------------------------------------------

    /**
     * Configura la ventana del juego, las pantallas y las capas de la interfaz.
     */
    public Pantalla() {
        // Configuración de ventana principal
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
        escenario.setBounds(0, 0, 4472, 4816); // Coordenadas para el fondo del mapa completo
        capaJuego.add(escenario, JLayeredPane.DEFAULT_LAYER);

        // 🔹 PNG de colisiones (capa oculta para detectar colisiones en el mapa)
        colisiones = new ColisionesPanel();
        colisiones.setBounds(0, 0, 4472, 4816);
        capaJuego.add(colisiones, JLayeredPane.PALETTE_LAYER);

        // 🔹 Crear el objeto Personaje para pasarlo al controlador Movimiento
        personaje = new Personaje();

        // 🔹 Configuración del Control de Movimiento (Personaje, Enemigos y Balas)
        movimiento = new Movimiento(escenario, colisiones, personaje); // 🔹 Modificado para incluir enemigos
        movimiento.setBounds(0, 0, 1280, 720); // Tamaño de la "vista" de la pantalla
        capaJuego.add(movimiento, JLayeredPane.MODAL_LAYER);

        // 🔹 Crear el gestor de enemigos
        gestorEnemigos = new GestorEnemigos();

        // 🔹 Minimapa para mostrar la posición del jugador y el mapa entero
        minimapa = new Minimapa(personaje, gestorEnemigos, 4472, 4816);
        minimapa.setBounds(getWidth() - 237, getHeight() - 280, 217, 236); // Coloca el minimapa en una esquina.
        capaJuego.add(minimapa, JLayeredPane.DRAG_LAYER); // Capas superiores.

        // 🔹 Agregar la pantalla de juego al contenedor de pantallas
        contenedorPrincipal.add(capaJuego, "JUEGO");

        // 🔹 Inicia el bucle del juego con un hilo optimizado
        iniciarBucle();

        // Hacer visible la ventana principal
        setVisible(true);
    }

    //---------------------------------------------------
    // 🔹 CAMBIO ENTRE PANTALLAS
    //---------------------------------------------------

    /**
     * Cambia entre pantallas (Menú o Juego) dentro del CardLayout.
     * @param pantalla Nombre de la pantalla ("MENU" o "JUEGO").
     */
    public void cambiarPantalla(String pantalla) {
        cardLayout.show(contenedorPrincipal, pantalla);

        if ("JUEGO".equals(pantalla)) {
            // Nos aseguramos que el componente de movimiento pueda capturar eventos de teclado.
            SwingUtilities.invokeLater(movimiento::requestFocusInWindow);
        }
    }

    //---------------------------------------------------
    // 🔹 BUCLE PRINCIPAL
    //---------------------------------------------------

    /**
     * Inicia el bucle principal para la lógica y renderización del juego.
     */
    private void iniciarBucle() {
        new Thread(() -> {
            final int fps = 60; // Frames por segundo deseados
            final long frameTime = 1_000_000_000L / fps; // Tiempo de cada frame en nanosegundos

            while (true) {
                long startTime = System.nanoTime();

                // Actualiza la lógica del juego
                actualizar();

                // Renderiza el juego
                repaint();

                // Calcula cuánto tiempo queda del frame y espera
                long elapsedTime = System.nanoTime() - startTime;
                long sleepTime = frameTime - elapsedTime;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1_000_000L); // Convertir a milisegundos
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //---------------------------------------------------
    // 🔹 ACTUALIZACIÓN DE LÓGICA EN EL BUCLE
    //---------------------------------------------------

    /**
     * Actualiza cualquier lógica del juego que necesite cambiar entre frames.
     */
    private void actualizar() {
        // 🔹 Actualiza el movimiento del jugador y lógica de las balas y enemigos
        movimiento.moverJugador();
        // 🔹 Calcula y actualiza los FPS en el título de la ventana
        calcularYActualizarFPS();
    }

    //---------------------------------------------------
    // 🔹 CÁLCULO Y ACTUALIZACIÓN DE FPS
    //---------------------------------------------------

    /**
     * Calcula los FPS y actualiza el título de la ventana.
     */
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