package juegoprog.efectos;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Efecto de partículas flotantes (polvo, cenizas, etc.) para dar más ambiente.
 * Cada partícula tiene posición, velocidad y tamaño. Se actualizan en un Timer.
 */
public class EfectoParticulas extends JPanel {

    private static class Particula {
        float x, y;        // posición
        float vx, vy;      // velocidad
        float radio;       // radio de la partícula
        float alpha;       // opacidad
    }

    private Particula[] particulas;    // array de partículas
    private final Random random = new Random();

    private float densidad = 0.3f; // opacidad global de la capa de partículas

    // Constructor
    public EfectoParticulas(int ancho, int alto, int numParticulas) {
        setOpaque(false);
        setSize(ancho, alto); // tamaño del panel

        particulas = new Particula[numParticulas];

        // Inicializamos cada partícula en una posición aleatoria
        for (int i = 0; i < numParticulas; i++) {
            particulas[i] = crearParticula();
        }

        // Timer que actualiza las partículas ~30 FPS
        int delay = 33; // ms => ~30fps
        new Timer(delay, e -> {
            actualizarParticulas();
            repaint();
        }).start();
    }

    /** Crea una partícula con valores aleatorios de posición, velocidad, tamaño y alpha */
    private Particula crearParticula() {
        Particula p = new Particula();
        p.x = random.nextFloat() * getWidth();  // dentro del panel
        p.y = random.nextFloat() * getHeight();
        p.vx = (random.nextFloat() - 0.5f) * 1.0f; // velocidad horizontal suave
        p.vy = (random.nextFloat() - 0.5f) * 1.0f; // velocidad vertical suave
        p.radio = 2 + random.nextFloat() * 4;     // radio 2..6 px
        p.alpha = 0.1f + random.nextFloat() * 0.4f; // opacidad 0.3..1.0
        return p;
    }

    /** Actualiza cada partícula: mueve y comprueba si sale del panel */
    private void actualizarParticulas() {
        for (Particula p : particulas) {
            p.x += p.vx;
            p.y += p.vy;

            // Si se escapa de los bordes, la "teletransportamos" a una posición nueva
            if (p.x < 0 || p.x > getWidth() || p.y < 0 || p.y > getHeight()) {
                // Reiniciamos la partícula para que aparezca en otra zona
                Particula nueva = crearParticula();
                p.x = nueva.x;
                p.y = nueva.y;
                p.vx = nueva.vx;
                p.vy = nueva.vy;
                p.radio = nueva.radio;
                p.alpha = nueva.alpha;
            }
        }
    }

    /** Ajusta la opacidad global de esta capa de partículas (0..1) */
    public void setDensidad(float dens) {
        this.densidad = dens;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Pintamos todas las partículas como pequeños círculos
        Graphics2D g2d = (Graphics2D) g.create();

        // Fijamos una opacidad global sobre toda la capa (para atenuar más)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, densidad));

        // Dibujamos cada partícula
        for (Particula p : particulas) {
            // Color blanco semitransparente
            // Ponemos la alpha de la partícula multiplicada por la densidad global
            float finalAlpha = p.alpha;
            Color color = new Color(1f, 1f, 1f, finalAlpha);

            g2d.setColor(color);
            g2d.fillOval((int)(p.x - p.radio), (int)(p.y - p.radio),
                    (int)(p.radio*2), (int)(p.radio*2));
        }

        g2d.dispose();
    }
}
