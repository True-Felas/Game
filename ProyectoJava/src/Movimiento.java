import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Implementar ActionListener para poder detectar teclado y ratón
public class Movimiento extends JPanel implements ActionListener {
    private int playerX = 300, playerY = 300;
    private int velocidad = 5;
    private double ang = 0;
    private boolean up, down, left, right;
    private Point ratonPos = new Point(300, 300);

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
                ratonPos = e.getPoint();
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
        if (up) playerY -= velocidad;
        if (down) playerY += velocidad;
        if (left) playerX -= velocidad;
        if (right) playerX += velocidad;

        // Calcula el ángulo entre el jugador y el cursor
        ang = Math.atan2(ratonPos.y - playerY, ratonPos.x - playerX);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Fondo para pruebas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Cuadrado para pruebas
        g2d.setColor(Color.RED);
        g2d.translate(playerX, playerY);
        g2d.rotate(ang);
        g2d.fillRect(-10, -10, 20, 20);
        g2d.rotate(-ang);
        g2d.translate(-playerX, -playerY);
    }
}
