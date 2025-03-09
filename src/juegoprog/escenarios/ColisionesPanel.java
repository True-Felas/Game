package juegoprog.escenarios;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.awt.Graphics;


/** Gestiona las colisiones en el juego mediante una imagen.
 *  Las áreas opacas representan obstáculos en el mapa.
 *  Usa detección de colisiones basada en imágenes, común en juegos 2D. */


public class ColisionesPanel extends JPanel {

    //---------------------------------------------------
    //  🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------

    // Imagen en PNG que define las áreas de colisión
    private BufferedImage colisionesImg;

    // Desplazamiento del mapa de colisiones para poder sincronizarlo con el escenario
    private int desplazamientoX = 0;
    private int desplazamientoY = 0;


    //---------------------------------------------------
    //  🔹 CONSTRUCTOR
    //---------------------------------------------------

    /** Inicializamos el panel de colisiones y cargamos la imagen de colisión.
     *  El panel es transparente para que no cubra el escenario. */

    public ColisionesPanel() {

        setOpaque(false);
        cargarImagenCollision();
    }

    //---------------------------------------------------
    //  🔹 METODO PARA CARGAR LA IMAGEN DE COLISIÓN
    //---------------------------------------------------

    /** Carga la imagen de colisión desde los recursos y la almacena como `BufferedImage`.
     *  Usa `ImageIO.read()` (compatible con PNG) para leer la imagen directamente sin necesidad de conversión extra.
     *  Si la imagen no se encuentra, muestra un error en la consola.  */

    private void cargarImagenCollision() {
        try {
            URL url = getClass().getResource("/escenarios/colision_distrito_sombrio.png");
            if (url == null) {
                System.err.println("❌ Imagen de colisión no encontrada.");
                return;
            }
            colisionesImg = ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la imagen de colisión: " + e.getMessage());
        }
    }

    //---------------------------------------------------
    //  🔹 METODO PARA VERIFICAR COLISIÓN EN UNA POSICIÓN DADA
    //---------------------------------------------------

    /** Verificamos si una posición del mapa tiene colisión o es transitable.
     *  Se basa en la transparencia (canal alfa) de la imagen de colisión.
     *  x Coordenada X en el mapa
     *  y Coordenada Y en el mapa
     *  Return: `true` si hay colisión (píxel no transparente), `false` si es transitable. */

    public boolean hayColision(int x, int y) {
        if (colisionesImg == null) return false;

        int colisionX = x + desplazamientoX;
        int colisionY = y + desplazamientoY;

        // Verificar si está dentro de los límites
        if (colisionX < 0 || colisionX >= colisionesImg.getWidth() ||
                colisionY < 0 || colisionY >= colisionesImg.getHeight()) {
            return false;
        }

        // Obtener la transparencia del píxel
        int alpha = (colisionesImg.getRGB(colisionX, colisionY) >> 24) & 0xff;

        return alpha > 0;
    }

    //---------------------------------------------------
    //  🔹 METODO PARA DIBUJAR LA CAPA DE COLISIONES (DEBUG)
    //---------------------------------------------------
    /** Dibuja la imagen de colisión en el panel, desplazándola según el
     * movimiento del fondo. Esto nos asegura que el mapa de colisiones
     * siempre coincida con el escenario. */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (colisionesImg != null) {
            g.drawImage(colisionesImg, -desplazamientoX, -desplazamientoY, this);
        }
    }

    //---------------------------------------------------
    //  🔹 METODO PARA ACTUALIZAR LA POSICIÓN DE LA CAPA DE COLISIONES
    //---------------------------------------------------

    /** Actualiza el desplazamiento del mapa de colisiones para que coincida
     *  con el escenario. */

    public void actualizarOffset(int offsetX, int offsetY) {
        this.desplazamientoX = offsetX;
        this.desplazamientoY = offsetY;
    }

}
