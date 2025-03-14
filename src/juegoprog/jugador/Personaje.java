package juegoprog.jugador;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class Personaje extends JPanel {
    private Image imagen; // La imagen del personaje

    // Posición actual del personaje
    private int x = 50; // Coordenada X inicial
    private int y = 50; // Coordenada Y inicial

    // Constructor
    public Personaje() {
        cargarImagen("/personaje/personajep.png"); // Ruta de la imagen en resources
    }

    public Image getImagen() {
        return imagen;
    }

    // Carga una imagen y le da tamaño
    public void cargarImagen(String rutaImagen) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(rutaImagen)));

            // Tamaño del personaje
            int alto = 60;
            int ancho = 60;
            this.imagen = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + rutaImagen);
            this.imagen = null; // Revertir a imagen nula
        }
    }

    // Métodos para acceder y modificar las coordenadas del personaje
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }
}


