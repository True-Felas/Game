package juegoprog.controles;

import juegoprog.audio.GestorSonidos;
import juegoprog.cinematica.GestorPistas;
import juegoprog.elementos.GestorBalas;
import juegoprog.elementos.GestorEnemigos;
import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.graficos.Pantalla;
import juegoprog.jugador.Personaje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Esta clase gestiona el movimiento del personaje principal y, a la vez,
 *   coordina la cámara y el desplazamiento del mapa.
 * - Captura eventos de teclado y ratón para mover y rotar al personaje.
 * - Dispara balas y gestiona colisiones.
 * - Interactúa con enemigos y elementos del escenario (pistas, minijuegos, alarmas, tejados, etc.).  */
public class Movimiento extends JPanel implements ActionListener {

    // =========================================================================
    // 1. CONSTANTES Y CONFIGURACIÓN GENERAL
    // =========================================================================

    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;
    private final int VELOCIDAD_CAMINAR = 3;  // Velocidad base

    // =========================================================================
    // 2. ATRIBUTOS DE CONTROL DE MOVIMIENTO
    // =========================================================================

    // Teclas de movimiento
    private boolean up, down, left, right, space;

    // Velocidad dinámica (depende de si está corriendo o no)
    private int velocidad = VELOCIDAD_CAMINAR;

    // Se usa para rotar el personaje según el cursor
    private double anguloRotacion = 0;

    // =========================================================================
    // 3. ATRIBUTOS DE RATÓN Y DESPLAZAMIENTO DE MAPA
    // =========================================================================

    // Posición absoluta del ratón (sumando desplazamientoX / desplazamientoY)
    private final Point posicionRaton = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

    // Controla el offset del escenario (cámara)
    private int desplazamientoX;
    private int desplazamientoY;

    // =========================================================================
    // 4. CONTROL DE ELEMENTOS GRÁFICOS (TEJADOS, ETC.)
    // =========================================================================

    // Por defecto, los tejados se muestran
    private boolean mostrarTejados = true;

    // =========================================================================
    // 5. REFERENCIAS A OTRAS CLASES (ESCENARIO, COLISIONES, JUGADOR, ETC.)
    // =========================================================================

    private final EscenarioDistritoSombrio escenario;
    private final ColisionesPanel colisiones;
    private final Personaje personaje;
    private final Pantalla ventana;

    // =========================================================================
    // 6. GESTOR DE BALAS, ENEMIGOS, SONIDOS Y PISTAS
    // =========================================================================

    private final GestorBalas gestorBalas = new GestorBalas();
    private final GestorEnemigos gestorEnemigos;
    private final GestorSonidos gestorSonidos = new GestorSonidos();
    private final GestorPistas gestorPistas;

    // =========================================================================
    // 7. CONTROL DE MINIJUEGOS, PISTAS Y MENSAJES
    // =========================================================================

    // Indica si el personaje está dentro de un minijuego
    public boolean enMinijuego = false;

    // Mensaje "Pulsa ENTER para acceder al minijuego"
    private boolean mostrarMensajeMinijuego = false;

    // Mensaje "Pulsa ENTER para inspeccionar (pista)"
    private boolean mostrarMensajePista = false;

    // Para ejecutar acciones puntuales cuando se pulsa ENTER
    private Runnable eventoEnter;

    // =========================================================================
    // 8. CONTROL DE ESTADOS (caminar, correr, alarma, etc.)
    // =========================================================================

    private boolean estaCaminando = false;
    private boolean estaCorriendo = false;
    private boolean alarmaActivada = false;

    // =========================================================================
    // 9. CONSTRUCTOR
    // =========================================================================

    /**
     * @param ventana    Referencia a la ventana principal.
     * @param escenario  Escenario actual (mapa).
     * @param colisiones Panel que maneja las colisiones.
     * @param personaje  Personaje controlado por el jugador.
     */
    public Movimiento(Pantalla ventana, EscenarioDistritoSombrio escenario,
                      ColisionesPanel colisiones, Personaje personaje) {

        this.ventana = ventana;
        this.escenario = escenario;
        this.colisiones = colisiones;
        this.personaje = personaje;
        this.gestorPistas = ventana.getGestorPistas();
        this.gestorEnemigos = new GestorEnemigos(gestorSonidos);

        setOpaque(false);
        setFocusable(true);

        // Posición inicial de la cámara
        this.desplazamientoX = 640;
        this.desplazamientoY = 360;

        // Ajustamos el escenario y colisiones al desplazamiento inicial
        escenario.actualizarDesplazamiento(desplazamientoX, desplazamientoY);
        colisiones.actualizarOffset(desplazamientoX, desplazamientoY);

        // Se configuran los listeners de teclado y ratón
        configurarEventos();

        // ─────────────────────────────────────────────────────────────────
        // NUEVO: Asignar la ventana para comprobar la cinemática en GestorEnemigos
        // ─────────────────────────────────────────────────────────────────
        this.gestorEnemigos.setPantalla(ventana);
    }

    // =========================================================================
    // 10. CONFIGURACIÓN DE EVENTOS
    // =========================================================================

    /** Configura los KeyListeners y MouseListeners para controlar movimiento,
     *  disparos y rotación del personaje. */
    private void configurarEventos() {
        // Evento de teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                toggleMovement(e.getKeyCode(), true);

                // Si el jugador está en la zona y pulsa ENTER, entra al minijuego
                if (e.getKeyCode() == KeyEvent.VK_ENTER && mostrarMensajeMinijuego) {
                    System.out.println("📍 Accediendo al minijuego...");
                    enMinijuego = true;
                    mostrarMensajeMinijuego = false;
                    ventana.cambiarPantalla("MINIJUEGO_CAJA_FUERTE");
                }

                // Si hay un evento ENTER asignado, se ejecuta (p. ej. mostrar pista)
                if (e.getKeyCode() == KeyEvent.VK_ENTER && eventoEnter != null) {
                    eventoEnter.run();
                    eventoEnter = null; // Evita repeticiones en bucle
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                toggleMovement(e.getKeyCode(), false);
            }
        });

        // Evento de movimiento de ratón
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                posicionRaton.setLocation(e.getX() + desplazamientoX, e.getY() + desplazamientoY);
                calcularAnguloRotacion();
            }
        });

        // Evento de clic de ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dispararBala();
                }
            }
        });
    }

    // =========================================================================
    // 11. LÓGICA DE MOVIMIENTO Y DISPARO
    // =========================================================================

    /** Cambia el estado de las variables de movimiento (arriba, abajo, izquierda, derecha, correr). */
    private void toggleMovement(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W -> up = pressed;
            case KeyEvent.VK_S -> down = pressed;
            case KeyEvent.VK_A -> left = pressed;
            case KeyEvent.VK_D -> right = pressed;
            case KeyEvent.VK_SPACE -> {
                space = pressed;
                ajustarVelocidad(); // Ajusta velocidad según si está corriendo
            }
        }
    }

    /** Define si el jugador camina o corre, modificando la velocidad y el estado del personaje. */
    private void ajustarVelocidad() {
        if (space) {
            // Velocidad al presionar SPACE
            velocidad = 5;
            personaje.setCorrer(true);
        } else {
            velocidad = VELOCIDAD_CAMINAR;
            personaje.setCorrer(false);
        }
    }

    /** Dispara una bala desde la posición del personaje hacia la posición del ratón. */
    private void dispararBala() {
        gestorSonidos.reproducirEfecto("/audio/NoirShotC.wav");

        double xInicial = personaje.getX();
        double yInicial = personaje.getY();

        double objetivoX = posicionRaton.x;
        double objetivoY = posicionRaton.y;

        gestorBalas.disparar(xInicial, yInicial, objetivoX, objetivoY);
    }

    // =========================================================================
    // 12. BUCLE PRINCIPAL DE MOVIMIENTO (SE LLAMA REGULARMENTE)
    // =========================================================================

    /**
     * Se encarga de:
     *  - Verificar colisiones y mover al jugador en consecuencia
     *  - Administrar sonidos de pasos/correr
     *  - Verificar zonas especiales (alarmas, minijuego, pistas, etc.)
     *  - Actualizar enemigos y balas
     */
    public void moverJugador() {

        // ─────────────────────────────────────────────────────────────────────
        // 🔹 BLOQUEA el movimiento si estamos en cinemática
        // ─────────────────────────────────────────────────────────────────────
        if (ventana.isEnCinematica()) {
            System.out.println("[DEBUG] Se ha detectado cinemática: no actualizo movimiento ni enemigos.");
            return;
        }

        // 1. Verificar colisiones
        boolean[] colisionesDirecciones = verificarColisiones();

        // 2. Calcular movimiento
        double[] movimiento = calcularMovimiento(colisionesDirecciones);

        // 3. Aplicar movimiento al escenario
        aplicarMovimiento(movimiento);

        // 4. Actualizar posición real del personaje
        int personajeRealX = desplazamientoX + SCREEN_WIDTH / 2;
        int personajeRealY = desplazamientoY + SCREEN_HEIGHT / 2;
        personaje.setPosicion(personajeRealX, personajeRealY);

        System.out.println("Coordenadas del jugador: X=" + personajeRealX + ", Y=" + personajeRealY);


        // 5. Verificar pistas en la posición actual
        gestorPistas.verificarPistas(personajeRealX, personajeRealY);

        // 6. Ocultar/mostrar tejados dependiendo de si el jugador está dentro de alguna casa
        gestionarTejados(personajeRealX, personajeRealY);

        // 7. Comprobar zona de minijuego (caja fuerte)
        gestionarMinijuegoCajaFuerte(personajeRealX, personajeRealY);

        // 8. Comprobar zona de alarma y reproducirla una sola vez
        gestionarAlarma(personajeRealX, personajeRealY);

        // 9. Administrar sonidos de pasos/carrera
        gestionarSonidosPasos(movimiento);

        // 10. Actualizar enemigos y balas
        gestorEnemigos.actualizar(personaje.getX(), personaje.getY(), colisiones, desplazamientoX, desplazamientoY);
        gestorEnemigos.verificarColisiones(gestorBalas);

        if (gestorEnemigos.enemigosEliminados()) {
            // Generar nueva oleada (ejemplo)
            gestorEnemigos.actualizar(personaje.getX(), personaje.getY(), colisiones,
                    desplazamientoX, desplazamientoY);
        }

        gestorBalas.actualizar(colisiones, desplazamientoX, desplazamientoY);
    }

    // =========================================================================
    // 12.1. GESTIÓN DE TEJADOS, MINIJUEGO Y ALARMAS
    // =========================================================================

    /** Si el jugador está dentro de ciertas casas, se ocultan los tejados. */
    private void gestionarTejados(int x, int y) {
        // Definir límites de las casas
        Rectangle casa1 = new Rectangle(1787, 1865, 463, 756);
        Rectangle casa2 = new Rectangle(2567, 2785, 516, 1084);

        mostrarTejados = !casa1.contains(x, y) && !casa2.contains(x, y);
    }

    /** Si el jugador está cerca de la caja fuerte, se muestra un mensaje para entrar al minijuego. */
    private void gestionarMinijuegoCajaFuerte(int x, int y) {
        if (x >= 2713 && x <= 2715 && y >= 3809 && y <= 3857) {
            if (!mostrarMensajeMinijuego) {
                System.out.println("📍 Pulsa ENTER para acceder al minijuego");
                mostrarMensajeMinijuego = true;
            }
        } else {
            mostrarMensajeMinijuego = false;
        }
    }

    /** Zona donde suena una alarma cuando el jugador entra, pero solo se activa una vez
     *  hasta que sale de la zona. */
    private void gestionarAlarma(int x, int y) {
        Rectangle zonaAlarma = new Rectangle(2499, 1854, 1301, 2588);
        if (zonaAlarma.contains(x, y)) {
            if (!alarmaActivada) {
                System.out.println("🚨 Alarma activada: ¡Intruso detectado!");
                gestorSonidos.reproducirEfecto("/audio/NoirAreaAlarm.wav");
                alarmaActivada = true;
            }
        } else {
            alarmaActivada = false;
        }
    }

    // =========================================================================
    // 12.2. GESTIÓN DE SONIDOS (PASOS Y CARRERA)
    // =========================================================================

    /** Activa o desactiva los sonidos de pasos y carrera según el movimiento del personaje. */
    private void gestionarSonidosPasos(double[] movimiento) {
        if (movimiento[0] != 0 || movimiento[1] != 0) {
            // Movimiento en x o y distinto de 0 => se está desplazando
            if (space) { // Correr
                if (!estaCorriendo) {
                    gestorSonidos.detenerSonido("/audio/NoirRun.wav");
                    gestorSonidos.detenerSonido("/audio/NoirStep3b.wav"); // Aseguramos detener el otro
                    gestorSonidos.reproducirBucle("/audio/NoirRun.wav");
                    estaCorriendo = true;
                    estaCaminando = false;
                }
            } else { // Caminar
                if (!estaCaminando) {
                    gestorSonidos.detenerSonido("/audio/NoirRun.wav");
                    gestorSonidos.detenerSonido("/audio/NoirStep3b.wav");
                    gestorSonidos.reproducirBucle("/audio/NoirStep3b.wav");
                    estaCaminando = true;
                    estaCorriendo = false;
                }
            }
        } else {
            // Se detuvo
            if (estaCaminando || estaCorriendo) {
                gestorSonidos.detenerSonido("/audio/NoirStep3b.wav");
                gestorSonidos.detenerSonido("/audio/NoirRun.wav");
                estaCaminando = false;
                estaCorriendo = false;
            }
        }
    }

    // =========================================================================
    // 13. LÓGICA DE COLISIONES
    // =========================================================================

    /**
     * Verifica si hay colisiones en las cuatro direcciones principales.
     * Devuelve un array [arriba, abajo, izquierda, derecha].
     */
    private boolean[] verificarColisiones() {
        int hitbox = 10;

        // Coordenadas globales
        int globalX = personaje.getX() - desplazamientoX;
        int globalY = personaje.getY() - desplazamientoY;

        // Chequeo de colisiones en cuatro direcciones
        boolean colisionArriba = colisiones.hayColision(globalX, globalY - hitbox - velocidad);
        boolean colisionAbajo = colisiones.hayColision(globalX, globalY + hitbox + velocidad);
        boolean colisionIzquierda = colisiones.hayColision(globalX - hitbox - velocidad, globalY);
        boolean colisionDerecha = colisiones.hayColision(globalX + hitbox + velocidad, globalY);

        return new boolean[]{colisionArriba, colisionAbajo, colisionIzquierda, colisionDerecha};
    }

    /** Calcula cuánto se mueve el personaje en X e Y, según teclas, colisiones y velocidad. */
    private double[] calcularMovimiento(boolean[] colisionesDirecciones) {
        double moveX = 0, moveY = 0;

        if (up && !colisionesDirecciones[0]) moveY -= velocidad;
        if (down && !colisionesDirecciones[1]) moveY += velocidad;
        if (left && !colisionesDirecciones[2]) moveX -= velocidad;
        if (right && !colisionesDirecciones[3]) moveX += velocidad;

        // Normalizar en diagonal (para no ir más rápido en diagonales)
        double length = Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 0) {
            moveX = (moveX / length) * velocidad;
            moveY = (moveY / length) * velocidad;
        }

        return new double[]{moveX, moveY};
    }

    /** Aplica el movimiento calculado al desplazamiento (cámara) y respeta los límites del mapa. */
    private void aplicarMovimiento(double[] movimiento) {
        int nuevoX = desplazamientoX + (int) movimiento[0];
        int nuevoY = desplazamientoY + (int) movimiento[1];

        // Limitar el movimiento dentro de los bordes del escenario
        nuevoX = Math.max(0, Math.min(nuevoX, escenario.getAncho() - SCREEN_WIDTH));
        nuevoY = Math.max(0, Math.min(nuevoY, escenario.getAlto() - SCREEN_HEIGHT));

        // Actualizar solo si cambió
        if (nuevoX != desplazamientoX || nuevoY != desplazamientoY) {
            desplazamientoX = nuevoX;
            desplazamientoY = nuevoY;
            escenario.actualizarDesplazamiento(desplazamientoX, desplazamientoY);
            colisiones.actualizarOffset(desplazamientoX, desplazamientoY);
        }
    }

    // =========================================================================
    // 14. ROTACIÓN DEL PERSONAJE HACIA EL RATÓN
    // =========================================================================

    /**
     * Calcula el ángulo de rotación basado en la posición del ratón
     * vs el centro de la pantalla.
     */
    private void calcularAnguloRotacion() {
        anguloRotacion = Math.atan2(
                (posicionRaton.y - desplazamientoY) - (double) SCREEN_HEIGHT / 2,
                (posicionRaton.x - desplazamientoX) - (double) SCREEN_WIDTH / 2
        );
    }

    // =========================================================================
    // 15. PINTADO DEL PERSONAJE Y DEMÁS ELEMENTOS EN PANTALLA
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Dibujar tejados si están activos
        if (mostrarTejados) {
            g.drawImage(ventana.getTejados(), -desplazamientoX, -desplazamientoY, null);
        }

        // 2. Dibujar mensajes de minijuego y pistas
        dibujarMensajes(g);

        // 3. Dibujar personaje con rotación
        dibujarPersonaje(g2d);

        // 4. Dibujar balas y enemigos
        gestorBalas.dibujar(g, desplazamientoX, desplazamientoY);
        gestorEnemigos.dibujar(g, desplazamientoX, desplazamientoY);
    }

    /** Muestra los textos en pantalla de “Pulsa ENTER...” para minijuego y pistas. */
    private void dibujarMensajes(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        if (mostrarMensajeMinijuego) {
            g.drawString("Pulsa ENTER para abrir la caja fuerte", SCREEN_WIDTH / 2 - 150, 50);
        }
        if (mostrarMensajePista) {
            g.drawString("Pulsa ENTER para inspeccionar", SCREEN_WIDTH / 2 - 150, 80);
        }
    }

    /** Renderiza la imagen del personaje rotada hacia la posición del ratón. */
    private void dibujarPersonaje(Graphics2D g2d) {
        Image imagenPersonaje = personaje.getImagen();
        if (imagenPersonaje == null) {
            return;
        }

        // Trasladar el Graphics2D al centro de la pantalla
        g2d.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

        // Rotar el personaje
        g2d.rotate(anguloRotacion);

        // Dibujar imagen en el centro
        int anchoImagen = imagenPersonaje.getWidth(this);
        int altoImagen = imagenPersonaje.getHeight(this);
        if (anchoImagen > 0 && altoImagen > 0) {
            g2d.drawImage(imagenPersonaje, -anchoImagen / 2, -altoImagen / 2, this);
        }

        // Deshacer la rotación y la traslación
        g2d.rotate(-anguloRotacion);
        g2d.translate(-SCREEN_WIDTH / 2, -SCREEN_HEIGHT / 2);
    }

    // =========================================================================
    // 16. GETTERS / SETTERS Y OTROS MÉTODOS PÚBLICOS
    // =========================================================================

    /** Define si el jugador está o no dentro de un minijuego. */
    public void setEnMinijuego(boolean estado) {
        this.enMinijuego = estado;
    }

    /** Para permitir que otras clases activen o desactiven el mensaje de pista. */
    public void setMostrarMensajePista(boolean mostrar) {
        this.mostrarMensajePista = mostrar;
    }

    /** Agrega una acción que se ejecutará cuando se presione ENTER. */
    public void agregarEventoEnter(Runnable accion) {
        this.eventoEnter = accion;
    }

    // =========================================================================
    // 17. METODO DE LA INTERFAZ ActionListener (SI SE NECESITA)
    // =========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        // Actualmente vacío
    }

}
