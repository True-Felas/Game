package juegoprog.graficos;

import juegoprog.jugador.Personaje;

import javax.swing.*;
import java.awt.*;

public class Minimapa extends JPanel {
    private final Image fondoMiniMapa; // Imagen del fondo del minimapa
    private final Personaje personaje; // Referencia al personaje
    private final int mapaAncho; // Ancho real del mapa
    private final int mapaAlto;  // Alto real del mapa

    private final int miniMapaAncho = 200; // Ancho fijo del minimapa
    private final int miniMapaAlto = 215;  // Alto fijo del minimapa

    public Minimapa(Personaje personaje, int mapaAncho, int mapaAlto) {
        this.fondoMiniMapa = new ImageIcon(getClass().getResource("/graficos/minimapaDS.png")).getImage();
        this.personaje = personaje;
        this.mapaAncho = mapaAncho;  // Guardamos el ancho real del mapa
        this.mapaAlto = mapaAlto;    // Guardamos el alto real del mapa

        // Tamaño del minimapa
        setPreferredSize(new Dimension(miniMapaAncho, miniMapaAlto));
        setOpaque(false); // Hace que el fondo sea transparente si es necesario.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibuja el fondo del minimapa
        g.drawImage(fondoMiniMapa, 0, 0, this);

        // Calcula la posición del personaje en el minimapa con los valores fijos
        int personajeX = (int) ((personaje.getX() / (double) mapaAncho) * miniMapaAncho);
        int personajeY = (int) ((personaje.getY() / (double) mapaAlto) * miniMapaAlto);

        // Dibuja el marcador del personaje (un punto rojo)
        g.setColor(Color.RED);
        g.fillOval(personajeX - 3, personajeY - 3, 6, 6);
    }
}