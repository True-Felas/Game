package juegoprog.controles;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.jugador.Personaje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

    /** Gestiona el movimiento del personaje y la cámara.
    *   Maneja eventos de teclado y ratón para controlar desplazamiento y rotación.
    *   Basado en los apuntes de Soraya "Eventos y Escuchadores". */

public class Movimiento extends JPanel implements ActionListener {

    //---------------------------------------------------
    //  🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------

        /** CONSTANTES Y CONFIGURACIÓN */

        private final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720; // 🔹 Tamaño de la pantalla
        private int velocidad = 4; // 🔹 Velocidad de movimiento

        /** CONTROL DE MOVIMIENTO */

        private boolean up, down, left, right; // 🔹 Control de teclas presionadas
        private double anguloRotacion  = 0; // 🔹 Ángulo de rotación basado en el puntero

        /** CONTROL DEL RATÓN Y DESPLAZAMIENTO DEL MAPA */

        private final Point posicionRaton = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2); // 🔹 Posición del puntero
        private int desplazamientoX = 0, desplazamientoY = 0; // 🔹 Desplazamiento del escenario

        /** REFERENCIAS AL ESCENARIO Y COLISIONES */

        private final EscenarioDistritoSombrio escenario; // 🔹 Referencia al escenario
        private final ColisionesPanel colisiones; // 🔹 Referencia al panel de colisiones

        // Agrega esto a tu clase Movimiento:
        private final Personaje personaje;

        //--------------------------------------------------------
    //  🔹 CONSTRUCTOR DE MOVIMIENTO + EVENTOS RATÓN Y TECLADO
    //--------------------------------------------------------

        /** Inicializa el movimiento, captura eventos de teclado y ratón,
         *  y sincroniza la cámara con el escenario y colisiones. */

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
            iniciarTemporizador();
        }


        /** Registra los eventos de teclado y ratón. */

        private void configurarEventos() {

            // 🔹 Captura de teclado
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) { toggleMovement(e.getKeyCode(), true); }
                @Override public void keyReleased(KeyEvent e) { toggleMovement(e.getKeyCode(), false); }
            });

            // 🔹 Captura de movimiento del ratón
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    posicionRaton.setLocation(e.getX() + desplazamientoX, e.getY() + desplazamientoY);
                    calcularAnguloRotacion();
                }
            });
        }

        /** Inicia el temporizador para actualizar el movimiento. */

        private void iniciarTemporizador() {
            Timer timer = new Timer(12, this);
            timer.setInitialDelay(12);  // 🔹 Asegura que no haya una aceleración inicial
            timer.start();
        }

    //---------------------------------------------------
    //  🔹 MÉTODOS DE MOVIMIENTO Y CONTROL
    //---------------------------------------------------

        /** Calcula el ángulo de rotación del personaje basado en la posición del ratón.
         * Ajusta la orientación cuando el mapa se desplaza. */

        private void calcularAnguloRotacion() {
            anguloRotacion = Math.atan2(
                    (posicionRaton.y - desplazamientoY) - (double) SCREEN_HEIGHT / 2,
                    (posicionRaton.x - desplazamientoX) - (double) SCREEN_WIDTH / 2
            );
        }

        /** Ajusta la posición del ratón en relación con el mapa en cada frame.
         *  Evita que el personaje pierda la referencia del puntero al desplazarse. */

        private void actualizarPosicionRaton() {
            PointerInfo pInfo = MouseInfo.getPointerInfo();
            if (pInfo != null) {
                Point pos = pInfo.getLocation();
                SwingUtilities.convertPointFromScreen(pos, this);
                posicionRaton.setLocation(pos.x + desplazamientoX, pos.y + desplazamientoY);
            }
        }

        /** Activa o desactiva el movimiento según la tecla presionada.
         *  W = arriba, S = abajo, A = izquierda, D = derecha. */

        private void toggleMovement(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W -> up = pressed;
            case KeyEvent.VK_S -> down = pressed;
            case KeyEvent.VK_A -> left = pressed;
            case KeyEvent.VK_D -> right = pressed;
        }
    }

    /** IMPORTANTE: Metodo obligatodio de ActionListener.
     *  Llama a 'moverJugador()' y repinta la pantalla para actualizar el movimiento.
     *  Se ejecuta automáticamente por el temporizador cada 16 ms. */

    @Override
    public void actionPerformed(ActionEvent e) {
        moverJugador();
        repaint();
    }

    //---------------------------------------------------
    //  🔹 LÓGICA DE MOVIMIENTO Y COLISIONES
    //---------------------------------------------------


        /**
         * Método principal que gestiona el movimiento del jugador.
         * Su tarea es coordinar toda la lógica:
         * 1. Verificar colisiones antes de moverse.
         * 2. Calcular el vector de movimiento del jugador.
         * 3. Aplicar el movimiento calculado al mapa.
         * 4. Redibujar la pantalla para reflejar los cambios.
         */
        private void moverJugador() {
            // La posición "central" del jugador en la pantalla.
            int personajeX = SCREEN_WIDTH / 2;
            int personajeY = SCREEN_HEIGHT / 2;

            // Verificar las colisiones en las cuatro direcciones:
            boolean[] colisionesDirecciones = verificarColisiones(personajeX, personajeY);

            // Calcular el movimiento basado en las teclas y las colisiones detectadas:
            double[] movimiento = calcularMovimiento(colisionesDirecciones);

            // Aplicar dicho movimiento al desplazamiento del escenario:
            aplicarMovimiento(movimiento);

            // Actualizar las coordenadas reales del personaje
            int personajeRealX = desplazamientoX + SCREEN_WIDTH / 2;
            int personajeRealY = desplazamientoY + SCREEN_HEIGHT / 2;

            // Sincronizar las coordenadas reales con el objeto `Personaje`
            personaje.setPosicion(personajeRealX, personajeRealY);

            // Redibujar la pantalla con los nuevos valores.
            repaint();
        }


        /** Verifica las colisiones en las cuatro direcciones y retorna un array con los resultados. */
        private boolean[] verificarColisiones(int personajeX, int personajeY) {
            int hitboxSize = 10;

            boolean colisionArriba = colisiones.hayColision(personajeX, personajeY - hitboxSize - velocidad);
            boolean colisionAbajo = colisiones.hayColision(personajeX, personajeY + hitboxSize + velocidad);
            boolean colisionIzquierda = colisiones.hayColision(personajeX - hitboxSize - velocidad, personajeY);
            boolean colisionDerecha = colisiones.hayColision(personajeX + hitboxSize + velocidad, personajeY);

            return new boolean[]{colisionArriba, colisionAbajo, colisionIzquierda, colisionDerecha};
        }

        /**
         * Calcula el desplazamiento en los ejes `X` e `Y` basado
         * en las teclas presionadas y teniendo en cuenta las
         * colisiones detectadas en las direcciones respectivas.
         *
         * Si hay colisiones en cualquier dirección, el personaje
         * no podrá moverse en esa dirección.
         *
         * Además, si el personaje intenta moverse diagonalmente,
         * este método normaliza el movimiento para que no sea más
         * rápido al combinar dos direcciones (evita el "boost diagonal").
         *
         * @param colisionesDirecciones Un array booleano con colisiones en:
         *                              - [0] Arriba
         *                              - [1] Abajo
         *                              - [2] Izquierda
         *                              - [3] Derecha
         * @return Un array `double[2]` donde:
         *         - [0] es el movimiento horizontal (`X`)
         *         - [1] es el movimiento vertical (`Y`)
         */
        private double[] calcularMovimiento(boolean[] colisionesDirecciones) {
            double moveX = 0, moveY = 0; // Inicializamos la dirección de movimiento.

            // Comprobar si podemos movernos en cada dirección (¡sin colisiones!):
            if (up && !colisionesDirecciones[0]) moveY -= velocidad; // Movimiento hacia arriba resta al eje Y.
            if (down && !colisionesDirecciones[1]) moveY += velocidad; // Movimiento hacia abajo suma al eje Y.
            if (left && !colisionesDirecciones[2]) moveX -= velocidad; // Movimiento hacia la izquierda resta al eje X.
            if (right && !colisionesDirecciones[3]) moveX += velocidad; // Movimiento hacia la derecha suma al eje X.

            // Si el personaje intenta moverse en diagonal (por ejemplo, W+A), ajustamos el movimiento.
            double length = Math.sqrt(moveX * moveX + moveY * moveY); // Magnitud del vector de movimiento.
            if (length > 0) {
                // Normalizamos el movimiento diagonal y lo ajustamos a la velocidad deseada.
                moveX = (moveX / length) * velocidad;
                moveY = (moveY / length) * velocidad;
            }

            return new double[]{moveX, moveY}; // Retornamos el desplazamiento calculado.
        }


        /**
         * Aplica el movimiento calculado al desplazamiento del mapa, permitiendo que
         * el personaje "se mueva" en pantalla desplazando el mapa y respetando los
         * límites del escenario.
         *
         * Además, sincroniza la posición del desplazamiento con los sistemas de
         * colisión y el escenario.
         *
         * @param movimiento Un array `double[2]` que contiene:
         *                   - [0] Movimiento en X
         *                   - [1] Movimiento en Y
         */
        private void aplicarMovimiento(double[] movimiento) {
            // Actualizar las posiciones del desplazamiento:
            int newDesplazamientoX = desplazamientoX + (int) movimiento[0];
            int newDesplazamientoY = desplazamientoY + (int) movimiento[1];

            // Limitar el movimiento dentro del tamaño del mapa:
            newDesplazamientoX = Math.max(0, Math.min(newDesplazamientoX, escenario.getAncho() - SCREEN_WIDTH));
            newDesplazamientoY = Math.max(0, Math.min(newDesplazamientoY, escenario.getAlto() - SCREEN_HEIGHT));

            // Si el desplazamiento cambia, actualizar el escenario y colisiones:
            if (newDesplazamientoX != desplazamientoX || newDesplazamientoY != desplazamientoY) {
                desplazamientoX = newDesplazamientoX; // Actualizamos X.
                desplazamientoY = newDesplazamientoY; // Actualizamos Y.
                escenario.actualizarDesplazamiento(desplazamientoX, desplazamientoY); // Actualizamos el fondo del mapa.
                colisiones.actualizarOffset(desplazamientoX, desplazamientoY); // Actualizamos las colisiones.
            }
        }



        //---------------------------------------------------
    //  🔹 DIBUJADO DEL PERSONAJE
    //---------------------------------------------------

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Obtener la imagen del personaje
            Image imagenPersonaje = personaje.getImagen();

            // Dibujar la imagen en el centro de la pantalla con rotación
            g2d.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
            g2d.rotate(anguloRotacion);

            int anchoImagen = imagenPersonaje.getWidth(this);
            int altoImagen = imagenPersonaje.getHeight(this);
            if (anchoImagen > 0 && altoImagen > 0) {
                g2d.drawImage(imagenPersonaje, -anchoImagen / 2, -altoImagen / 2, this);
            }

            g2d.rotate(-anguloRotacion);
            g2d.translate(-SCREEN_WIDTH / 2, -SCREEN_HEIGHT / 2);
        }


    }
