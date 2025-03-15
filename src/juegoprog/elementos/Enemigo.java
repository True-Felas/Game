package juegoprog.elementos;

import juegoprog.audio.GestorSonidos;
import juegoprog.escenarios.ColisionesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Enemigo {
    private double x, y;             // Posición actual del enemigo
    private final int tamano = 60;   // Tamaño del cuadrado que representa al enemigo
    private boolean activo = true;   // Indica si está activo (vivo) o eliminado
    private int vida = 3;            // Vida del enemigo

    private final Random random = new Random();

    // Movimiento aleatorio
    private double objetivoX;        // Destino X al que se moverá aleatoriamente
    private double objetivoY;        // Destino Y al que se moverá aleatoriamente
    private int tiempoCambioDireccion = 0; // Contador que determina cuándo cambiar de destino aleatorio
    private int intentosMoverse = 0; // Recuento de intentos por moverse sin éxito (atascos)

    // Imagen del enemigo
    private final Image imagenEnemigo;

    // Ángulo de rotación para que el enemigo mire hacia donde se mueve
    private double anguloRotacion = 0;

    private final GestorSonidos gestorSonidos;


    /**
     * Constructor del enemigo.
     *
     * @param xInicial Posición inicial X del enemigo.
     * @param yInicial Posición inicial Y del enemigo.
     */
    public Enemigo(GestorSonidos gestorSonidos, double xInicial, double yInicial) {
        this.gestorSonidos = gestorSonidos;
        this.x = xInicial;
        this.y = yInicial;
        calcularDestinoAleatorio(); // Calcula un primer destino aleatorio inicial
        // Cargar la imagen del enemigo desde la ruta especificada
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/personaje/enemigo_cuchillo.gif")));
        this.imagenEnemigo = icon.getImage();
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

        if (distanciaJugador < 250) {
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
    private boolean yaEmitioAlerta = false; // 🔹 Evita que el enemigo grite más de una vez
    private boolean estabaPersiguiendo = false; // 🔹 Detecta si el enemigo acaba de cambiar de estado
    private int delayAlerta = new Random().nextInt(200) + 100; // 🔹 Retraso aleatorio mayor (100 a 300 ciclos)

    // 🔹 Variable estática para evitar que muchos enemigos griten seguidos
    private static long ultimoGrito = 0;

    private void perseguirJugador(double objetivoXJugador, double objetivoYJugador, ColisionesPanel colisiones,
                                  int desplazamientoX, int desplazamientoY) {
        // Movimiento hacia el jugador
        double velocidadPersecucion = 4;
        moverHaciaDestino(objetivoXJugador, objetivoYJugador, velocidadPersecucion, colisiones, desplazamientoX, desplazamientoY);

        // 🔹 Solo hacer sonido si:
        // - Nunca ha gritado antes (`yaEmitioAlerta == false`)
        // - Ha pasado un retraso aleatorio (`delayAlerta == 0`)
        // - Hay una probabilidad MUY baja (2% → `nextInt(50) == 0`)
        // - Ha pasado suficiente tiempo desde el último grito global (`System.currentTimeMillis() - ultimoGrito > 5000`)
        if (!yaEmitioAlerta && --delayAlerta <= 0
                && new Random().nextInt(50) == 0
                && System.currentTimeMillis() - ultimoGrito > 5000) {

            // 🔹 Seleccionar aleatoriamente entre dos sonidos
            String sonidoAlerta = new Random().nextBoolean() ? "/audio/NoirAlertA.wav" : "/audio/NoirAlertB.wav";
            gestorSonidos.reproducirEfecto(sonidoAlerta);

            // 🔹 Marcar este enemigo como ya alertado y actualizar el último grito global
            yaEmitioAlerta = true;
            ultimoGrito = System.currentTimeMillis();
        }

        // 🔹 Marcar que el enemigo ya está persiguiendo
        estabaPersiguiendo = true;
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
        // Velocidades
        double velocidadBase = 1;
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

        // Calcular el ángulo de rotación basado en la dirección del movimiento
        anguloRotacion = Math.atan2(deltaY, deltaX);

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

    /**
     * Dibuja al enemigo con la rotación adecuada.
     */
    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY) {
        if (!activo) return;

        int xVisible = (int) x - desplazamientoX;
        int yVisible = (int) y - desplazamientoY;

        // Convertir Graphics a Graphics2D para usar transformaciones
        Graphics2D g2d = (Graphics2D) g.create();

        // Aplicar la rotación al dibujar la imagen
        g2d.translate(xVisible, yVisible);
        g2d.rotate(anguloRotacion);
        g2d.drawImage(imagenEnemigo, -tamano / 2, -tamano / 2, tamano, tamano, null);
        g2d.dispose(); // Liberar recursos de Graphics2D

        // Dibuja la vida encima del enemigo
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