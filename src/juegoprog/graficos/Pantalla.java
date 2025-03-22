package juegoprog.graficos;

import juegoprog.audio.GestorMusica;
import juegoprog.audio.GestorSonidos;
import juegoprog.cinematica.Cinematica;
import juegoprog.cinematica.FinalMision;
import juegoprog.cinematica.GestorPistas;
import juegoprog.elementos.Dial;
import juegoprog.elementos.Enemigo;
import juegoprog.elementos.GestorEnemigos;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.jugador.Personaje;
import juegoprog.sistema.MenuPrincipal;
import juegoprog.controles.Movimiento;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

/**
 * Clase principal para gestionar la ventana del juego, con distintas 'pantallas' (Menu, Juego, Minijuegos, etc.).
 * Además, contiene el bucle principal (FPS) para actualizar la lógica y renderizar.
 */
public class Pantalla extends JFrame {

    // =========================================================================
    // 1. ATRIBUTOS PRINCIPALES (CardLayout, Movimiento, música, etc.)
    // =========================================================================

    private final CardLayout cardLayout;         // Permite cambiar entre pantallas
    private final JPanel contenedorPrincipal;    // Panel que contiene las distintas pantallas

    // Referencia al panel del juego
    private JLayeredPane capaJuego;

    private final Movimiento movimiento;         // Control principal de movimiento y lógica del personaje

    private int frameCount = 0;                  // Contador de frames para calcular FPS
    private long lastTime = System.nanoTime();   // Ayuda en el cálculo de FPS

    private final GestorMusica gestorMusica;     // Gestor de música de fondo
    private GestorSonidos gestorSonidos;         // Gestor de efectos de sonido

    private Image tejados;                       // Imagen de los tejados del escenario
    private GestorPistas gestorPistas;           // Gestiona pistas (investigación / recolección)

    private PanelVidas panelVidas;

    private Personaje personaje; // Personaje principal

    private boolean partidaTerminada = false; // Bandera para saber si la partida terminó

    private boolean bucleEnEjecucion = true;

    private GestorEnemigos gestorEnemigos; // Añade esta variable

    // Pantalla final del juego antes de regresar al menú.
    private FinalMision finalMision;


    // =========================================================================
    // 2. CONSTRUCTOR Y CONFIGURACIÓN INICIAL
    // =========================================================================

    /**
     * Configura la ventana principal (JFrame):
     * - Añade el Menú.
     * - Crea el Escenario, Colisiones y Movimiento.
     * - Registra pantallas como el Minijuego.
     * - Inicia el bucle principal de actualización (FPS).
     */
    public Pantalla() {

        // ---------------------------------------------------------------------
        // 2.1 Ajustes de la ventana
        // ---------------------------------------------------------------------
        setTitle("Juego - Pantalla Principal");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ---------------------------------------------------------------------
        // 2.2 Inicialización de componentes y contenedor principal (CardLayout)
        // ---------------------------------------------------------------------
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);
        setContentPane(contenedorPrincipal);

        // Creamos de una vez el gestor de pistas (ligado a esta ventana)
        gestorPistas = new GestorPistas(this, gestorEnemigos);

        // ---------------------------------------------------------------------
        // 2.3 Agregar la pantalla del Menú principal
        // ---------------------------------------------------------------------
        contenedorPrincipal.add(new MenuPrincipal(this), "MENU");

        // ---------------------------------------------------------------------
        // 2.4 Configuración de la pantalla de juego (JLayeredPane con varias capas)
        // ---------------------------------------------------------------------
        capaJuego = new JLayeredPane();
        capaJuego.setPreferredSize(new Dimension(1280, 720));

        // Fondo del escenario (mapa)
        EscenarioDistritoSombrio escenario = new EscenarioDistritoSombrio();
        escenario.setBounds(0, 0, 4472, 4816);
        capaJuego.add(escenario, JLayeredPane.DEFAULT_LAYER);

        // Capa de colisiones (invisible, se usa para detectar choques con paredes/obstáculos)
        ColisionesPanel colisiones = new ColisionesPanel();
        colisiones.setBounds(0, 0, 4472, 4816);
        capaJuego.add(colisiones, JLayeredPane.PALETTE_LAYER);

        // Personaje principal
        personaje = new Personaje();
        finalMision = new FinalMision(this);
        // Control de movimiento (manejador de la lógica principal del juego)
        movimiento = new Movimiento(this, escenario, colisiones, personaje, finalMision);
        movimiento.setBounds(0, 0, 1280, 720);
        capaJuego.add(movimiento, JLayeredPane.MODAL_LAYER);

        // Minimapa
        Minimapa minimapa = new Minimapa(personaje, 4472, 4816);
        minimapa.setBounds(getWidth() - 237, getHeight() - 280, 217, 236);
        capaJuego.add(minimapa, JLayeredPane.DRAG_LAYER); // Se coloca por encima de las capas base

        // Crear el panel de vidas
        panelVidas = new PanelVidas(3, "/resources/graficos/Vida2.png"); // Inicia con 3 vidas
        panelVidas.setBounds(0, 0, 200, 100); // Colocarlo en la esquina superior izquierda
        capaJuego.add(panelVidas, JLayeredPane.POPUP_LAYER);

        // Agregar esta "pantalla de juego" al CardLayout
        contenedorPrincipal.add(capaJuego, "JUEGO");

        // ---------------------------------------------------------------------
        // 2.5 Registrar el minijuego de la caja fuerte en el CardLayout
        // ---------------------------------------------------------------------
        contenedorPrincipal.add(new Dial(this), "MINIJUEGO_CAJA_FUERTE");

        // ---------------------------------------------------------------------
        // 2.6 Cargar la imagen de tejados y el gestor de música/sonidos
        // ---------------------------------------------------------------------
        tejados = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/escenarios/tejados_distrito_sombrio.png"))).getImage();




        gestorMusica = new GestorMusica();
        gestorSonidos = new GestorSonidos(); // Inicializamos aquí para evitar null

        // ---------------------------------------------------------------------
        // 2.7 Iniciar el bucle principal del juego
        // ---------------------------------------------------------------------
        iniciarBucle();

        // ---------------------------------------------------------------------
        // 2.8 Hacer visible la ventana
        // ---------------------------------------------------------------------
        setVisible(true);
    }

    private void configurarCursorPersonalizado() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Cargar la imagen del cursor desde los recursos
        Image cursorImagen = toolkit.getImage(getClass().getResource("/graficos/punteroRojo.png"));

        // Crear el cursor personalizado con el punto activo en el centro
        Cursor cursorPersonalizado = toolkit.createCustomCursor(
                cursorImagen,
                new Point(16, 16), // Punto activo
                "CursorCruceta"
        );

        // Establecer el cursor personalizado en el panel del juego
        capaJuego.setCursor(cursorPersonalizado);
    }

    private void restaurarCursorPorDefecto() {
        // Restaura el cursor predeterminado del sistema para toda la ventana
        capaJuego.setCursor(Cursor.getDefaultCursor());
    }



    // =========================================================================
    // 3. CAMBIO ENTRE PANTALLAS
    // =========================================================================

    /**
     * Cambia entre pantallas (por ejemplo, "MENU", "CINEMATICA", "JUEGO", "MINIJUEGO_CAJA_FUERTE", etc.).
     */
    private boolean cinematicaYaMostrada = false; // Indica si ya se ha reproducido la cinemática una vez

    public void cambiarPantalla(String pantalla) {
        if (pantalla.equals("CINEMATICA")) {
            if (gestorMusica != null) {
                if (!cinematicaYaMostrada) {
                    gestorMusica.fadeOutMusica(2000);
                } else {
                    gestorMusica.detenerMusica(); // corte limpio si es la segunda vez
                }
            }

            setEnCinematica(true);

            // Si ya se mostró una vez, usamos la versión sin locución
            if (!cinematicaYaMostrada) {
                contenedorPrincipal.add(new Cinematica(this, true), "CINEMATICA");
                cinematicaYaMostrada = true;
            } else {
                contenedorPrincipal.add(new Cinematica(this, false), "CINEMATICA");
            }
        }

        cardLayout.show(contenedorPrincipal, pantalla);

        if (pantalla.equals("MENU")) {
            gestorMusica.reproducirMusica("/resources/audio/Intro_NoirCity_Find Me Again.wav");
        }

        if (pantalla.equals("JUEGO")) {
            configurarCursorPersonalizado();
            movimiento.setEnMinijuego(false);
            SwingUtilities.invokeLater(movimiento::requestFocusInWindow);
        } else {
            restaurarCursorPorDefecto();
        }
    }


    // =========================================================================
    // 4. BUCLE PRINCIPAL (LOOP DE JUEGO)
    // =========================================================================

    /**
     * Inicia el bucle principal en un hilo separado:
     * - Se repite continuamente: actualizar() + repaint().
     * - Usa una tasa fija de 60 FPS.
     */
    private void iniciarBucle() {
        new Thread(() -> {
            final int fps = 60;
            final long frameTime = 1_000_000_000L / fps; // nanos

            while (bucleEnEjecucion) {
                long startTime = System.nanoTime();

                // Actualiza la lógica del juego
                actualizar();

                // Llama al paint(...) de la ventana
                repaint();

                // Calcula cuánto tardó en este frame
                long elapsedTime = System.nanoTime() - startTime;
                long sleepTime = frameTime - elapsedTime;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1_000_000L); // nanos a ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // =========================================================================
    // 5. ACTUALIZACIÓN DE LÓGICA POR FRAME
    // =========================================================================

    /**
     * Se llama en cada frame:
     *  - Actualiza el movimiento del personaje (si no estamos en cinemática).
     *  - Calcula y actualiza los FPS.
     */
    private void actualizar() {
        // No realiza lógica si la partida ya ha terminado
        if (partidaTerminada) {
            return;
        }

        // Solo actualiza si no estamos en cinemática
        if (!enCinematica) {
            movimiento.moverJugador();
            // Eliminamos enemigos inactivos de forma segura
            GestorEnemigos.getEnemigos().removeIf(enemigo -> {
                enemigo.verificarColision(personaje);
                return !enemigo.isActivo();
            });

            // Actualizar el panel de vidas si la vida del personaje cambia
            panelVidas.actualizarVidas(personaje.getVida());

            // Verificar si las vidas llegaron a 0
            if (personaje.getVida() <= 0) {
                terminarPartida();
            }
        }

        calcularYActualizarFPS();
    }






    private void terminarPartida() {

        // Mostrar mensaje al jugador
        JOptionPane.showMessageDialog(this, "Has perdido todas las vidas. ¡Volverás a intentarlo desde el inicio!", "Vida perdida", JOptionPane.INFORMATION_MESSAGE);
        // Reiniciar las teclas para que el movimiento no continúe al reaparecer
        movimiento.reiniciarTeclas();

        // Reiniciar desplazamiento de la pantalla
        movimiento.reiniciarDesplazamiento(1280, 720);


        // Restablecer la salud y vidas del personaje
        personaje.setVida(4); // Salud inicial

// 🔹 REINICIAR EL ESTADO DE LA CAJA FUERTE PARA QUE NO SE GUARDE AL MORIR
        finalMision.setCajaFuerteCompletada(false);

    }

    // =========================================================================
    // 6. CÁLCULO Y MOSTRADO DE FPS EN LA VENTANA
    // =========================================================================

    /**
     * Calcula los fotogramas por segundo (FPS) y los muestra en el título de la ventana.
     * Se hace cada vez que pasa 1 segundo (1_000_000_000 ns).
     */
    private void calcularYActualizarFPS() {
        frameCount++;
        long currentTime = System.nanoTime();

        if (currentTime - lastTime >= 1_000_000_000L) {
            double fps = frameCount / ((currentTime - lastTime) / 1e9);
            frameCount = 0;
            lastTime = currentTime;

            SwingUtilities.invokeLater(() ->
                    setTitle("NOIR CITY - FPS: " + String.format("%.2f", fps))
            );
        }
    }

    // =========================================================================
    // 7. GETTERS / SETTERS Y UTILIDADES
    // =========================================================================

    /**
     * Indica si la cinemática está en curso (true). Si es true,
     * se pausa la lógica del juego en el metodo actualizar().
     */
    private boolean enCinematica = false;

    /** Activa o desactiva la bandera 'enCinematica'. */
    public void setEnCinematica(boolean valor) {
        this.enCinematica = valor;
    }

    /**
     * @return true si el juego está en cinemática,
     *         false si está en gameplay normal.
     */
    public boolean isEnCinematica() {
        return enCinematica;
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public GestorMusica getGestorMusica() {
        return gestorMusica;
    }

    public Image getTejados() {
        return tejados;
    }

    public GestorSonidos getGestorSonidos() {
        return gestorSonidos;
    }

    public GestorPistas getGestorPistas() {
        return gestorPistas;
    }

    public FinalMision getFinalMision() { return finalMision; }


}
