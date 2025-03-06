package juegoprog.graficos;

import juegoprog.escenarios.EscenarioDistritoSombrio;
import juegoprog.escenarios.ColisionesPanel;
import juegoprog.sistema.MenuPrincipal;
import juegoprog.sistema.Movimiento;

import javax.swing.*;
import java.awt.*;

    /** Gestiona la ventana principal del juego, utilizando
     *  CardLayout para cambiar entre pantallas como vimos en los ejemplos.
     *  Implementa capas (`JLayeredPane`) para manejar la superposición de elementos. */

public class Pantalla extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contenedorPrincipal;
    private final JLayeredPane capaJuego;
    private final Movimiento movimiento;
    private final EscenarioDistritoSombrio escenario;
    private final ColisionesPanel colisiones;

    /** Configura la ventana del juego, las pantallas y las capas de la interfaz. */

    public Pantalla() {
        setTitle("Juego - Pantalla Principal");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 Configuración de CardLayout para gestionar pantallas
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);
        setContentPane(contenedorPrincipal);

        // 🔹 Menú principal
        contenedorPrincipal.add(new MenuPrincipal(this), "MENU");

        // 🔹 Configuración de la pantalla de juego con capas
        capaJuego = new JLayeredPane();
        capaJuego.setPreferredSize(new Dimension(1280, 720));

        // 🔹 Fondo del escenario
        escenario = new EscenarioDistritoSombrio();
        escenario.setBounds(0, 0, 3192, 4096);
        capaJuego.add(escenario, JLayeredPane.DEFAULT_LAYER);

        // 🔹 PNG de colisiones
        colisiones = new ColisionesPanel();
        colisiones.setBounds(0, 0, 3192, 4096);
        capaJuego.add(colisiones, JLayeredPane.PALETTE_LAYER);

        // 🔹 Movimiento (Personaje), pasando referencias de escenario y colisiones
        movimiento = new Movimiento(escenario, colisiones);
        movimiento.setBounds(0, 0, 1280, 720);
        capaJuego.add(movimiento, JLayeredPane.MODAL_LAYER);

        // 🔹 Agregar la pantalla de juego al contenedor de pantallas
        contenedorPrincipal.add(capaJuego, "JUEGO");

        setVisible(true);
    }

    /** Cambia entre pantallas (Menú o Juego) dentro del CardLayout.
     *  Si se cambia a "JUEGO", asegura que `Movimiento` reciba el foco. */

    public void cambiarPantalla(String pantalla) {
        cardLayout.show(contenedorPrincipal, pantalla);

        if ("JUEGO".equals(pantalla)) {
            SwingUtilities.invokeLater(movimiento::requestFocusInWindow);
        }
    }
}
