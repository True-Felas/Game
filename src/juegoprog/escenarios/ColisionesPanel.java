package juegoprog.escenarios;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** Clase que gestiona las colisiones en el juego.
 * Se basa en una imagen donde las áreas sin transparencias representan "obstáculos".
 * Implementa el concepto de detección de colisiones por imagen, lo cual vimos es un metodo usado en juegos 2D. */

public class ColisionesPanel extends JPanel {

    //---------------------------------------------------
    //  🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------
    private BufferedImage colisionesImg; // Imagen que contiene la información de colisión

    //---------------------------------------------------
    //  🔹 CONSTRUCTOR
    //---------------------------------------------------

    public ColisionesPanel() {
        setOpaque(false); // Hacemos que el panel sea transparente para que no tape la imagen del escenario
        cargarImagenCollision(); // Cargamos la imagen de colisión al iniciar
    }

    //---------------------------------------------------
    //  🔹 METODO PARA CARGAR LA IMAGEN DE COLISIÓN
    //---------------------------------------------------

    private void cargarImagenCollision() {
        try {
            // Cargamos la imagen desde la carpeta de recursos

            InputStream input = getClass().getClassLoader().getResourceAsStream("escenarios/colision_distrito_sombrio.png");
            if (input != null) {
                BufferedImage imagenOriginal = ImageIO.read(input);

                // 🔹 Convertimos la imagen a un formato que soporte Alfa (transparencia)

                colisionesImg = new BufferedImage(
                        imagenOriginal.getWidth(),
                        imagenOriginal.getHeight(),
                        BufferedImage.TYPE_INT_ARGB // 🔹 Aseguramos que tenga Canal Alfa
                );

                Graphics2D g2d = colisionesImg.createGraphics();
                g2d.drawImage(imagenOriginal, 0, 0, null);
                g2d.dispose();

                System.out.println("✅ Imagen de colisión cargada correctamente con Alfa.");
            } else {
                System.out.println("❌ No se encontró la imagen de colisión en el classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error al cargar la imagen de colisión.");
        }
    }

    //---------------------------------------------------
    //  🔹 METODO PARA VERIFICAR COLISIÓN EN UNA POSICIÓN DADA
    //---------------------------------------------------

    /** Verifica si una posición del mapa es transitable o no.
     * Se basa en el canal alfa de la imagen de colisión.
     *
     * @param x Coordenada X en el mapa
     * @param y Coordenada Y en el mapa
     * @param offsetX Desplazamiento horizontal del mapa
     * @param offsetY Desplazamiento vertical del mapa
     * @return `true` si el píxel no es transparente (hay colisión), `false` si es transparente (se puede caminar). */

    public boolean hayColision(int x, int y, int offsetX, int offsetY) {
        if (colisionesImg == null) return false;

        // 🔹 Calculamos la posición en la imagen de colisiones
        int colisionX = (x + offsetX);
        int colisionY = (y + offsetY);

        // 🔹 Comprobamos si está dentro del rango de la imagen
        if (colisionX < 0 || colisionX >= colisionesImg.getWidth() || colisionY < 0 || colisionY >= colisionesImg.getHeight()) {
            System.out.println("⚠️ Fuera del rango de colisión -> X: " + colisionX + " | Y: " + colisionY);
            return false;
        }

        // 🔹 Obtenemos el color del píxel
        int pixel = colisionesImg.getRGB(colisionX, colisionY);
        int alpha = (pixel >> 24) & 0xff;

        // 🔹 Depuración
        System.out.println("🎨 Posición real: (" + colisionX + ", " + colisionY + ") - Color: " + Integer.toHexString(pixel) + " | Alfa: " + alpha);

        return alpha > 0;
    }


    //---------------------------------------------------
    //  🔹 METODO PARA OBTENER LA IMAGEN DE COLISIÓN
    //---------------------------------------------------

    public BufferedImage getImagenColision() {
        return colisionesImg;
    }

    //---------------------------------------------------
    //  🔹 METODO PARA DIBUJAR LA CAPA DE COLISIONES (DEBUG)
    //---------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (colisionesImg != null) {
            // 🔹 Aseguramos que se dibuja en la posición exacta
            g.drawImage(colisionesImg, -offsetX, -offsetY, colisionesImg.getWidth(), colisionesImg.getHeight(), null);
        }
    }




    //---------------------------------------------------
    //  🔹 METODO PARA ACTUALIZAR LA POSICIÓN DE LA CAPA DE COLISIONES
    //---------------------------------------------------
    int offsetX = 0;
    int offsetY = 0;
    public void actualizarOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        System.out.println("Colisiones Offset: X=" + offsetX + ", Y=" + offsetY);
        repaint();
    }


}
