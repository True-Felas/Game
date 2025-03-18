package juegoprog.jugador;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Personaje extends JPanel {
    private ImageIcon gifNormal;  // GIF de caminar o estar quieto
    private ImageIcon gifCorrer;  // GIF de correr
    private ImageIcon gifActual;  // GIF actualmente usado

    // Posición actual del personaje
    private int x = 50;
    private int y = 50;

    private int vida;
    // Constructor
    public Personaje() {
        // Cargar GIFs
        cargarGifNormal("/personaje/personaje_andando.gif"); // Ruta del GIF normal
        cargarGifCorrer("/personaje/personaje_corriendo.gif"); // Ruta del GIF de correr

        // Por defecto, usa el GIF normal
        gifActual = gifNormal;

        this.vida = 3;
    }

    public Image getImagen() {
        return gifActual.getImage();
    }

    public void cargarGifNormal(String ruta) {
        try {
            ImageIcon original = new ImageIcon(Objects.requireNonNull(getClass().getResource(ruta)));
            gifNormal = new ImageIcon(original.getImage().getScaledInstance(70, 65, Image.SCALE_DEFAULT));
        } catch (Exception e) {
            System.err.println("Error al cargar el GIF normal: " + ruta);
        }
    }

    public void cargarGifCorrer(String ruta) {
        try {
            ImageIcon original = new ImageIcon(Objects.requireNonNull(getClass().getResource(ruta)));
            gifCorrer = new ImageIcon(original.getImage().getScaledInstance(70, 65, Image.SCALE_DEFAULT));
        } catch (Exception e) {
            System.err.println("Error al cargar el GIF de correr: " + ruta);
        }
    }

    // Cambiar entre los GIF
    public void setCorrer(boolean corriendo) {
        if (corriendo) {
            gifActual = gifCorrer;  // Si está corriendo, usa el GIF de correr
        } else {
            gifActual = gifNormal;  // Si no, usa el GIF normal
        }
    }

    // Métodos de posición
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

    // Métodos para la vida del personaje

    public int getVida() {
        return vida; // Obtener vida actual
    }

    public void setVida(int vida) {
        this.vida = Math.max(0, vida); // Aseguramos que la vida no sea negativa
    }


}
