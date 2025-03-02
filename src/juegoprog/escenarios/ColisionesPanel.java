package juegoprog.escenarios;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.awt.Graphics;
import java.awt.Graphics2D;


/**
 * Panel para gestionar las colisiones en el juego.
 * Carga una imagen de colisiones donde las áreas no transparentes representan obstáculos.
 */
public class ColisionesPanel extends JPanel {
    private BufferedImage colisionesImg;

    public ColisionesPanel() {
        setOpaque(false); // 🔹 Hacemos que el panel sea invisible
        cargarImagenCollision();
    }

    private void cargarImagenCollision() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("escenarios/colision_distrito_sombrio.png");
            if (input != null) {
                colisionesImg = ImageIO.read(input);
                System.out.println("✅ Imagen de colisión cargada correctamente.");
            } else {
                System.out.println("❌ No se encontró la imagen de colisión en el classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error al cargar la imagen de colisión.");
        }
    }

    /**
     * Método para verificar si hay colisión en una posición dada.
     * @param x Coordenada X en el mapa
     * @param y Coordenada Y en el mapa
     * @return `true` si el pixel no es transparente (colisión), `false` si es transparente (se puede caminar).
     */
    public boolean hayColision(int x, int y, int offsetX, int offsetY) {
        if (colisionesImg == null) return false; // Si la imagen no se cargó, no hay colisión

        int colisionX = x - offsetX; // 🔹 Ajustamos la coordenada con el desplazamiento
        int colisionY = y - offsetY;

        // Validamos que esté dentro del rango de la imagen
        if (colisionX < 0 || colisionX >= colisionesImg.getWidth() || colisionY < 0 || colisionY >= colisionesImg.getHeight()) {
            System.out.println("❌ Fuera del rango de la imagen de colisión -> X: " + colisionX + " | Y: " + colisionY);
            return false;
        }

        // Obtenemos el color del píxel
        int pixel = colisionesImg.getRGB(colisionX, colisionY);
        int alpha = (pixel >> 24) & 0xff; // 🔹 Extraemos el canal alfa

        // Debug: Imprimir si hay colisión o no
        System.out.println("🎨 Posición: (" + colisionX + ", " + colisionY + ") - Color: " + Integer.toHexString(pixel) + " | Alfa: " + alpha);

        return alpha == 0;  // Invertimos la condición

    }




    public BufferedImage getImagenColision() {
        return colisionesImg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (colisionesImg != null) {
            g.drawImage(colisionesImg, 0, 0, this); // 🔹 Dibuja la imagen de colisión encima
        }
    }

    public void actualizarOffset(int offsetX, int offsetY) {
        setLocation(-offsetX, -offsetY); // 🔹 Ajustamos la posición del panel en relación con el fondo
        repaint();
    }


}

