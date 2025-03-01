package juegoprog.sistema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Implementar ActionListener para poder detectar teclado y ratón
public class Movimiento extends JPanel implements ActionListener {
    private final int MAP_WIDTH = 3192;  // NUEVO: Definimos el tamaño real del mapa
    private final int MAP_HEIGHT = 4096; // NUEVO: Definimos el tamaño real del mapa
    private final int SCREEN_WIDTH = 1280; // Ancho de la ventana
    private final int SCREEN_HEIGHT = 720;  // Alto de la ventana

    private int playerX = MAP_WIDTH / 2, playerY = MAP_HEIGHT / 2; // NUEVO: El personaje inicia en el centro del mapa
    private int velocidad = 5;
    private double ang = 0;
    private boolean up, down, left, right;
    private Point ratonPos = new Point(playerX, playerY);

    private int offsetX = playerX - SCREEN_WIDTH / 2; // NUEVO: Desplazamiento de la cámara
    private int offsetY = playerY - SCREEN_HEIGHT / 2; // NUEVO: Desplazamiento de la cámara

    public Movimiento() {
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
                ratonPos = new Point(e.getX() + offsetX, e.getY() + offsetY); // NUEVO: Ajustamos la posición del ratón al mapa
            }
        });
        Timer timer = new Timer(16, this);
        timer.start();
    }

    //Asignación de teclas
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
        int newX = playerX;
        int newY = playerY;

        if (up) newY -= velocidad;
        if (down) newY += velocidad;
        if (left) newX -= velocidad;
        if (right) newX += velocidad;

        // NUEVO: Asegurar que el personaje no salga del mapa
        if (newX >= 0 && newX <= MAP_WIDTH) playerX = newX;
        if (newY >= 0 && newY <= MAP_HEIGHT) playerY = newY;

        // NUEVO: La cámara sigue al jugador
        offsetX = playerX - SCREEN_WIDTH / 2;
        offsetY = playerY - SCREEN_HEIGHT / 2;

        // NUEVO: Calcula el ángulo entre el jugador y el cursor
        ang = Math.atan2(ratonPos.y - playerY, ratonPos.x - playerX);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // NUEVO: Fondo del mapa
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, MAP_WIDTH, MAP_HEIGHT);

        // NUEVO: Dibujar cuadrícula para referencia visual
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < MAP_WIDTH; i += 100) { // Líneas verticales cada 100px
            g.drawLine(i - offsetX, 0, i - offsetX, MAP_HEIGHT);
        }
        for (int j = 0; j < MAP_HEIGHT; j += 100) { // Líneas horizontales cada 100px
            g.drawLine(0, j - offsetY, MAP_WIDTH, j - offsetY);
        }

        // Dibujo del personaje en el centro de la pantalla
        g2d.setColor(Color.RED);
        int drawX = SCREEN_WIDTH / 2;  // Siempre en el centro de la pantalla
        int drawY = SCREEN_HEIGHT / 2;
        g2d.translate(drawX, drawY);
        g2d.rotate(ang);
        g2d.fillRect(-10, -10, 20, 20);
        g2d.rotate(-ang);
        g2d.translate(-drawX, -drawY);
    }
}
