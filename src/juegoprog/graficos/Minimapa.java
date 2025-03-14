package juegoprog.graficos;

import juegoprog.elementos.Enemigo;
import juegoprog.jugador.Personaje;
import juegoprog.elementos.GestorEnemigos;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Minimapa extends JPanel {
    private final Image fondoMiniMapa; // Imagen del fondo del minimapa
    private final Personaje personaje; // Referencia al personaje
    private final int mapaAncho; // Ancho real del mapa
    private final int mapaAlto;  // Alto real del mapa

    private final int miniMapaAncho = 200; // Ancho fijo del minimapa
    private final int miniMapaAlto = 215;  // Alto fijo del minimapa

    public Minimapa(Personaje personaje, int mapaAncho, int mapaAlto) {
        this.fondoMiniMapa = new ImageIcon(Objects.requireNonNull(getClass().getResource("/graficos/minimapaDS.png"))).getImage();
        this.personaje = personaje;
        // Referencia directa al gestor de enemigos
        this.mapaAncho = mapaAncho;  // Guardamos el ancho real del mapa
        this.mapaAlto = mapaAlto;    // Guardamos el alto real del mapa

        // Tamaño del minimapa
        setPreferredSize(new Dimension(miniMapaAncho, miniMapaAlto));
        setOpaque(false); // Permite transparencia
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibuja el fondo del minimapa
        g.drawImage(fondoMiniMapa, 0, 0, this);

        // Calcula la posición del personaje en el minimapa con coordenadas escaladas
        int personajeX = (int) ((personaje.getX() / (double) mapaAncho) * miniMapaAncho);
        int personajeY = (int) ((personaje.getY() / (double) mapaAlto) * miniMapaAlto);

        // Dibuja el marcador del personaje (punto rojo)
        g.setColor(Color.RED);
        g.fillOval(personajeX - 3, personajeY - 3, 6, 6);

        // Dibuja los enemigos (puntos azules) obtenidos dinámicamente del gestor de enemigos
        List<Enemigo> enemigos = GestorEnemigos.getEnemigos();
        g.setColor(Color.GREEN); // Color de los enemigos
        for (Enemigo enemigo : enemigos) {
            if (enemigo.isActivo()) {
                // Calculamos la posición del enemigo en el minimapa
                int enemigoX = (int) ((enemigo.getX() / (double) mapaAncho) * miniMapaAncho);
                int enemigoY = (int) ((enemigo.getY() / (double) mapaAlto) * miniMapaAlto);

                // Dibuja el enemigo como un punto azul
                g.fillOval(enemigoX - 2, enemigoY - 2, 4, 4);
            }
        }
    }
}