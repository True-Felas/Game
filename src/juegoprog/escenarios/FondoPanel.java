package juegoprog.escenarios;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class FondoPanel extends JPanel {
    private BufferedImage imagenFondo;

    public FondoPanel() {
        cargarImagen();
        // Omitimos el setPreferredSize, ya que en Pantalla lo posicionamos con setBounds()
        setOpaque(true); // Si quieres ver dónde termina, puedes poner setOpaque(true) y setBackground(Color.BLACK)
    }

    private void cargarImagen() {
        try {
            URL recurso = getClass().getClassLoader().getResource("escenarios/distrito_sombrio.png");
            if (recurso != null) {
                imagenFondo = ImageIO.read(recurso);
                System.out.println("✅ Imagen del escenario cargada correctamente.");
            } else {
                System.out.println("❌ ERROR: No se encontró la imagen del escenario.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ ERROR al cargar la imagen del escenario.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            // Dibuja la imagen en su tamaño original
            g.drawImage(imagenFondo, 0, 0, imagenFondo.getWidth(), imagenFondo.getHeight(), null);
        }
    }
}

