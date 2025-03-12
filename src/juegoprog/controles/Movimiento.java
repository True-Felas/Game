package juegoprog.controles;

import juegoprog.elementos.GestorBalas;
import juegoprog.elementos.GestorEnemigos;
import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.jugador.Personaje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Gestiona el movimiento del personaje y la cámara.
 * Maneja eventos de teclado y ratón para controlar desplazamiento, rotación y disparos de balas.
 * Basado en los apuntes de Soraya "Eventos y Escuchadores".
 */
public class Movimiento extends JPanel implements ActionListener {

    //---------------------------------------------------
    //  🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------

    /** CONSTANTES Y CONFIGURACIÓN */
    private final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720; // 🔹 Tamaño de la pantalla
    private int velocidad = 3; // 🔹 Velocidad de movimiento (puede variar)

    /** CONTROL DE MOVIMIENTO */
    private boolean up, down, left, right, space; // 🔹 Control de teclas presionadas
    private double anguloRotacion = 0; // 🔹 Ángulo de rotación basado en el puntero

    /** CONTROL DEL RATÓN Y DESPLAZAMIENTO DEL MAPA */
    private final Point posicionRaton = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2); // 🔹 Posición del puntero
    private int desplazamientoX = 0, desplazamientoY = 0; // 🔹 Desplazamiento del escenario

    /** REFERENCIAS AL ESCENARIO Y COLISIONES */
    private final EscenarioDistritoSombrio escenario; // 🔹 Referencia al escenario
    private final ColisionesPanel colisiones; // 🔹 Referencia al panel de colisiones
    private final Personaje personaje; // 🔹 Personaje que se moverá en pantalla

    /** GESTIÓN DE BALAS */
    private final GestorBalas gestorBalas = new GestorBalas(); // 🔹 Clase auxiliar para manejo de balas

    /** GESTIÓN DE ENEMIGOS */
    private final GestorEnemigos gestorEnemigos = new GestorEnemigos(); // 🔹 Clase para manejar enemigos

    //---------------------------------------------------
    //  🔹 CONSTRUCTOR Y CONFIGURACIÓN DE EVENTOS
    //---------------------------------------------------

    /**
     * Inicializa el movimiento, captura eventos de teclado y ratón,
     * y sincroniza la cámara con el escenario, colisiones y disparo de balas.
     */
    public Movimiento(EscenarioDistritoSombrio escenario, ColisionesPanel colisiones, Personaje personaje) {
        this.escenario = escenario;
        this.colisiones = colisiones;
        this.personaje = personaje;
        setOpaque(false);
        setFocusable(true);

        // 🔹 Establecer desplazamiento inicial
        this.desplazamientoX = 640;
        this.desplazamientoY = 360;

        // 🔹 Asegurar que el escenario y colisiones empiecen en la posición correcta
        escenario.actualizarDesplazamiento(desplazamientoX, desplazamientoY);
        colisiones.actualizarOffset(desplazamientoX, desplazamientoY);

        configurarEventos();
    }

    /** Configura los eventos de teclado y ratón. */
    private void configurarEventos() {
        // Captura de teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                toggleMovement(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                toggleMovement(e.getKeyCode(), false);
            }
        });

        // Captura de movimiento del ratón
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                posicionRaton.setLocation(e.getX() + desplazamientoX, e.getY() + desplazamientoY);
                calcularAnguloRotacion();
            }
        });

        // Captura de clics del ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dispararBala(); // 🔹 Dispara una bala con el clic izquierdo
                }
            }
        });
    }

    //---------------------------------------------------
    //  🔹 LÓGICA DE MOVIMIENTO Y DISPARO
    //---------------------------------------------------

    /** Calcula el ángulo de rotación del personaje basado en la posición del ratón. */
    private void calcularAnguloRotacion() {
        anguloRotacion = Math.atan2(
                (posicionRaton.y - desplazamientoY) - (double) SCREEN_HEIGHT / 2,
                (posicionRaton.x - desplazamientoX) - (double) SCREEN_WIDTH / 2
        );
    }

    /** Activa o desactiva el movimiento según la tecla presionada. */
    private void toggleMovement(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W -> up = pressed;
            case KeyEvent.VK_S -> down = pressed;
            case KeyEvent.VK_A -> left = pressed;
            case KeyEvent.VK_D -> right = pressed;
            case KeyEvent.VK_SPACE -> {
                space = pressed; // Detecta si el espacio está presionado
                ajustarVelocidad(); // Cambia la velocidad según el espacio
            }
        }
    }

    /** Ajusta la velocidad según si se está presionando la tecla espacio o no. */
    private void ajustarVelocidad() {
        if (space) {
            velocidad = 5; // Aumenta la velocidad cuando "ESPACIO" está presionado
        } else {
            velocidad = 3; // Vuelve a la velocidad normal
        }
    }

    /**
     * Dispara una nueva bala hacia la posición del ratón.
     * La bala se inicia en el centro de la pantalla y se mueve hacia
     * la posición del ratón relativa a la ventana.
     */
    private void dispararBala() {
        // Coordenadas iniciales: el centro de la pantalla
        double xInicial = personaje.getX();
        double yInicial = personaje.getY();

        // Coordenadas objetiv: posición actual del ratón (relativa al desplazamiento del mapa)
        double objetivoX = posicionRaton.x;
        double objetivoY = posicionRaton.y;


        // Llamar al gestor de balas para disparar
        gestorBalas.disparar(xInicial, yInicial, objetivoX, objetivoY);
        System.out.println(xInicial +", "+ yInicial);
    }




    //---------------------------------------------------
    //  🔹 LÓGICA PRINCIPAL DE MOVIMIENTO
    //---------------------------------------------------

    /**
     * Sincroniza el desplazamiento del mapa con las colisiones y gestiona las balas.
     */
    public void moverJugador() {
        // La posición "central" del jugador en la pantalla.
        int personajeX = desplazamientoX + SCREEN_WIDTH / 2;
        int personajeY = desplazamientoY + SCREEN_HEIGHT / 2;

        // Verificar colisiones en las cuatro direcciones
        boolean[] colisionesDirecciones = verificarColisiones(personajeX, personajeY);

        // Calcular movimiento basado en las teclas y las colisiones
        double[] movimiento = calcularMovimiento(colisionesDirecciones);

        // Aplicar el movimiento calculado al mapa
        aplicarMovimiento(movimiento);

        // Actualizar las coordenadas reales del personaje
        int personajeRealX = desplazamientoX + SCREEN_WIDTH / 2;
        int personajeRealY = desplazamientoY + SCREEN_HEIGHT / 2;

        // Sincronizar las coordenadas reales con el objeto `Personaje`
        personaje.setPosicion(personajeRealX, personajeRealY);

        // 🔹 Actualizar enemigos: movimiento hacia el personaje y colisiones con balas
        gestorEnemigos.actualizar(personaje.getX(), personaje.getY(), colisiones, desplazamientoX, desplazamientoY);
        gestorEnemigos.verificarColisiones(gestorBalas);

        // 🔹 Añadir lógica de oleadas de enemigos
        if (gestorEnemigos.enemigosEliminados()) {
            // Determinar las dimensiones del escenario
            int anchoEscenario = escenario.getAncho(); // Métodos para obtener dimensiones del escenario
            int altoEscenario = escenario.getAlto();

            // Posición del jugador
            double posJugadorX = personaje.getX();
            double posJugadorY = personaje.getY();

            // Generar nueva oleada
            gestorEnemigos.generarOleada(anchoEscenario, altoEscenario);
        }


        // 🔹 Actualizar las balas activas
        gestorBalas.actualizar(colisiones, desplazamientoX, desplazamientoY);
    }

    /** Verifica las colisiones y retorna un array con los resultados [arriba, abajo, izquierda, derecha]. */
    private boolean[] verificarColisiones(int personajeX, int personajeY) {
        int hitbox = 10;

        // Ajustamos las coordenadas globales basadas en el desplazamiento del mapa
        int globalX = personaje.getX() - desplazamientoX;
        int globalY = personaje.getY() - desplazamientoY;

        // Verificamos las colisiones en las cuatro direcciones usando estas coordenadas globales
        boolean colisionArriba = colisiones.hayColision(globalX, globalY - hitbox - velocidad);
        boolean colisionAbajo = colisiones.hayColision(globalX, globalY + hitbox + velocidad);
        boolean colisionIzquierda = colisiones.hayColision(globalX - hitbox - velocidad, globalY);
        boolean colisionDerecha = colisiones.hayColision(globalX + hitbox + velocidad, globalY);

        return new boolean[]{colisionArriba, colisionAbajo, colisionIzquierda, colisionDerecha};
    }


    /** Calcula el movimiento del personaje basado en las teclas presionadas y las colisiones detectadas. */
    private double[] calcularMovimiento(boolean[] colisionesDirecciones) {
        double moveX = 0, moveY = 0;

        if (up && !colisionesDirecciones[0]) moveY -= velocidad;
        if (down && !colisionesDirecciones[1]) moveY += velocidad;
        if (left && !colisionesDirecciones[2]) moveX -= velocidad;
        if (right && !colisionesDirecciones[3]) moveX += velocidad;

        // Normaliza el movimiento diagonal para que no sea más rápido
        double length = Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 0) {
            moveX = (moveX / length) * velocidad;
            moveY = (moveY / length) * velocidad;
        }

        return new double[]{moveX, moveY};
    }

    /** Aplica el movimiento calculado al desplazamiento del mapa y actualiza los límites del escenario. */
    private void aplicarMovimiento(double[] movimiento) {
        int nuevoX = desplazamientoX + (int) movimiento[0];
        int nuevoY = desplazamientoY + (int) movimiento[1];

        // Limitar el movimiento dentro de los límites del mapa
        nuevoX = Math.max(0, Math.min(nuevoX, escenario.getAncho() - SCREEN_WIDTH));
        nuevoY = Math.max(0, Math.min(nuevoY, escenario.getAlto() - SCREEN_HEIGHT));

        if (nuevoX != desplazamientoX || nuevoY != desplazamientoY) {
            desplazamientoX = nuevoX;
            desplazamientoY = nuevoY;
            escenario.actualizarDesplazamiento(desplazamientoX, desplazamientoY);
            colisiones.actualizarOffset(desplazamientoX, desplazamientoY);
        }
    }

    //---------------------------------------------------
    //  🔹 DIBUJADO DEL PERSONAJE Y LAS BALAS
    //---------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Obtener la imagen del personaje
        Image imagenPersonaje = personaje.getImagen();

        // Dibujar la imagen del personaje en el centro de la pantalla con rotación
        g2d.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g2d.rotate(anguloRotacion);

        int anchoImagen = imagenPersonaje.getWidth(this);
        int altoImagen = imagenPersonaje.getHeight(this);
        if (anchoImagen > 0 && altoImagen > 0) {
            g2d.drawImage(imagenPersonaje, -anchoImagen / 2, -altoImagen / 2, this);
        }

        g2d.rotate(-anguloRotacion);
        g2d.translate(-SCREEN_WIDTH / 2, -SCREEN_HEIGHT / 2);

        // 🔹 Dibujar las balas
        gestorBalas.dibujar(g, desplazamientoX, desplazamientoY);

        // 🔹 Dibujar enemigos
        gestorEnemigos.dibujar(g, desplazamientoX, desplazamientoY);


    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}