package juegoprog.graficos;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PanelVidas extends JPanel {

    private int vidas; // Número de vidas actuales
    private Image imagenVida; // Imagen para representar las vidas

    // Constructor que inicializa el número de vidas
    public PanelVidas(int vidasIniciales, String rutaImagen) {
        this.vidas = vidasIniciales;
        ImageIcon icono = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/graficos/Vida3.png")));

        if (icono.getIconWidth() == -1) {
            System.err.println("Error: No se pudo cargar la imagen de la vida en " + rutaImagen);
        } else {
            this.imagenVida = icono.getImage().getScaledInstance(225, 225, Image.SCALE_SMOOTH);
        }

        setPreferredSize(new Dimension(225, 225)); // Tamaño del panel
    }

    // Metodo para actualizar el número de vidas y redibujar el panel
    public void actualizarVidas(int nuevasVidas) {
        this.vidas = nuevasVidas;
        repaint(); // Redibuja el panel
    }

    // Metodo para dibujar las imágenes según el número de vidas
    @Override
    protected void paintComponent(Graphics g) {

        if (imagenVida == null) {
            return; // No dibujar si la imagen no está cargada
        }

        // Dibujar tantas imágenes como vidas tenga el personaje
        int anchoImagen = 40;
        int altoImagen = 40;
        int espacio = 10; // Espaciado entre imágenes

        for (int i = 0; i < vidas; i++) {
            int x = 10 + i * (anchoImagen + espacio); // Posición horizontal
            int y = (getHeight() - altoImagen) / 2; // Centra verticalmente
            g.drawImage(imagenVida, x, y, anchoImagen, altoImagen, this);
        }
    }
}
