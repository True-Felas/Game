package juegoprog.elementos;

import juegoprog.escenarios.ColisionesPanel;

import java.awt.*;

public class Enemigo {
    private double x, y;          // Posición actual del enemigo
    private final double velocidad;
    private boolean activo = true; // Activo o eliminado
    private int vida = 3;         // Nueva característica: vida del enemigo

    /**
     * Constructor del enemigo.
     *
     * @param xInicial Posición inicial X del enemigo
     * @param yInicial Posición inicial Y del enemigo
     * @param velocidad Velocidad a la que se mueve
     */
    public Enemigo(double xInicial, double yInicial, double velocidad) {
        this.x = xInicial;
        this.y = yInicial;
        this.velocidad = velocidad;
    }

    /**
     * Actualiza la posición del enemigo en dirección al personaje.
     *
     * @param objetivoX Coordenada X del personaje principal.
     * @param objetivoY Coordenada Y del personaje principal.
     */
    public void moverHacia(double objetivoX, double objetivoY, ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {
        if (!activo) return;

        double dx = objetivoX - x;
        double dy = objetivoY - y;

        // Normalizar el vector de dirección
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia != 0) {
            dx /= distancia;
            dy /= distancia;
        }

        // Proponer nuevas coordenadas
        double nuevaX = x + dx * velocidad;
        double nuevaY = y + dy * velocidad;

        // Ajustar las coordenadas según el desplazamiento
        int globalX = (int) nuevaX - desplazamientoX;
        int globalY = (int) nuevaY - desplazamientoY;

        // Verificar colisiones usando las coordenadas globales
        int hitbox = 15; // Tamaño de la hitbox del enemigo
        boolean colisionSuperiorIzquierda = colisiones.hayColision(globalX - hitbox, globalY - hitbox);
        boolean colisionSuperiorDerecha = colisiones.hayColision(globalX + hitbox, globalY - hitbox);
        boolean colisionInferiorIzquierda = colisiones.hayColision(globalX - hitbox, globalY + hitbox);
        boolean colisionInferiorDerecha = colisiones.hayColision(globalX + hitbox, globalY + hitbox);

        if (!colisionSuperiorIzquierda && !colisionSuperiorDerecha &&
                !colisionInferiorIzquierda && !colisionInferiorDerecha) {
            // Actualizar posición si no hay colisión
            x = nuevaX;
            y = nuevaY;
        }
    }



    /**
     * Renderiza al enemigo en pantalla.
     *
     * @param g Contexto gráfico
     */
    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY) {
        if (!activo) return;

        int xVisible = (int) x - desplazamientoX;
        int yVisible = (int) y - desplazamientoY;

        g.setColor(Color.RED); // Color del enemigo
        g.fillRect(xVisible - 15, yVisible - 15, 30, 30); // Rectángulo que representa al enemigo

        // Representar la vida del enemigo en texto sobre él
        g.setColor(Color.WHITE);
        g.drawString("Vida: " + vida, xVisible - 20, yVisible - 20);
    }

    /**
     * Verifica si el enemigo colisiona con una bala.
     *
     * @param balaX Coordenada X de la bala.
     * @param balaY Coordenada Y de la bala.
     * @return Verdadero si hay colisión.
     */
    public boolean colisionaCon(double balaX, double balaY) {
        int tamano = 30; // Tamaño del enemigo
        return activo && balaX >= x - (double) tamano / 2 && balaX <= x + (double) tamano / 2 &&
                balaY >= y - (double) tamano / 2 && balaY <= y + (double) tamano / 2;
    }

    /**
     * Reduce la vida del enemigo en 1.
     */
    public void recibirDano() {
        if (!activo) return;

        vida--;
        if (vida <= 0) {
            desactivar();
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        this.activo = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getVida() {
        return vida;
    }
}