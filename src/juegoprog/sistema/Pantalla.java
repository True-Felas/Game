package juegoprog.sistema;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal de la ventana del juego.
 * Implementa CardLayout para gestionar los cambios de pantalla, como se indica en los apuntes de la profesora
 * sobre "Usos de CardLayout" (1.6. Usos de CardLayout.docx).
 */
public class Pantalla extends JFrame {
    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;
    private JLayeredPane capaJuego;
    private Movimiento movimiento;
    private EscenarioDistritoSombrio escenario;
    private ColisionesPanel colisiones;

    public Pantalla() {
        setTitle("Juego - Pantalla Principal");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 Se usa CardLayout para manejar múltiples pantallas en un solo JFrame.
        // 🔹 Esto sigue los apuntes sobre "Distribución de paneles y Layouts" (1.3. Distribución paneles_ Principales Layout.docx).
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);
        setContentPane(contenedorPrincipal);

        // 🔹 Menú Principal (primera pantalla al iniciar el juego)
        MenuPrincipal menu = new MenuPrincipal(this);
        contenedorPrincipal.add(menu, "MENU");

        // 🔹 Configuramos la pantalla de juego con JLayeredPane para manejar capas
        capaJuego = new JLayeredPane();
        capaJuego.setPreferredSize(new Dimension(1280, 720));

        // 🔹 Capa 1: Fondo del escenario
        escenario = new EscenarioDistritoSombrio();
        escenario.setBounds(0, 0, 3192, 4096);
        capaJuego.add(escenario, JLayeredPane.DEFAULT_LAYER);
        escenario.repaint(); // 🔹 Forzamos la actualización para evitar que se quede en blanco

        // 🔹 Capa 2: PNG de colisiones
        colisiones = new ColisionesPanel();
        colisiones.setBounds(0, 0, 3192, 4096);
        capaJuego.add(colisiones, JLayeredPane.PALETTE_LAYER);

        // 🔹 Capa 3: Movimiento (Personaje) - Se pasa la referencia del escenario y del sistema de colisiones
        // 🔹 Aseguramos que `Movimiento` se inicializa después del escenario para evitar problemas de referencia.
        movimiento = new Movimiento(escenario, colisiones);
        movimiento.setBounds(0, 0, 1280, 720);
        capaJuego.add(movimiento, JLayeredPane.MODAL_LAYER);

        // 🔹 Agregamos la capa de juego a CardLayout
        contenedorPrincipal.add(capaJuego, "JUEGO");

        setVisible(true);
    }

    /**
     * Método para cambiar entre pantallas (Menú, Juego).
     * Se usa invokeLater() para asegurar que Movimiento reciba el foco correctamente después del cambio.
     * Explicado en los apuntes sobre "Eventos y Escuchadores" (1.4. EVENTOS Y ESCUCHADORES.docx).
     *
     * @param pantalla Nombre de la pantalla a la que queremos cambiar ("MENU" o "JUEGO").
     */
    public void cambiarPantalla(String pantalla) {
        cardLayout.show(contenedorPrincipal, pantalla);

        // 🔹 Cuando pasamos a la pantalla de juego, aseguramos que Movimiento reciba el foco
        if (pantalla.equals("JUEGO")) {
            SwingUtilities.invokeLater(() -> movimiento.requestFocusInWindow());
        }
    }
}
