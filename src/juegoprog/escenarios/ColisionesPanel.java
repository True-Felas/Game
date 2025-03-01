package juegoprog.escenarios;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para gestionar las colisiones en el juego.
 * De momento, solo es un panel vacío para evitar errores al compilar.
 */
public class ColisionesPanel extends JPanel {

    public ColisionesPanel() {
        setOpaque(false); // 🔹 Hacemos que el panel sea invisible (de momento)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 🔹 De momento, no pintamos nada aquí. Luego implementaremos la detección de colisiones.
    }
}
