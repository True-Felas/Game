package juegoprog.elementos;

import juegoprog.escenarios.ColisionesPanel;

import java.awt.*;

/**
 * Representa una bala que se mueve por el escenario, dispara hacia una dirección,
 * y detecta colisiones con los objetos del mapa.
 */
public class Bala {
    private double x, y;          // Posición actual de la bala en el mundo
    private double dx, dy;        // Dirección de movimiento (vector normalizado)
    private final int velocidad;  // Velocidad de desplazamiento de la bala
    private final int tamano = 5; // Tamaño de la bala en píxeles
    private boolean activa = true; // Indica si la bala sigue activa

    /**
     * Constructor de la clase Bala.
     *
     * @param xInicial    Posición inicial X de la bala.
     * @param yInicial    Posición inicial Y de la bala.
     * @param xObjetivo   Coordenada X hacia donde se disparó.
     * @param yObjetivo   Coordenada Y hacia donde se disparó.
     */
    public Bala(double xInicial, double yInicial, double xObjetivo, double yObjetivo) {
        this.x = xInicial;
        this.y = yInicial;
        this.velocidad = 30;

        // Calcula el vector normalizado hacia el objetivo
        double distancia = Math.sqrt((xObjetivo - xInicial) * (xObjetivo - xInicial) +
                (yObjetivo - yInicial) * (yObjetivo - yInicial));
        dx = (xObjetivo - xInicial) / distancia;
        dy = (yObjetivo - yInicial) / distancia;
    }

    /**
     * Actualiza la posición de la bala y verifica colisiones.
     */
    public void actualizar() {
        if (!activa) return; // Si ya está inactiva, no hace nada

        x += dx * velocidad;
        y += dy * velocidad;

    }

    /**
     * Verifica si la bala sigue activa.
     *
     * @return true si la bala está activa, false si está desactivada.
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * Dibuja la bala en el contexto gráfico del juego.
     *
     * @param g Contexto gráfico donde se dibuja la bala.
     */
    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY) {
        if (!activa) return; // Si no está activa, no se dibuja

        // Ajusta las coordenadas de la bala basándote en el desplazamiento del mapa
        int xVisible = (int) x - desplazamientoX;
        int yVisible = (int) y - desplazamientoY;

        // Dibuja la bala ajustada al sistema de coordenadas visible
        g.setColor(Color.YELLOW);
        g.fillOval(xVisible - tamano / 2, yVisible - tamano / 2, tamano, tamano);
    }

}