package juegoprog.escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class EscenarioDistritoSombrio extends BaseEscenario {
    private BufferedImage imagenFondo;

    public EscenarioDistritoSombrio() {
        super(3192, 4096);
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            // Busca la imagen en la carpeta resources/escenarios/
            URL recurso = getClass().getClassLoader().getResource("escenarios/distrito_sombrio.png");
            if (recurso != null) {
                imagenFondo = ImageIO.read(recurso);
                System.out.println("Imagen del escenario cargada correctamente.");
            } else {
                System.out.println("Error: No se encontró la imagen del escenario.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la imagen del escenario.");
        }
    }

    @Override
    public void renderizar(Graphics g) {
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, null);
        } else {
            System.out.println("No se puede renderizar el escenario, la imagen no está cargada.");
        }
    }
}
