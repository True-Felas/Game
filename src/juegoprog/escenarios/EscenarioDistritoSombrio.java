package juegoprog.escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

    /** Representa el escenario de "Distrito Sombrío", gestionando su fondo y desplazamiento.
    *   Extiende `BaseEscenario` para manejar dimensiones y comportamiento del mapa. */

    public class EscenarioDistritoSombrio extends BaseEscenario {

    //---------------------------------------------------
    //  🔹 CONSTANTES DEL ESCENARIO
    //---------------------------------------------------

    private final int ANCHO = 4472;
    private final int ALTO = 4816;

    //---------------------------------------------------
    //  🔹 ATRIBUTOS PRINCIPALES
    //---------------------------------------------------

    private BufferedImage imagenFondo; // 🔹 Imagen del escenario
    private int desplazamientoX = 0, desplazamientoY = 0; // 🔹 Control del desplazamiento

    //----------------------------------------------------------------
    //  🔹 CONSTRUCTOR - CONFIGURA TAMAÑO DEL ESCENARIO Y CARGA IMAGEN
    //----------------------------------------------------------------

    public EscenarioDistritoSombrio() {
        super(4472, 4816);
        setSize(ANCHO, ALTO);
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setVisible(true);

        cargarImagen();
    }

    //---------------------------------------------------
    //  🔹 DIBUJA EL ESCENARIO APLICANDO EL DESPLAZAMIENTO
    //---------------------------------------------------

    /** Dibuja el fondo del escenario en la pantalla, aplicando el
     *  desplazamiento según la posición del jugador. */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagenFondo != null) {
            g.drawImage(imagenFondo, -desplazamientoX, -desplazamientoY, null);
        }
    }

    //---------------------------------------------------
    //  🔹 METODO PARA CARGAR LA IMAGEN DEL ESCENARIO
    //---------------------------------------------------

    /** Carga la imagen del escenario desde los recursos del proyecto.
    *   Se utiliza `getResource()` porque funciona bien con archivos dentro del JAR. */

    private void cargarImagen() {
        try {
            URL url = getClass().getResource("/escenarios/distrito_sombrio2.png");
            if (url == null) {
                System.err.println("❌ Imagen del escenario no encontrada.");
                return;
            }
            imagenFondo = ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la imagen del escenario: " + e.getMessage());
        }
    }

    //---------------------------------------------------
    //  🔹 METODO PARA CARGAR LA IMAGEN DEL ESCENARIO
    //---------------------------------------------------

    /** Actualiza el desplazamiento del escenario según la posición del personaje. */

    public void actualizarDesplazamiento(int x, int y) {
        this.desplazamientoX = x;
        this.desplazamientoY = y;
    }

    //---------------------------------------------------
    //  🔹 MÉTODOS GETTERS
    //---------------------------------------------------


        public BufferedImage getFondo() {
            return imagenFondo;
        }

        /** Retorna el ancho del escenario. */
    public int getAncho() {
        return ANCHO;
    }

    /** Retorna la altura del escenario. */
    public int getAlto() {
        return ALTO;
    }
}

