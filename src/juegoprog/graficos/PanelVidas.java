package juegoprog.graficos;

import javax.swing.*;
import java.awt.*;

public class PanelVidas extends JPanel {

    private int vidas; // Número de vidas actuales

    // Constructor que inicializa el número de vidas
    public PanelVidas(int vidasIniciales) {
        this.vidas = vidasIniciales;
        setPreferredSize(new Dimension(200, 50)); // Tamaño del panel
    }

    // Metodo para actualizar el número de vidas y redibujar el panel
    public void actualizarVidas(int nuevasVidas) {
        this.vidas = nuevasVidas;
    }

    // Metodo para dibujar los puntos rojos según el número de vidas
    @Override
    protected void paintComponent(Graphics g) {

        // Color para las vidas restantes (puntos rojos)
        g.setColor(Color.RED);

        // Dibujar tantos puntos como vidas tenga el personaje
        int radio = 15; // Tamaño de los círculos
        int espacio = 10; // Espaciado entre círculos
        for (int i = 0; i < vidas; i++) {
            int x = 10 + i * (radio + espacio); // Posición horizontal
            int y = (getHeight() - radio) / 2; // Centra verticalmente
            g.fillOval(x, y, radio, radio);
        }
    }
}