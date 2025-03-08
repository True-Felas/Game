package juegoprog.jugador;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class Personaje extends JPanel {
    private Image imagen; // La imagen del personaje
    private final int ancho = 60;
    private final int alto = 60; // Tamaño del personaje

    // Posición actual del personaje
    private int x = 50; // Coordenada X inicial
    private int y = 50; // Coordenada Y inicial

    // Constructor
    public Personaje() {
        cargarImagen("/personaje/prueba.png"); // Ruta de la imagen en resources
    }

    public Image getImagen() {
        return imagen;
    }

    // Carga una imagen y le da tamaño
    public void cargarImagen(String rutaImagen) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(rutaImagen)));
            this.imagen = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + rutaImagen);
            this.imagen = null; // Revertir a imagen nula
        }
    }

    // Dibuja personaje
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Ahora dibuja la imagen
        if (imagen != null) {
            g.drawImage(imagen, x, y, ancho, alto, this);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, ancho, alto); // Si no se carga la imagen, dibuja un cuadrado
        }

        Toolkit.getDefaultToolkit().sync(); // Para animación suave
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
        repaint(); // Redibujar el personaje en su nueva posición
    }



}


