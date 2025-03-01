package juegoprog.sistema;

import juegoprog.escenarios.EscenarioDistritoSombrio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Clase encargada del movimiento del personaje y de la cámara.
 * Se asegura de que el fondo no tape el escenario y de que el personaje mire siempre al puntero.
 * Implementa eventos de teclado y ratón según los apuntes sobre "Eventos y Escuchadores"
 * (1.4. EVENTOS Y ESCUCHADORES.docx).
 */
public class Movimiento extends JPanel implements ActionListener {
    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;
    private int velocidad = 5;
    private double ang = 0;
    private boolean up, down, left, right;

    // 🔹 Punto de referencia para el ratón
    private Point ratonPos = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

    private int offsetX = 0;
    private int offsetY = 0;
    private EscenarioDistritoSombrio escenario; // 🔹 Referencia al escenario para ajustar colisiones

    public Movimiento(EscenarioDistritoSombrio escenario) {
        this.escenario = escenario;
        setOpaque(false); // ✅ Permite ver el fondo sin taparlo
        setFocusable(true);

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

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // 🔹 Capturamos la posición del ratón en coordenadas del mapa
                ratonPos.x = e.getX() + offsetX;
                ratonPos.y = e.getY() + offsetY;

                // 🔹 Calculamos el ángulo correctamente
                actualizarAngulo();
            }
        });

        Timer timer = new Timer(16, this);
        timer.start();
    }

    /**
     * 🔹 Calcula el ángulo exacto basándose en la posición global del ratón.
     * Corrige el problema de orientación cuando el mapa se mueve.
     */
    private void actualizarAngulo() {
        // 🔹 Ajustamos el cálculo para que siempre sea relativo al centro de la pantalla
        ang = Math.atan2((ratonPos.y - offsetY) - SCREEN_HEIGHT / 2, (ratonPos.x - offsetX) - SCREEN_WIDTH / 2);
    }

    /**
     * 🔹 Método para actualizar la posición del ratón RELATIVA al fondo en cada frame.
     * Esto evita que el personaje "pierda" el puntero cuando el mapa se mueve.
     */
    private void actualizarRatonPos() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo != null) {
            Point puntoRaton = pointerInfo.getLocation();
            SwingUtilities.convertPointFromScreen(puntoRaton, this);
            ratonPos = new Point(puntoRaton.x + offsetX, puntoRaton.y + offsetY);
        }
    }

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

    private void movePlayer() {
        int newX = offsetX;
        int newY = offsetY;

        if (up) newY -= velocidad;
        if (down) newY += velocidad;
        if (left) newX -= velocidad;
        if (right) newX += velocidad;

        // 🔹 Aplicamos restricciones de límites del mapa
        newX = Math.max(0, Math.min(newX, escenario.getAncho() - SCREEN_WIDTH));
        newY = Math.max(0, Math.min(newY, escenario.getAlto() - SCREEN_HEIGHT));

        offsetX = newX;
        offsetY = newY;

        // 🔹 Asegurar que el fondo también se mueva con el offset
        escenario.actualizarOffset(offsetX, offsetY);

        // 🔹 Volvemos a calcular el ángulo para evitar que el personaje pierda la orientación al moverse
        actualizarRatonPos();
        actualizarAngulo();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 🔹 Dibujamos el personaje en el centro de la pantalla
        g2d.setColor(Color.RED);
        int drawX = SCREEN_WIDTH / 2;
        int drawY = SCREEN_HEIGHT / 2;

        g2d.translate(drawX, drawY);
        g2d.rotate(ang);  // 🔹 Aplica la rotación exacta
        g2d.fillRect(-10, -10, 20, 20);
        g2d.rotate(-ang);
        g2d.translate(-drawX, -drawY);
    }
}
