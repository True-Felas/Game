package juegoprog.efectos;

import juegoprog.jugador.Personaje;
import juegoprog.controles.Movimiento;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class EfectoOscuridadSigilo extends JPanel {

    private final Personaje personaje;
    private final Movimiento movimiento;
    private int radioVisible;

    public EfectoOscuridadSigilo(Personaje personaje, Movimiento movimiento) {
        this.personaje = personaje;
        this.movimiento = movimiento;
        this.radioVisible = 150; // Ajusta a tu gusto
        setOpaque(false);
    }

    public void setRadioVisible(int radioVisible) {
        this.radioVisible = radioVisible;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // 1) Pintamos toda la pantalla con un velo negro semitransparente
        g2d.setColor(new Color(0, 0, 0, 200)); // Ajusta alpha a tu gusto
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2) DST_OUT para sustraer lo que dibujemos a continuación
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));

        // 3) Coordenadas del personaje en pantalla
        int xPantalla = personaje.getX() - movimiento.getDesplazamientoX();
        int yPantalla = personaje.getY() - movimiento.getDesplazamientoY();

        // 4) RadialGradientPaint con centro “blanco opaco” y borde “blanco transparente”
        //    => en DST_OUT, lo opaco “corta” completamente la niebla, lo transparente no la toca
        float[] distancias = {0f, 0.6f, 1f};
        Color[] colores = {
                new Color(1f, 1f, 1f, 1f),   // centro => recorta la niebla al 100%
                new Color(1f, 1f, 1f, 0.4f), // transición => recorta parcialmente
                new Color(1f, 1f, 1f, 0f)    // borde => no recorta (queda oscuro)
        };

        RadialGradientPaint gradiente = new RadialGradientPaint(
                new Point2D.Float(xPantalla, yPantalla),
                radioVisible,
                distancias,
                colores
        );
        g2d.setPaint(gradiente);

        // 5) Dibujar el óvalo para “abrir hueco” en la niebla
        g2d.fillOval(
                xPantalla - radioVisible,
                yPantalla - radioVisible,
                radioVisible * 2,
                radioVisible * 2
        );

        g2d.dispose();
    }
}
