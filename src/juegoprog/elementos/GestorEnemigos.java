package juegoprog.elementos;

import juegoprog.audio.GestorSonidos;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.graficos.Pantalla;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GestorEnemigos {
    private static final List<Enemigo> enemigos = new ArrayList<>(); // Lista de enemigos activos
    private static final int MAX_ENEMIGOS = 20; // Máximo de enemigos activos
    private final Random random = new Random(); // Generador de números aleatorios

    // Coordenadas de los puntos de respawn existentes
    private final int[][] puntosRespawn = {
            {750, 420},       // Esquina Superior Izquierda
            {3350, 679},      // Esquina Superior Derecha
            {840, 4285},      // Esquina Inferior Izquierda
            {3436, 3560}       //Esquina inferior derecha
    };

    /**
     * Actualiza la posición y el estado de todos los enemigos activos y genera nuevos
     * enemigos en puntos de respawn aleatorios hasta el máximo definido.
     *
     * @param objetivoX       Coordenada X del personaje principal.
     * @param objetivoY       Coordenada Y del personaje principal.
     * @param colisiones      Referencia al sistema de colisiones.
     * @param desplazamientoX Desplazamiento actual en el eje X del mapa.
     * @param desplazamientoY Desplazamiento actual en el eje Y del mapa.
     */
    public void actualizar(double objetivoX, double objetivoY, ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {

        // ─────────────────────────────────────────────────────────────────
        // NUEVO: Si la pantalla indica que hay cinemática, abortamos
        // ─────────────────────────────────────────────────────────────────
        if (pantalla != null && pantalla.isEnCinematica()) {
            return;
        }

        Iterator<Enemigo> iterator = enemigos.iterator(); // Usamos un iterador para manejar la lista

        // Actualizar enemigos activos
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            if (enemigo.isActivo()) {
                enemigo.moverHacia(objetivoX, objetivoY, colisiones, desplazamientoX, desplazamientoY);
            } else {
                // Si el enemigo no está activo (muerto), lo eliminamos de la lista
                iterator.remove();
            }
        }

        // Generar nuevos enemigos para alcanzar el máximo permitido
        while (enemigos.size() < MAX_ENEMIGOS) {
            int[] respawn = puntosRespawn[random.nextInt(puntosRespawn.length)]; // Elegir punto de respawn
            Enemigo nuevoEnemigo = new Enemigo(gestorSonidos, respawn[0], respawn[1]); // 🔹 Ahora pasamos gestorSonidos
            enemigos.add(nuevoEnemigo);
        }
    }

    /**
     * Verifica las colisiones entre los enemigos y las balas.
     *
     * @param gestorBalas Gestor de balas.
     */
    public void verificarColisiones(GestorBalas gestorBalas) {
        for (Enemigo enemigo : enemigos) {
            if (!enemigo.isActivo()) continue;

            gestorBalas.getBalas().forEach(bala -> {
                if (enemigo.colisionaCon(bala.getX(), bala.getY())) {
                    enemigo.recibirDano();
                    bala.desactivar();

                    // Si el enemigo muere, lo marcamos como inactivo y reproducimos un sonido aleatorio (o ninguno)
                    if (!enemigo.isActivo()) {
                        enemigo.desactivar();

                        // 🔹 Generar un número aleatorio entre 0 y 4 (5 opciones: 4 sonidos y 1 silencio)
                        int opcion = new Random().nextInt(5); // 0, 1, 2, 3 o 4

                        String sonidoMuerte = switch (opcion) {
                            case 0 -> "/audio/NoirDeathA.wav";
                            case 1 -> "/audio/NoirDeathB.wav";
                            case 2 -> "/audio/NoirDeathC.wav";
                            case 3 -> "/audio/NoirDeathD.wav";
                            default -> null; // 🔹 Caso 4: No reproducir sonido
                        };

                        // 🔹 Si no es null, reproducir sonido
                        if (sonidoMuerte != null) {
                            gestorSonidos.reproducirEfecto(sonidoMuerte);
                        }
                    }
                }
            });
        }
    }


    private final GestorSonidos gestorSonidos;

    // ─────────────────────────────────────────────────────────────────
    // NUEVO: Pantalla para comprobar isEnCinematica()
    // ─────────────────────────────────────────────────────────────────
    private Pantalla pantalla;

    public GestorEnemigos(GestorSonidos gestorSonidos) {
        this.gestorSonidos = gestorSonidos;
    }

    // ─────────────────────────────────────────────────────────────────
    // NUEVO: Setter para asignar la pantalla
    // ─────────────────────────────────────────────────────────────────
    public void setPantalla(Pantalla pantalla) {
        this.pantalla = pantalla;
    }

    /**
     * Dibuja a todos los enemigos activos en el contexto gráfico.
     *
     * @param g              Contexto gráfico.
     * @param desplazamientoX Desplazamiento en el eje X.
     * @param desplazamientoY Desplazamiento en el eje Y.
     */
    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY) {
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(g, desplazamientoX, desplazamientoY);
        }
    }

    /**
     * Devuelve si todos los enemigos están eliminados.
     *
     * @return Verdadero si no queda ningún enemigo activo.
     */
    public boolean enemigosEliminados() {
        return enemigos.stream().noneMatch(Enemigo::isActivo); // Usamos streams para verificar si queda algún enemigo activo
    }

    /**
     * Devuelve la lista de enemigos activos.
     *
     * @return Lista de enemigos activos.
     */
    public static List<Enemigo> getEnemigos() {
        return new ArrayList<>(enemigos); // Devuelve una copia de la lista para evitar modificaciones externas
    }
}
