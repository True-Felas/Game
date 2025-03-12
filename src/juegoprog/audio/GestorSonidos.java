package juegoprog.audio;

import javax.sound.sampled.*;
import java.net.URL;

public class GestorSonidos {
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
}
