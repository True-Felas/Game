package juegoprog.elementos;

import juegoprog.audio.GestorSonidos;
import juegoprog.escenarios.ColisionesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Representa un enemigo en el juego.
 * Puede moverse aleatoriamente por el mapa o perseguir al jugador cuando se acerca.
 * Tiene vida y puede ser eliminado por las balas del jugador.
 */
public class Enemigo {

    /**
     * Posición X del enemigo en el escenario (coordenadas globales).
     * Se actualiza conforme se mueve.
     */
    private double x;

    /**
     * Posición Y del enemigo en el escenario (coordenadas globales).
     */
    private double y;

    /**
     * Dimensión del sprite base del enemigo (se dibuja como un cuadrado de lado 'tamano').
     */
    private final int tamano = 60;

    /**
     * Indica si el enemigo está activo (vivo) o ha sido eliminado (falso).
     */
    private boolean activo = true;

    /**
     * Cantidad de vida que posee el enemigo. Cuando llega a 0, se desactiva.
     */
    private int vida = 3;

    /**
     * Instancia de Random para generar comportamientos y destinos aleatorios.
     */
    private final Random random = new Random();

    // =========================================================================
    // MOVIMIENTO / IA
    // =========================================================================

    /**
     * Destino X hacia el que el enemigo se moverá aleatoriamente.
     */
    private double objetivoX;

    /**
     * Destino Y hacia el que el enemigo se moverá aleatoriamente.
     */
    private double objetivoY;

    /**
     * Contador decreciente que determina cada cuántos frames cambia de destino aleatorio.
     */
    private int tiempoCambioDireccion = 0;

    /**
     * Cuenta el número de veces que el enemigo ha intentado moverse y no ha podido
     * (por colisiones o atascos).
     * Si supera cierto límite, fuerza un cambio de destino.
     */
    private int intentosMoverse = 0;

    // =========================================================================
    // APARIENCIA / ROTACIÓN / SONIDO
    // =========================================================================

    /**
     * Imagen animada (GIF) o estática que representa al enemigo.
     */
    private final Image imagenEnemigo;

    /**
     * Ángulo de rotación (en radianes) que el enemigo adopta para mirar
     * hacia el sentido de su desplazamiento.
     */
    private double anguloRotacion = 0;

    /**
     * Referencia al gestor de sonidos para reproducir alertas, etc.
     */
    private final GestorSonidos gestorSonidos;

    // =========================================================================
    // PERSECUCIÓN / ALERTAS
    // =========================================================================

    /**
     * Indica si este enemigo ya emitió alguna alerta (grito) al detectar al jugador.
     */
    private boolean yaEmitioAlerta = false;

    /**
     * Indica si el enemigo ya estaba persiguiendo al jugador en frames anteriores.
     */
    private boolean estabaPersiguiendo = false;

    /**
     * Retraso aleatorio para el momento en que puede "gritar"
     * (evita que el enemigo grite nada más empezar).
     */
    private int delayAlerta = new Random().nextInt(200) + 100;

    /**
     * Variable estática para forzar un intervalo mínimo entre gritos globales,
     * aunque haya varios enemigos.
     */
    private static long ultimoGrito = 0;

    // =========================================================================
    // 1. CONSTRUCTOR
    // =========================================================================

    /** Crea un nuevo enemigo con una posición inicial y carga su sprite.
     *
     * @param gestorSonidos  Referencia al gestor de sonidos para reproducir efectos.
     * @param xInicial       Posición X inicial.
     * @param yInicial       Posición Y inicial. */
    public Enemigo(GestorSonidos gestorSonidos, double xInicial, double yInicial) {
        this.gestorSonidos = gestorSonidos;
        this.x = xInicial;
        this.y = yInicial;

        // Calcular un primer destino aleatorio para moverse
        calcularDestinoAleatorio();

        // Carga de la imagen del enemigo (GIF)
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/resources/personaje/enemigo_cuchillo.gif")));
        this.imagenEnemigo = icon.getImage();
    }

    // =========================================================================
    // 2. MOVIMIENTO GENERAL
    // =========================================================================

    /** Determina cómo se mueve el enemigo en cada frame.
     * Si el jugador está cerca, lo persigue. De lo contrario, vaga aleatoriamente.
     *
     * @param objetivoXJugador  Posición X del jugador.
     * @param objetivoYJugador  Posición Y del jugador.
     * @param colisiones        Referencia al panel de colisiones para detectar obstáculos.
     * @param desplazamientoX   Desplazamiento de la cámara en X.
     * @param desplazamientoY   Desplazamiento de la cámara en Y. */

    public void moverHacia(double objetivoXJugador, double objetivoYJugador,
                           ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {

        if (!activo) return;  // Si el enemigo está inactivo, no hace nada

        double distanciaJugador = Math.hypot(objetivoXJugador - x, objetivoYJugador - y);

        // Determina si debe perseguir al jugador o moverse aleatoriamente
        if (distanciaJugador < 250) {
            perseguirJugador(objetivoXJugador, objetivoYJugador, colisiones, desplazamientoX, desplazamientoY);
        } else {
            moverAleatoriamente(colisiones, desplazamientoX, desplazamientoY);
        }
    }

    // =========================================================================
    // 3. PERSECUCIÓN DEL JUGADOR
    // =========================================================================

    /** Mueve al enemigo hacia la posición del jugador y reproduce sonidos de alerta
     * de forma ocasional.
     *
     * @param objetivoXJugador  Posición X del jugador.
     * @param objetivoYJugador  Posición Y del jugador.
     * @param colisiones        Mapa de colisiones.
     * @param desplazamientoX   Desplazamiento de la cámara X.
     * @param desplazamientoY   Desplazamiento de la cámara Y. */

    private void perseguirJugador(double objetivoXJugador, double objetivoYJugador,
                                  ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {

        // Se mueve hacia el jugador con cierta velocidad
        double velocidadPersecucion = 4;
        moverHaciaDestino(objetivoXJugador, objetivoYJugador, velocidadPersecucion,
                colisiones, desplazamientoX, desplazamientoY);

        // Reglas para soltar un "grito" de alerta
        if (!yaEmitioAlerta
                && --delayAlerta <= 0
                && new Random().nextInt(50) == 0
                && System.currentTimeMillis() - ultimoGrito > 5000) {

            // Seleccionar uno de dos sonidos de alerta
            String sonidoAlerta = new Random().nextBoolean()
                    ? "/audio/NoirAlertA.wav"
                    : "/audio/NoirAlertB.wav";
            gestorSonidos.reproducirEfecto(sonidoAlerta);

            yaEmitioAlerta = true;
            ultimoGrito = System.currentTimeMillis();
        }

        estabaPersiguiendo = true;
    }

    // =========================================================================
    // 4. MOVIMIENTO ALEATORIO
    // =========================================================================

    /** Mueve al enemigo por el escenario con un patrón aleatorio.
     * Cada cierto tiempo cambia de destino para simular patrulla errática.
     *
     * @param colisiones       Referencia para chequear obstáculos.
     * @param desplazamientoX  Offset de la cámara en X.
     * @param desplazamientoY  Offset de la cámara en Y. */

    private void moverAleatoriamente(ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {
        // Si se agotó el tiempo o el enemigo está atascado, busca un nuevo destino
        if (tiempoCambioDireccion <= 0 || intentosMoverse >= 5) {
            calcularDestinoAleatorio();
            tiempoCambioDireccion = 200;  // Reinicia el temporizador para cambiar de dirección
            intentosMoverse = 0;
        }

        // Intentar avanzar hacia el destino actual
        double velocidadBase = 1;
        boolean pudoMoverse = moverHaciaDestino(
                objetivoX, objetivoY, velocidadBase,
                colisiones, desplazamientoX, desplazamientoY
        );

        if (!pudoMoverse) {
            intentosMoverse++;
        }

        tiempoCambioDireccion--;
    }

    // =========================================================================
    // 5. MÉTODO GENÉRICO PARA MOVER HACIA UN DESTINO
    // =========================================================================

    /** Intenta mover al enemigo hacia un destino (destinoX, destinoY), teniendo en cuenta colisiones.
     *
     * @param destinoX        Coordenada X del destino.
     * @param destinoY        Coordenada Y del destino.
     * @param velocidad       Velocidad a la que se mueve el enemigo.
     * @param colisiones      Panel de colisiones para detectar choques.
     * @param desplazamientoX Offset de la cámara en X.
     * @param desplazamientoY Offset de la cámara en Y.
     * @return true si logró moverse, false si tuvo colisión y no pudo avanzar. */

    private boolean moverHaciaDestino(double destinoX, double destinoY, double velocidad,
                                      ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {

        double deltaX = destinoX - x;
        double deltaY = destinoY - y;
        double distancia = Math.hypot(deltaX, deltaY);

        // Si ya llegó al destino (cerca)
        if (distancia < 2) {
            return true;
        }

        // Calcular el vector de movimiento normalizado
        double factor = velocidad / distancia;
        double nuevoX = x + deltaX * factor;
        double nuevoY = y + deltaY * factor;

        // Actualizar ángulo de rotación en función de la dirección
        anguloRotacion = Math.atan2(deltaY, deltaX);

        // Chequear colisiones en la nueva posición
        if (!colisiones.hayColision((int) (nuevoX - desplazamientoX), (int) (nuevoY - desplazamientoY))) {
            x = nuevoX;
            y = nuevoY;
            return true;
        }

        // Si colisiona, probar un ajuste mínimo aleatorio (para "esquivar")
        double ajusteX = random.nextInt(3) - 1;
        double ajusteY = random.nextInt(3) - 1;
        nuevoX = x + ajusteX * velocidad;
        nuevoY = y + ajusteY * velocidad;

        if (!colisiones.hayColision((int) (nuevoX - desplazamientoX), (int) (nuevoY - desplazamientoY))) {
            x = nuevoX;
            y = nuevoY;
            return true;
        }

        // Ni siquiera el ajuste funcionó
        return false;
    }

    // =========================================================================
    // 6. DESTINO ALEATORIO
    // =========================================================================

    /** Escoge una nueva posición (objetivoX, objetivoY) en un rango amplio del mapa
     * para que el enemigo camine hacia allí. */

    private void calcularDestinoAleatorio() {
        objetivoX = random.nextInt(4000);
        objetivoY = random.nextInt(4000);
    }

    // =========================================================================
    // 7. DIBUJADO
    // =========================================================================

    /** Dibuja al enemigo en el mapa, rotado hacia su dirección de movimiento.
     * También muestra su vida sobre la cabeza del sprite.
     *
     * @param g               objeto Graphics para dibujar.
     * @param desplazamientoX desplazamiento (offset) actual en X de la cámara.
     * @param desplazamientoY desplazamiento (offset) actual en Y de la cámara. */

    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY) {
        if (!activo) {
            return;
        }

        int xVisible = (int) x - desplazamientoX;
        int yVisible = (int) y - desplazamientoY;

        // Crear un Graphics2D temporal para hacer la rotación
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(xVisible, yVisible);
        g2d.rotate(anguloRotacion);

        // Dibujar el sprite en el centro (ajustando la mitad de su tamaño)
        g2d.drawImage(imagenEnemigo, -tamano / 2, -tamano / 2, tamano, tamano, null);

        // Liberar el Graphics2D
        g2d.dispose();

        // Dibujar la vida del enemigo encima
        g.setColor(Color.WHITE);
        g.drawString("Vida: " + getVida(), xVisible - 20, yVisible - tamano / 2 - 5);
    }

    // =========================================================================
    // 8. DETECCIÓN DE BALAS / DAÑO
    // =========================================================================

    /** Comprueba si las coordenadas de una bala (balaX, balaY)
     * colisionan con el 'cuadrado' del enemigo.
     *
     * @param balaX coordenada X de la bala.
     * @param balaY coordenada Y de la bala.
     * @return true si el disparo alcanzó al enemigo, false en caso contrario. */

    public boolean colisionaCon(double balaX, double balaY) {
        return balaX >= x - (double) tamano / 2 && balaX <= x + (double) tamano / 2 &&
                balaY >= y - (double) tamano / 2 && balaY <= y + (double) tamano / 2;
    }

    /** Reduce en 1 la vida del enemigo. Si llega a 0, pasa a inactivo (muerto). */
    public void recibirDano() {
        vida--;
        if (vida <= 0) {
            activo = false;
        }
    }

    // =========================================================================
    // 9. GETTERS / SETTERS Y OTROS
    // =========================================================================

    /** Indica si el enemigo está aún con vida.
     * @return true si está activo, false si ya fue eliminado. */
    public boolean isActivo() {
        return activo;
    }

    /** Desactiva al enemigo inmediatamente. */
    public void desactivar() {
        activo = false;
    }

    /** @return la coordenada X actual del enemigo en el mapa. */
    public double getX() {
        return x;
    }

    /** @return la coordenada Y actual del enemigo en el mapa. */
    public double getY() {
        return y;
    }

    /** @return la vida restante del enemigo. */
    public int getVida() {
        return vida;
    }
}
