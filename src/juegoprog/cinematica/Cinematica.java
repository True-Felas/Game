package juegoprog.cinematica;

import juegoprog.graficos.Pantalla;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Cinematica extends JPanel implements ActionListener {
    private final List<Image> imagenes = new ArrayList<>(); // Lista de imágenes
    private int indiceActual = 0; // Índice de la imagen actual
    private float alpha = 0f; // Control de opacidad (fade-in)
    private float escala = 1.0f; // Control de zoom
    private final Timer timer; // Timer para actualizar la animación
    private final Timer cambioImagenTimer; // Timer para cambiar de imagen
    private final Pantalla ventana; // Referencia a la ventana principal

    /** Constructor de la Cinemática
     * "ventana" JFrame principal donde se ejecuta la animación */

    public Cinematica(Pantalla ventana) {
        this.ventana = ventana;
        setOpaque(true);
        setBackground(Color.BLACK);
        setSize(ventana.getSize());
        setVisible(true);
        requestFocusInWindow();

        cargarImagenes();

        // 🔹 Reproducir música usando GestorMusica
        ventana.getGestorMusica().reproducirMusica("/resources/audio/pruebacine.wav");


        timer = new Timer(40, this);
        cambioImagenTimer = new Timer(20000, e -> siguienteImagen());
        timer.start();
        cambioImagenTimer.start();
    }


    /** Cargamos las imágenes de la cinemática desde "resources". */

    private void cargarImagenes() {
        String[] archivos = {"/cinematicas/imagen1.jpg", "/cinematicas/imagen2.jpg", "/cinematicas/imagen3.jpg"};
        for (String archivo : archivos) {
            try {
                java.net.URL imgURL = getClass().getResource(archivo);
                if (imgURL != null) imagenes.add(ImageIO.read(imgURL));
                else System.err.println("❌ ERROR: No se encontró la imagen -> " + archivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Cambia a la siguiente imagen o finaliza la cinemática. */

    private void siguienteImagen() {
        if (++indiceActual >= imagenes.size()) terminarCinematica();
        else alpha = 0f; // Reinicia el efecto de fade-in
    }

    /** Finaliza la cinemática y carga el juego. */

    private void terminarCinematica() {
        timer.stop();
        cambioImagenTimer.stop();

        ventana.cambiarPantalla("JUEGO");
        SwingUtilities.invokeLater(() -> ventana.getMovimiento().requestFocusInWindow());
    }


    /** Maneja la animación de fade-in y zoom */
    @Override
    public void actionPerformed(ActionEvent e) {
        alpha = Math.min(alpha + 0.01f, 1f);
        escala += 0.0002f;
        repaint();
    }

    /** Renderiza la imagen actual con efectos de fade-in y zoom. */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!imagenes.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int imgWidth = imagenes.get(indiceActual).getWidth(this);
            int imgHeight = imagenes.get(indiceActual).getHeight(this);
            int drawX = (getWidth() - (int) (imgWidth * escala)) / 2;
            int drawY = (getHeight() - (int) (imgHeight * escala)) / 2;
            g2d.drawImage(imagenes.get(indiceActual), drawX, drawY, (int) (imgWidth * escala), (int) (imgHeight * escala), this);
        }
    }
}
