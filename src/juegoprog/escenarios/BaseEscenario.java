package juegoprog.escenarios;

import java.awt.Graphics;

public class BaseEscenario {
    protected int ancho;
    protected int alto;

    public BaseEscenario(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
    }

    public void cargar() {
        System.out.println("Cargando escenario...");
    }

    public void actualizar() {
        System.out.println("Actualizando escenario...");
    }

    public void renderizar(Graphics g) {  // ✅ Ahora usa Graphics g
        System.out.println("Dibujando escenario...");
    }
}

