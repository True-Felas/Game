package juegoprog.escenarios;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Panel para gestionar las colisiones en el juego.
 * Carga una imagen de colisiones donde las áreas no transparentes representan obstáculos.
 */
public class ColisionesPanel extends JPanel {
    private BufferedImage colisionesImg;

    public ColisionesPanel() {
        setOpaque(false); // 🔹 Hacemos que el panel sea invisible
        cargarImagenColision();
    }

    private void cargarImagenColision() {
        try {
            URL recurso = getClass().getClassLoader().getResource("escenarios/colision_distrito_sombrio.png");
            if (recurso != null) {
                colisionesImg = ImageIO.read(recurso);
                System.out.println("✅ Imagen de colisión cargada correctamente.");
            } else {
                System.out.println("❌ Error: No se encontró la imagen de colisión.");
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
    public boolean hayColision(int x, int y) {
        if (colisionesImg == null) return false;

        if (x < 0 || x >= colisionesImg.getWidth() || y < 0 || y >= colisionesImg.getHeight()) {
            return false;
        }

        int pixel = colisionesImg.getRGB(x, y);
        int alpha = (pixel >> 24) & 0xff;

        // 🔹 DEPURACIÓN: Imprime el color exacto del píxel
        System.out.println("🎨 Posición (" + x + ", " + y + ") - Color: " + Integer.toHexString(pixel) + " | Alpha: " + alpha);

        return alpha > 0;
    }

    public BufferedImage getImagenColision() {
        return colisionesImg;
    }


}

