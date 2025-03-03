package juegoprog.sistema;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Clase encargada del movimiento del personaje y de la cámara.
 * - Se asegura de que el fondo no tape el escenario.
 * - Permite que el personaje mire siempre al puntero.
 * - Implementa eventos de teclado y ratón.
 * - Según los apuntes de Soraya sobre "Eventos y Escuchadores"
 *   (1.4. EVENTOS Y ESCUCHADORES.docx). */

public class Movimiento extends JPanel implements ActionListener {

    //---------------------------------------------------
    //  🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------

    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;
    private int velocidad = 5;
    private double ang = 0; // 🔹 Ángulo de rotación basado en el puntero
    private boolean up, down, left, right; // 🔹 Control de teclas presionadas

    // 🔹 Punto de referencia para el ratón

    private Point ratonPos = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
    private int offsetX = 0, offsetY = 0; // 🔹 Control de desplazamiento

    private EscenarioDistritoSombrio escenario; // 🔹 Referencia al escenario
    private ColisionesPanel colisiones; // 🔹 Referencia al panel de colisiones

    //---------------------------------------------------
    //  🔹 CONSTRUCTOR DE MOVIMIENTO
    //---------------------------------------------------

    /** Captura eventos de teclado y ratón según los apuntes de "Eventos y Escuchadores"
     * (1.4. EVENTOS Y ESCUCHADORES.docx).
     *
     * @param escenario Escenario en el que nos movemos.
     * @param colisiones Panel de colisiones para detectar obstáculos. */

    public Movimiento(EscenarioDistritoSombrio escenario, ColisionesPanel colisiones) {
        this.escenario = escenario;
        this.colisiones = colisiones;
        setOpaque(false);
        setFocusable(true);

        // 🔹 Escuchadores de teclado. Usamos KeyListener (apuntes).

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

        // 🔹 Escuchador de movimiento del ratón. Usamos MouseMotionListener.

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ratonPos.x = e.getX() + offsetX;
                ratonPos.y = e.getY() + offsetY;
                actualizarAngulo();
            }
        });

        // 🔹 Temporizador para actualizar el movimiento del personaje.

        Timer timer = new Timer(16, this);
        timer.start();
    }

    //---------------------------------------------------
    //  🔹 MÉTODOS DE MOVIMIENTO Y CONTROL
    //---------------------------------------------------

    /** Calcula el ángulo exacto de rotación basándose en la posición del ratón.
     * Corrige la orientación cuando el mapa se mueve. */

    private void actualizarAngulo() {
        ang = Math.atan2((ratonPos.y - offsetY) - SCREEN_HEIGHT / 2, (ratonPos.x - offsetX) - SCREEN_WIDTH / 2);
    }

    /** Metodo para actualizar la posición relativa del ratón al fondo en cada frame.
     * Evita que el personaje "pierda" el puntero cuando el mapa se mueve, cosa importante */

    private void actualizarRatonPos() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo != null) {
            Point puntoRaton = pointerInfo.getLocation();
            SwingUtilities.convertPointFromScreen(puntoRaton, this);
            ratonPos = new Point(puntoRaton.x + offsetX, puntoRaton.y + offsetY);
        }
    }
/** Metodo para activar o desactivar el movimiento según la tecla presionada.
 * @param keyCode Código de la tecla presionada.
 * @param pressed Estado de la tecla (true si está presionada, false si se soltó). */

    private void toggleMovement(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W -> up = pressed;
            case KeyEvent.VK_S -> down = pressed;
            case KeyEvent.VK_A -> left = pressed;
            case KeyEvent.VK_D -> right = pressed;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        repaint();
    }

    //---------------------------------------------------
    //  🔹 LÓGICA DE MOVIMIENTO Y COLISIONES
    //---------------------------------------------------

    private void movePlayer() {
        int newOffsetX = offsetX;
        int newOffsetY = offsetY;

        // 🔹 Coordenadas exactas del cuadro rojo
        int personajeX = SCREEN_WIDTH / 2;  // Centro del personaje en pantalla
        int personajeY = SCREEN_HEIGHT / 2;
        int hitboxSize = 10;  // 🔹 Ajusta según el tamaño del personaje

        // 🔹 Comprobamos la colisión en el centro del personaje
        boolean colisionArriba = colisiones.hayColision(personajeX, personajeY - hitboxSize, offsetX, offsetY);
        boolean colisionAbajo = colisiones.hayColision(personajeX, personajeY + hitboxSize, offsetX, offsetY);
        boolean colisionIzquierda = colisiones.hayColision(personajeX - hitboxSize, personajeY, offsetX, offsetY);
        boolean colisionDerecha = colisiones.hayColision(personajeX + hitboxSize, personajeY, offsetX, offsetY);

        // 🔹 Solo movemos si NO hay colisión en esa dirección
        if (up && !colisionArriba) {
            newOffsetY -= velocidad;
        }
        if (down && !colisionAbajo) {
            newOffsetY += velocidad;
        }
        if (left && !colisionIzquierda) {
            newOffsetX -= velocidad;
        }
        if (right && !colisionDerecha) {
            newOffsetX += velocidad;
        }

        // 🔹 Aplicamos límites
        newOffsetX = Math.max(0, Math.min(newOffsetX, escenario.getAncho() - SCREEN_WIDTH));
        newOffsetY = Math.max(0, Math.min(newOffsetY, escenario.getAlto() - SCREEN_HEIGHT));

        // 🔹 Solo actualizamos si hay cambios
        if (newOffsetX != offsetX || newOffsetY != offsetY) {
            offsetX = newOffsetX;
            offsetY = newOffsetY;
            escenario.actualizarOffset(offsetX, offsetY);
            colisiones.actualizarOffset(offsetX, offsetY);
        }
    }





    //---------------------------------------------------
    //  🔹 DIBUJADO DEL PERSONAJE
    //---------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 🔹 Dibujamos el personaje en el centro de la pantalla

        g2d.setColor(Color.RED);
        int drawX = SCREEN_WIDTH / 2;
        int drawY = SCREEN_HEIGHT / 2;

        g2d.translate(drawX, drawY);
        g2d.rotate(ang);
        g2d.fillRect(-10, -10, 20, 20);
        g2d.rotate(-ang);
        g2d.translate(-drawX, -drawY);
    }
}
