package juegoprog.audio;

import javax.sound.sampled.*;
import java.net.URL;

public class GestorMusica {
    private Clip musicaClip; // 🔹 Clip de audio que reproduce la música

    /** Reproduce un archivo de música en bucle.
     * Si ya hay una música sonando, se detiene antes de iniciar la nueva. */

    public void reproducirMusica(String ruta) {
        detenerMusica(); // Para evitar superposiciones de música

        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("❌ Archivo de música no encontrado: " + ruta);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            musicaClip = AudioSystem.getClip();
            musicaClip.open(audioStream);
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY); // 🔹 Se repetirá en bucle
            musicaClip.start();
        } catch (Exception e) {
            System.err.println("❌ Error al cargar la música: " + e.getMessage());
        }
    }

    /** Detiene la música inmediatamente. */

    public void detenerMusica() {
        if (musicaClip != null && musicaClip.isRunning()) {
            musicaClip.stop();
            musicaClip.close();
        }
    }

    /** Aplica un efecto de fade out antes de detener la música.
     * Reduce gradualmente el volumen hasta 0 y luego detiene la música. */

    public void fadeOutMusica(int duracion) {
        if (musicaClip == null || !musicaClip.isRunning()) return; // Si no hay música, salir

        new Thread(() -> {
            try {
                FloatControl controlVolumen = (FloatControl) musicaClip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumenActual = controlVolumen.getValue();
                float paso = volumenActual / (duracion / 100); // 🔹 Ajuste progresivo del volumen

                for (int i = 0; i < (duracion / 100); i++) {
                    volumenActual -= paso;
                    controlVolumen.setValue(volumenActual);
                    Thread.sleep(100); // 🔹 Espera breve para hacer la transición suave
                }

                detenerMusica(); // 🔹 Detiene la música cuando el volumen llega a 0
            } catch (Exception e) {
                System.err.println("❌ Error en fade out: " + e.getMessage());
            }
        }).start();
    }
}

