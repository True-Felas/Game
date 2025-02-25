import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Movimiento extends JPanel implements ActionListener {
    private int playerX = 300, playerY = 300;
    private int speed = 5;
    private double angle = 0;
    private boolean up, down, left, right;
    private Point mousePos = new Point(300, 300);

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
                mousePos = e.getPoint();
            }
        });
        Timer timer = new Timer(16, this);
        timer.start();
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
        if (up) playerY -= speed;
        if (down) playerY += speed;
        if (left) playerX -= speed;
        if (right) playerX += speed;

        // Calcula el ángulo entre el jugador y el cursor
        angle = Math.atan2(mousePos.y - playerY, mousePos.x - playerX);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Fondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Dibuja al jugador
        g2d.setColor(Color.RED);
        g2d.translate(playerX, playerY);
        g2d.rotate(angle);
        g2d.fillRect(-10, -10, 20, 20);
        g2d.rotate(-angle);
        g2d.translate(-playerX, -playerY);
    }
}
