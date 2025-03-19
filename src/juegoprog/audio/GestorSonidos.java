package juegoprog.audio;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GestorSonidos {
    private Map<String, Clip> clipsActivos = new HashMap<>();

    /** Reproduce un sonido corto (disparos, alerta, abrir caja fuerte) */
    public void reproducirEfecto(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("❌ Archivo de sonido no encontrado: " + ruta);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip efectoClip = AudioSystem.getClip();
            efectoClip.open(audioStream);
            efectoClip.start();
        } catch (Exception e) {
            System.err.println("❌ Error al reproducir el sonido: " + e.getMessage());
        }
    }

    /** Reproduce un sonido en bucle (como pasos o correr) */
    public void reproducirBucle(String ruta) {
        try {
            if (clipsActivos.containsKey(ruta) && clipsActivos.get(ruta).isRunning()) {
                return; // Si ya está sonando, no lo reiniciamos
            }

            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("❌ Archivo de sonido no encontrado: " + ruta);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clipsActivos.put(ruta, clip);
            clip.start();
        } catch (Exception e) {
            System.err.println("❌ Error al reproducir el sonido en bucle: " + e.getMessage());
        }
    }

    /** Detiene un sonido en bucle (por ejemplo, cuando el jugador se detiene) */
    public void detenerSonido(String ruta) {
        if (clipsActivos.containsKey(ruta)) {
            Clip clip = clipsActivos.get(ruta);
            clip.stop();
            clip.close();
            clipsActivos.remove(ruta);
        }
    }

    public void detenerTodosLosSonidos() {
        for (String ruta : clipsActivos.keySet()) {
            Clip clip = clipsActivos.get(ruta);
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }
        clipsActivos.clear(); // Limpiamos la lista de sonidos activos
    }

}
