package juegoprog.escenarios;

import javax.swing.*;
import java.awt.*;

public abstract class BaseEscenario extends JPanel { // 🔹 Ahora hereda de JPanel
    protected int ancho;
    protected int alto;

    public BaseEscenario(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        setLayout(null); // 🔹 Permite posicionar elementos libremente en el panel
    }

    @Override
    protected void paintComponent(Graphics g) {  // ✅ Método correcto para dibujar en JPanel
        super.paintComponent(g);
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}
