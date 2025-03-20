package juegoprog.elementos;

import juegoprog.audio.GestorSonidos;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.graficos.Pantalla;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GestorEnemigos {
    private static final CopyOnWriteArrayList<Enemigo> enemigos = new CopyOnWriteArrayList<>(); // Ya es threadsafe
    private static final int MAX_ENEMIGOS = 15; // Máximo número de enemigos simultáneos
    private final Random random = new Random();

    // Coordenadas de los puntos de respawn existentes
    private final int[][] puntosRespawn = {
            {3350, 679},      // Esquina Superior Derecha
            {840, 4285},      // Esquina Inferior Izquierda
            {3436, 3560}      // Esquina Inferior Derecha
    };

    private final GestorSonidos gestorSonidos;

    // NUEVO: Pantalla para comprobar si hay cinemática
    private Pantalla pantalla;

    public GestorEnemigos(GestorSonidos gestorSonidos) {
        this.gestorSonidos = gestorSonidos;
    }

    // ------ Establecer pantalla ------
    public void setPantalla(Pantalla pantalla) {
        this.pantalla = pantalla;
    }

    /**
     * Actualiza la posición y el estado de todos los enemigos activos,
     * y genera nuevos enemigos en puntos de respawn aleatorios hasta el máximo definido.
     *
     * @param objetivoX       Coordenada X del personaje principal.
     * @param objetivoY       Coordenada Y del personaje principal.
     * @param colisiones      Referencia al sistema de colisiones.
     * @param desplazamientoX Desplazamiento actual en el eje X del mapa.
     * @param desplazamientoY Desplazamiento actual en el eje Y del mapa.
     */
    public void actualizar(double objetivoX, double objetivoY, ColisionesPanel colisiones,
                           int desplazamientoX, int desplazamientoY) {
        if (pantalla != null && pantalla.isEnCinematica()) {
            return;
        }

        // Mover a enemigos activos
        enemigos.forEach(enemigo -> {
            if (enemigo.isActivo()) {
                enemigo.moverHacia(objetivoX, objetivoY, colisiones, desplazamientoX, desplazamientoY);
            }
        });

        // Eliminar enemigos inactivos de forma segura y reproducir sonido de muerte
        enemigos.removeIf(enemigo -> {
            if (!enemigo.isActivo()) {
                reproducirSonidoMuerte(); // Reproducir sonido cuando un enemigo se elimina
                return true;
            }
            return false;
        });

        // Generar nuevos enemigos si el total está por debajo del máximo permitido
        while (enemigos.size() < MAX_ENEMIGOS) {
            int[] respawn = puntosRespawn[random.nextInt(puntosRespawn.length)];
            enemigos.add(new Enemigo(gestorSonidos, respawn[0], respawn[1]));
        }
    }



    /**
     * Verifica las colisiones entre los enemigos y las balas.
     *
     * @param gestorBalas Gestor de balas.
     */
    public void verificarColisiones(GestorBalas gestorBalas) {
        enemigos.forEach(enemigo -> {
            gestorBalas.getBalas().forEach(bala -> {
                if (enemigo.colisionaCon(bala.getX(), bala.getY())) {
                    enemigo.recibirDano();
                    bala.desactivar();
                }
            });
        });

        // Eliminar enemigos inactivos de forma segura y reproducir sonido de muerte
        enemigos.removeIf(enemigo -> {
            if (!enemigo.isActivo()) {
                reproducirSonidoMuerte(); // Reproducir sonido cuando un enemigo se elimina
                return true;
            }
            return false;
        });
    }


    /**
     * Dibuja a todos los enemigos activos en el contexto gráfico.
     *
     * @param g               Contexto gráfico.
     * @param desplazamientoX Desplazamiento actual en el eje X del mapa.
     * @param desplazamientoY Desplazamiento actual en el eje Y del mapa.
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
            return enemigos.stream().noneMatch(Enemigo::isActivo);
    }


    /**
     * Devuelve una copia de la lista de enemigos activos.
     *
     * @return Lista de enemigos activos.
     */
    public static CopyOnWriteArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }

    // ------ Metodo para reproducir un sonido de muerte aleatorio ------
    private void reproducirSonidoMuerte() {
        // Selección aleatoria entre 5 opciones (4 sonidos + silencio)
        int opcion = new Random().nextInt(5);
        String sonidoMuerte = switch (opcion) {
            case 0 -> "/audio/NoirDeathA.wav";
            case 1 -> "/audio/NoirDeathB.wav";
            case 2 -> "/audio/NoirDeathC.wav";
            case 3 -> "/audio/NoirDeathD.wav";
            default -> null;
        };

        // Si el sonido no es nulo, reproducimos el efecto
        if (sonidoMuerte != null) {
            gestorSonidos.reproducirEfecto(sonidoMuerte);
        }
    }
}