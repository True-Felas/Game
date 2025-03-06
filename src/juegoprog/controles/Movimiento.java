package juegoprog.sistema;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
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
        private int velocidad = 5; // 🔹 Velocidad de movimiento

        /** CONTROL DE MOVIMIENTO */

        private boolean up, down, left, right; // 🔹 Control de teclas presionadas
        private double anguloRotacion  = 0; // 🔹 Ángulo de rotación basado en el puntero

        /** CONTROL DEL RATÓN Y DESPLAZAMIENTO DEL MAPA */

        private final Point posicionRaton = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2); // 🔹 Posición del puntero
        private int desplazamientoX = 0, desplazamientoY = 0; // 🔹 Desplazamiento del escenario

        /** REFERENCIAS AL ESCENARIO Y COLISIONES */

        private final EscenarioDistritoSombrio escenario; // 🔹 Referencia al escenario
        private final ColisionesPanel colisiones; // 🔹 Referencia al panel de colisiones

    //--------------------------------------------------------
    //  🔹 CONSTRUCTOR DE MOVIMIENTO + EVENTOS RATÓN Y TECLADO
    //--------------------------------------------------------

        /** Inicializa el movimiento, captura eventos de teclado y ratón,
         *  y sincroniza la cámara con el escenario y colisiones. */

        public Movimiento(EscenarioDistritoSombrio escenario, ColisionesPanel colisiones) {
            this.escenario = escenario;
            this.colisiones = colisiones;
            setOpaque(false);
            setFocusable(true);

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
            new Timer(16, this).start();
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

        /** Gestiona el movimiento del personaje y el desplazamiento del fondo.
         *  Verifica colisiones y evita que el mapa se salga de los límites. */

        private void moverJugador() {
            int newDesplazamientoX = desplazamientoX, newDesplazamientoY = desplazamientoY;
            int personajeX = SCREEN_WIDTH / 2, personajeY = SCREEN_HEIGHT / 2;
            int hitboxSize = 10;

            // 🔹 Verificar colisiones en cada dirección
            boolean colisionArriba = colisiones.hayColision(personajeX, personajeY - hitboxSize - velocidad);
            boolean colisionAbajo = colisiones.hayColision(personajeX, personajeY + hitboxSize + velocidad);
            boolean colisionIzquierda = colisiones.hayColision(personajeX - hitboxSize - velocidad, personajeY);
            boolean colisionDerecha = colisiones.hayColision(personajeX + hitboxSize + velocidad, personajeY);

            // 🔹 Calcular nuevo desplazamiento según la dirección del movimiento
            if (up && !colisionArriba) newDesplazamientoY -= velocidad;
            if (down && !colisionAbajo) newDesplazamientoY += velocidad;
            if (left && !colisionIzquierda) newDesplazamientoX -= velocidad;
            if (right && !colisionDerecha) newDesplazamientoX += velocidad;

            // 🔹 Aplicar límites para que el mapa no se salga de los bordes
            newDesplazamientoX = Math.max(0, Math.min(newDesplazamientoX, escenario.getAncho() - SCREEN_WIDTH));
            newDesplazamientoY = Math.max(0, Math.min(newDesplazamientoY, escenario.getAlto() - SCREEN_HEIGHT));

            // 🔹 Actualizar desplazamiento solo si hay cambios
            if (newDesplazamientoX != desplazamientoX || newDesplazamientoY != desplazamientoY) {
                desplazamientoX = newDesplazamientoX;
                desplazamientoY = newDesplazamientoY;
                escenario.actualizarDesplazamiento(desplazamientoX, desplazamientoY);
                colisiones.actualizarOffset(desplazamientoX, desplazamientoY);
            }

            // 🔹 Redibujar pantalla
            repaint();
        }

    //---------------------------------------------------
    //  🔹 DIBUJADO DEL PERSONAJE
    //---------------------------------------------------

        /** Dibuja al personaje en el centro de la pantalla
         * con su rotación correspondiente. */

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 🔹 Configuración de color y rotación
            g2d.setColor(Color.RED);
            g2d.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
            g2d.rotate(anguloRotacion);
            g2d.fillRect(-10, -10, 20, 20);
            g2d.rotate(-anguloRotacion);
            g2d.translate(-SCREEN_WIDTH / 2, -SCREEN_HEIGHT / 2);
        }
    }
