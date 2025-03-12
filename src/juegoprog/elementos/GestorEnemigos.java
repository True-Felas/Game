package juegoprog.elementos;

import juegoprog.escenarios.ColisionesPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GestorEnemigos {
    private static final List<Enemigo> enemigos = new ArrayList<>(); // Lista de enemigos
    public int enemigosPorOleada = 3; // Cantidad de enemigos por oleada

    /**
     * Genera una nueva oleada de enemigos.
     */
    public void generarOleada(int anchoEscenario, int altoEscenario) {
        Random random = new Random();

        for (int i = 0; i < enemigosPorOleada; i++) {
            // Generar un número aleatorio entre 1 y 3
            int esquina = random.nextInt(3) + 1; // Esquinas predefinidas: 1, 2, 3

            double x = 0, y = 0;

            // Asignar las coordenadas de la esquina correspondiente
            switch (esquina) {
                case 1: // Esquina Superior Izquierda
                    x = 750;
                    y = 420;
                    break;
                case 2: // Esquina Superior Derecha
                    x = 3350;
                    y = 679;
                    break;
                case 3: // Esquina Inferior Izquierda
                    x = 840;
                    y = 4285;
                    break;
            }

            // Crear un nuevo enemigo en la posición seleccionada
            Enemigo enemigo = new Enemigo(x, y, 1); // ID o nivel de ejemplo
            enemigos.add(enemigo);
        }
    }

    /**
     * Actualiza la posición y el estado de todos los enemigos activos.
     *
     * @param objetivoX Coordenada X del personaje principal.
     * @param objetivoY Coordenada Y del personaje principal.
     * @param colisiones Referencia al sistema de colisiones.
     * @param desplazamientoX Desplazamiento actual en el eje X del mapa.
     * @param desplazamientoY Desplazamiento actual en el eje Y del mapa.
     */
    public void actualizar(double objetivoX, double objetivoY, ColisionesPanel colisiones, int desplazamientoX, int desplazamientoY) {
        Iterator<Enemigo> iterator = enemigos.iterator(); // Usamos un iterador para manejar la lista

        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            if (enemigo.isActivo()) {
                enemigo.moverHacia(objetivoX, objetivoY, colisiones, desplazamientoX, desplazamientoY);
            } else {
                // Si el enemigo no está activo (muerto), lo eliminamos de la lista
                iterator.remove();
            }
        }
    }

    /**
     * Verifica si todos los enemigos actuales han sido eliminados.
     *
     * @return Verdadero si no queda ningún enemigo activo.
     */
    public boolean enemigosEliminados() {
        // Usamos streams para verificar si queda algún enemigo activo
        return enemigos.stream().noneMatch(Enemigo::isActivo);
    }

    /**
     * Verifica las colisiones entre los enemigos y las balas.
     *
     * @param gestorBalas Gestor de balas.
     */
    public void verificarColisiones(GestorBalas gestorBalas) {
        for (Enemigo enemigo : enemigos) {
            if (!enemigo.isActivo()) continue;

            // Verificamos cada bala activa contra cada enemigo
            gestorBalas.getBalas().forEach(bala -> {
                if (enemigo.colisionaCon(bala.getX(), bala.getY())) {
                    enemigo.recibirDano(); // Reducir vida del enemigo
                    bala.desactivar();    // Desactivar bala

                    // Si el enemigo muere, lo marcamos como inactivo
                    if (!enemigo.isActivo()) {
                        enemigo.desactivar(); // Método que puedes definir en la clase `Enemigo`
                    }
                }
            });
        }
    }

    /**
     * Dibuja a todos los enemigos activos.
     *
     * @param g Contexto gráfico.
     * @param desplazamientoX Desplazamiento en el eje X.
     * @param desplazamientoY Desplazamiento en el eje Y.
     */
    public void dibujar(Graphics g, int desplazamientoX, int desplazamientoY) {
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(g, desplazamientoX, desplazamientoY);
        }
    }

    /**
     * Devuelve la lista de enemigos activos.
     *
     * @return Lista de enemigos.
     */
    public static List<Enemigo> getEnemigos() {
        return new ArrayList<>(enemigos); // Devuelve una copia de la lista para evitar modificaciones externas
    }
}