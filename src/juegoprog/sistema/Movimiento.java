package juegoprog.sistema;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;

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
    private ColisionesPanel colisiones; // 🔹 Referencia al panel de colisiones

    public Movimiento(EscenarioDistritoSombrio escenario, ColisionesPanel colisiones) {
        this.escenario = escenario;
        this.colisiones = colisiones; // 🔹 Guardamos la referencia al panel de colisiones
        setOpaque(false);
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
                ratonPos.x = e.getX() + offsetX;
                ratonPos.y = e.getY() + offsetY;
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
        int newOffsetX = offsetX;
        int newOffsetY = offsetY;
        int newPlayerX = SCREEN_WIDTH / 2; // 🔹 Mantiene al personaje en el centro por defecto
        int newPlayerY = SCREEN_HEIGHT / 2;

        boolean moved = false; // 🔹 Detecta si hubo movimiento real

        int margen = 200; // 🔹 Espacio dentro del cual el personaje puede moverse sin desplazar el fondo

        // 🔹 Movimiento en Y (arriba/abajo)
        if (up) {
            if (offsetY > 0) {
                newOffsetY -= velocidad;
                moved = true;
            } else if (newPlayerY > 0) {
                newPlayerY -= velocidad;
                moved = true;
            }
        }
        if (down) {
            if (offsetY < escenario.getAlto() - SCREEN_HEIGHT) {
                newOffsetY += velocidad;
                moved = true;
            } else if (newPlayerY < SCREEN_HEIGHT) {
                newPlayerY += velocidad;
                moved = true;
            }
        }

        // 🔹 Movimiento en X (izquierda/derecha)
        if (left) {
            if (offsetX > 0) {
                newOffsetX -= velocidad;
                moved = true;
            } else if (newPlayerX > 0) {
                newPlayerX -= velocidad;
                moved = true;
            }
        }
        if (right) {
            if (offsetX < escenario.getAncho() - SCREEN_WIDTH) {
                newOffsetX += velocidad;
                moved = true;
            } else if (newPlayerX < SCREEN_WIDTH) {
                newPlayerX += velocidad;
                moved = true;
            }
        }

        // 🔹 Aplicamos las restricciones para no salir del mapa
        newOffsetX = Math.max(0, Math.min(newOffsetX, escenario.getAncho() - SCREEN_WIDTH));
        newOffsetY = Math.max(0, Math.min(newOffsetY, escenario.getAlto() - SCREEN_HEIGHT));

        // 🔹 Si no hubo movimiento, no seguimos con el código (optimización)
        if (!moved) return;

        int colisionX = offsetX + SCREEN_WIDTH / 2;
        int colisionY = offsetY + SCREEN_HEIGHT / 2;

        if (colisiones != null && colisiones.getImagenColision() != null) {
            int color = colisiones.getImagenColision().getRGB(offsetX + SCREEN_WIDTH / 2, offsetY + SCREEN_HEIGHT / 2);
            boolean colision = colisiones.hayColision(offsetX + SCREEN_WIDTH / 2, offsetY + SCREEN_HEIGHT / 2);

            System.out.println("🔍 Posición: (" + (offsetX + SCREEN_WIDTH / 2) + ", " + (offsetY + SCREEN_HEIGHT / 2) + ") - ¿Colisión? " + colision);
            System.out.println("🎨 Color en la posición actual: " + Integer.toHexString(color));
        }

        offsetX = newOffsetX;
        offsetY = newOffsetY;

        // 🔹 Asegurar que el fondo también se mueva con el offset
        escenario.actualizarOffset(offsetX, offsetY);

        // 🔹 Volvemos a calcular el ángulo para evitar que el personaje pierda la orientación al moverse
        actualizarRatonPos();
        actualizarAngulo();

        // 🔹 Debug: Solo imprimimos si hubo cambio real
        System.out.println("🎮 PlayerX: " + newPlayerX + " | PlayerY: " + newPlayerY);
        System.out.println("🗺️ OffsetX: " + offsetX + " | OffsetY: " + offsetY);
        System.out.println("📏 Tamaño del escenario -> Ancho: " + escenario.getAncho() + " | Alto: " + escenario.getAlto());
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
