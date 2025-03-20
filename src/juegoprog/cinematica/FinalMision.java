package juegoprog.cinematica;

import juegoprog.graficos.Pantalla;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class FinalMision {
    private final Pantalla ventana;
    private boolean enFinal = false;
    private boolean cajaFuerteCompletada;
    private final Rectangle areaEscape = new Rectangle(1120, 1800, 50, 120);

    public FinalMision(Pantalla ventana) {
        this.ventana = ventana;
        this.cajaFuerteCompletada = cajaFuerteCompletada;
    }

    public void verificarEscape(int x, int y) {
        if (enFinal) return;

        if (areaEscape.contains(x, y)) {
            ventana.getMovimiento().setMostrarMensajePista(true);
            ventana.getMovimiento().agregarEventoEnter(() -> {
                ventana.getMovimiento().setMostrarMensajePista(false);
                if (cajaFuerteCompletada) {
                    mostrarFinal();
                } else {
                    JOptionPane.showMessageDialog(null, "Aún falta algo... No puedes irte sin los documentos.", "Algo no cuadra...", JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }

    private void mostrarFinal() {
        enFinal = true;
        JFrame finalVentana = new JFrame();
        finalVentana.setUndecorated(true);
        finalVentana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        finalVentana.setSize(ventana.getWidth(), ventana.getHeight());
        finalVentana.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/cinematicas/mision_completada.png"))));

        panel.add(label, BorderLayout.CENTER);
        JLabel texto = new JLabel("Pulsa ESCAPE para salir", SwingConstants.CENTER);
        texto.setFont(new Font("Arial", Font.BOLD, 24));
        texto.setForeground(Color.WHITE);
        panel.add(texto, BorderLayout.NORTH);

        finalVentana.add(panel);

        // 🔹 Asegurar que la música previa se detiene y reproducir la locución final
        ventana.getGestorSonidos().reproducirEfecto("/audio/NoirEscape.wav");

        finalVentana.setVisible(true);

        finalVentana.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cerrarJuego();
                    finalVentana.dispose();
                }
            }
        });
    }

    private void cerrarJuego() {
        ventana.getGestorMusica().detenerMusica();  // Detener música de fondo
        //ventana.getGestorSonidos().detenerSonido("/audio/NoirAreaAlarm.wav");
        ventana.getGestorSonidos().detenerTodosLosSonidos(); // Detener todos los sonidos en bucle
        ventana.getGestorSonidos().reproducirEfecto("/audio/NoirEscape.wav"); // Efecto de escape
        ventana.cambiarPantalla("MENU");

        // 🔹 REANUDAR MÚSICA DEL MENÚ
        ventana.getGestorMusica().reproducirMusica("/audio/Intro_NoirCity_Find Me Again.wav");
    }

    public void setCajaFuerteCompletada(boolean completada) {
        this.cajaFuerteCompletada = completada;
    }
}
