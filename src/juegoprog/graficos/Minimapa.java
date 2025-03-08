package juegoprog.graficos;

import juegoprog.jugador.Personaje;

import javax.swing.*;
import java.awt.*;

public class Minimapa extends JPanel {
    private final Image fondoMiniMapa; // Imagen del fondo del mapa (escalada)
    private final Personaje personaje; // Referencia al personaje para saber su posición
    private final int mapaAncho; // Ancho real del mapa
    private final int mapaAlto;  // Alto real del mapa

    private final int miniMapaAncho = 200; // Ancho del minimapa en la pantalla
    private final int miniMapaAlto = 200;  // Alto del minimapa en la pantalla

    public Minimapa(Image fondoMapa, Personaje personaje, int mapaAncho, int mapaAlto) {
        this.fondoMiniMapa = fondoMapa.getScaledInstance(miniMapaAncho, miniMapaAlto, Image.SCALE_SMOOTH);
        this.personaje = personaje;
        this.mapaAncho = mapaAncho;
        this.mapaAlto = mapaAlto;

        // Tamaño del minimapa
        setPreferredSize(new Dimension(miniMapaAncho, miniMapaAlto));
        setOpaque(false); // Hazlo transparente si hay que superponerlo.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibuja el fondo del minimapa
        g.drawImage(fondoMiniMapa, 0, 0, null);

        // Calcula la posición del personaje en el minimapa
        int personajeX = (int) ((personaje.getX() / (double) mapaAncho) * miniMapaAncho);
        int personajeY = (int) ((personaje.getY() / (double) mapaAlto) * miniMapaAlto);

        // Dibuja el marcador del personaje (un punto rojo)
        g.setColor(Color.RED);
        g.fillOval(personajeX - 3, personajeY - 3, 6, 6);
    }
}

