package juegoprog.efectos;

import javax.swing.*;
import java.awt.*;

public class EfectoNieblaDinamica extends JPanel {

    private final Image imagenNiebla;  // Tu única imagen
    private float alpha = 0.5f;

    private int offsetX = 0;
    private int offsetY = 0;

    private final int velocidadY = 1;
    private final int fps = 30;

    public EfectoNieblaDinamica() {
        setOpaque(false);

        imagenNiebla = new ImageIcon(
                getClass().getResource("/resources/efectos/fog.png")
        ).getImage();

        int w = imagenNiebla.getWidth(null);
        int h = imagenNiebla.getHeight(null);
        System.out.println("fog.png mide: " + w + " x " + h);

        int delay = 1000 / fps;
        new Timer(delay, e -> {
            // Scroll vertical interno
            offsetY += velocidadY;
            if (offsetY >= h) {
                offsetY = 0;
            }
            repaint();
        }).start();
    }

    // --- NUEVO: para mover la niebla junto al escenario
    public void actualizarDesplazamiento(int desplX, int desplY) {
        // Al igual que EscenarioDistritoSombrio setLocation(-x, -y)
        // Ponemos la niebla en la posición "opuesta" a la cámara
        setLocation(-desplX, -desplY);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int imgW = imagenNiebla.getWidth(null);
        int imgH = imagenNiebla.getHeight(null);

        // Repetimos la textura
        for (int x = -imgW; x < getWidth() + imgW; x += imgW) {
            for (int y = -imgH; y < getHeight() + imgH; y += imgH) {
                int dibujoX = x + offsetX;
                int dibujoY = y + offsetY;
                g2d.drawImage(imagenNiebla, dibujoX, dibujoY, null);
            }
        }

        g2d.dispose();
    }
}
