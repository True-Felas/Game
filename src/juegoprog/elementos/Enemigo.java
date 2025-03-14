package juegoprog.elementos;

import juegoprog.escenarios.ColisionesPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Enemigo {
    private double x, y;             // Posición actual del enemigo
    private final int tamano = 50;   // Tamaño del cuadrado que representa al enemigo
    private boolean activo = true;   // Indica si está activo (vivo) o eliminado
    private int vida = 3;            // Vida del enemigo

    private final Random random = new Random();

    // Velocidades
    private final double velocidadBase = 2;
    private final double velocidadPersecucion = 4;

    // Movimiento aleatorio
    private double objetivoX;        // Destino X al que se moverá aleatoriamente
    private double objetivoY;        // Destino Y al que se moverá aleatoriamente
    private int tiempoCambioDireccion = 0; // Contador que determina cuándo cambiar de destino aleatorio
    private int intentosMoverse = 0; // Recuento de intentos por moverse sin éxito (atascos)

    /**
     * Constructor del enemigo.
     *
     * @param xInicial Posición inicial X del enemigo.
     * @param yInicial Posición inicial Y del enemigo.
     */
    /** Constructor del enemigo */
    public Enemigo(double xInicial, double yInicial, double velocidad) {
        this.x = xInicial;
        this.y = yInicial;
        cargarImagen(); // 🔹 Cargar la imagen del enemigo al crearlo
        calcularDestinoAleatorio(); // 🔹 Calcula un primer destino aleatorio inicial
    }


    /**
     * Mueve el enemigo en función de su estado actual.
     *
     * @param objetivoXJugador Coordenada X del jugador.
     * @param objetivoYJugador Coordenada Y del jugador.
     * @param colisiones       Sistema de colisiones.
     * @param desplazamientoX  Desplazamiento actual en el eje X.
     * @param desplazamientoY  Desplazamiento actual en el eje Y.
     */
    public void moverHacia(double objetivoXJugador, double objetivoYJugador, ColisionesPanel colisiones,
                           int desplazamientoX, int desplazamientoY) {
        if (!activo) return;

        double distanciaJugador = Math.hypot(objetivoXJugador - x, objetivoYJugador - y);

        if (distanciaJugador < 200) {
            // Si el jugador está cerca, perseguir
            perseguirJugador(objetivoXJugador, objetivoYJugador, colisiones, desplazamientoX, desplazamientoY);
        } else {
            // Si el jugador está lejos, moverse aleatoriamente
            moverAleatoriamente(colisiones, desplazamientoX, desplazamientoY);
        }
    }

    /**
     * Mueve al enemigo en dirección al jugador.
     */
    private void perseguirJugador(double objetivoXJugador, double objetivoYJugador, ColisionesPanel colisiones,
                                  int desplazamientoX, int desplazamientoY) {
        // Movimiento hacia el jugador
        moverHaciaDestino(objetivoXJugador, objetivoYJugador, velocidadPersecucion, colisiones, desplazamientoX, desplazamientoY);
    }

    /**
     * Realiza un movimiento aleatorio por el mapa.
     */
    private void moverAleatoriamente(ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {
        if (tiempoCambioDireccion <= 0 || intentosMoverse >= 5) {
            // Calcula un nuevo destino si es necesario
            calcularDestinoAleatorio();
            tiempoCambioDireccion = 200;
            intentosMoverse = 0; // Reinicia los intentos
        }

        // Intenta moverse hacia el destino actual
        if (!moverHaciaDestino(objetivoX, objetivoY, velocidadBase, colisiones, desplazamientoX, desplazamientoY)) {
            intentosMoverse++; // Incrementa el contador si no logra avanzar
        }

        tiempoCambioDireccion--; // Reduce el tiempo para cambiar de dirección
    }

    /**
     * Intenta mover al enemigo hacia un destino específico, ajustándose si hay colisiones.
     *
     * @param destinoX          Coordenada X del destino.
     * @param destinoY          Coordenada Y del destino.
     * @param velocidad         Velocidad a la que se moverá.
     * @param colisiones        Sistema de colisiones.
     * @param desplazamientoX   Desplazamiento en el eje X.
     * @param desplazamientoY   Desplazamiento en el eje Y.
     * @return Verdadero si logra moverse, falso si no puede avanzar (colisión).
     */
    private boolean moverHaciaDestino(double destinoX, double destinoY, double velocidad,
                                      ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {
        double deltaX = destinoX - x;
        double deltaY = destinoY - y;
        double distancia = Math.hypot(deltaX, deltaY);

        // Si ya está cerca del destino
        if (distancia < 2) {
            return true; // Llegó al destino
        }

        // Calcula el nuevo movimiento
        double factor = velocidad / distancia;
        double nuevoX = x + deltaX * factor;
        double nuevoY = y + deltaY * factor;

        // Revisar colisiones
        if (!colisiones.hayColision((int) (nuevoX - desplazamientoX), (int) (nuevoY - desplazamientoY))) {
            // Aplicar movimiento si no hay colisión
            x = nuevoX;
            y = nuevoY;
            return true;
        }

        // Sí hay colisión, intentar un ajuste
        double ajusteX = random.nextInt(3) - 1; // Movimiento aleatorio en eje X
        double ajusteY = random.nextInt(3) - 1; // Movimiento aleatorio en eje Y
        nuevoX = x + ajusteX * velocidad;
        nuevoY = y + ajusteY * velocidad;

        // Verificar si el ajuste resuelve el problema
        if (!colisiones.hayColision((int) (nuevoX - desplazamientoX), (int) (nuevoY - desplazamientoY))) {
            x = nuevoX;
            y = nuevoY;
            return true;
        }

        return false; // Si no logra moverse
    }

    /**
     * Calcula una nueva posición aleatoria como destino del enemigo.
     */
    private void calcularDestinoAleatorio() {
        objetivoX = random.nextInt(4000); // Rango del ancho del mapa
        objetivoY = random.nextInt(4000); // Rango del alto del mapa
    }
    // Variable para almacenar la imagen del enemigo
    private ImageIcon imagenEnemigo;

    /** Carga la imagen del enemigo desde resources (GIF animado) */
    /** Carga la imagen del enemigo desde resources y la escala */
    private void cargarImagen() {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/resources/personaje/enemigo_cuchillo.gif"));

        if (iconoOriginal.getIconWidth() > 0 && iconoOriginal.getIconHeight() > 0) {
            // Escalar la imagen manteniendo la animación del GIF
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            imagenEnemigo = new ImageIcon(imagenEscalada);
        } else {
            System.err.println("⚠ No se pudo cargar la imagen del enemigo.");
            imagenEnemigo = null;
        }
    }



    /**
     * Dibuja al enemigo como un cuadrado rojo con la vida mostrada sobre él.
     */

    /** Dibuja al enemigo en pantalla con su animación, rotado hacia el personaje */
    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY, double personajeX, double personajeY) {
        if (!activo) return;

        int xVisible = (int) x - desplazamientoX;
        int yVisible = (int) y - desplazamientoY;

        // Calcular el ángulo entre el enemigo y el personaje
        double angulo = Math.atan2(personajeY - y, personajeX - x);

        // Dibujar el enemigo con rotación
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(xVisible, yVisible);
        g2d.rotate(angulo, tamano / 2.0, tamano / 2.0); // 🔹 Rotar respecto al centro del sprite

        if (imagenEnemigo != null) {
            imagenEnemigo.paintIcon(null, g2d, -tamano / 2, -tamano / 2);
        } else {
            // Si la imagen no carga, se dibuja un cuadrado rojo
            g2d.setColor(Color.RED);
            g2d.fillRect(-tamano / 2, -tamano / 2, tamano, tamano);
        }

        g2d.dispose(); // 🔹 Liberar recursos gráficos

        // Dibujar la vida encima del enemigo
        g.setColor(Color.WHITE);
        g.drawString("Vida: " + getVida(), xVisible - 20, yVisible - tamano / 2 - 5);
    }




    public boolean colisionaCon(double balaX, double balaY) {
        return balaX >= x - (double) tamano / 2 && balaX <= x + (double) tamano / 2 &&
                balaY >= y - (double) tamano / 2 && balaY <= y + (double) tamano / 2;
    }

    public void recibirDano() {
        vida--;
        if (vida <= 0) {
            activo = false;
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        activo = false;
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