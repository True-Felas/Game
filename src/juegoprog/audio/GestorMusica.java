package juegoprog.audio;

import javax.sound.sampled.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Timer;

public class GestorMusica {
    private Clip musicaClip; // 🔹 Clip de audio que reproduce la música

    /** Reproduce un archivo de música en bucle.
     * Si ya hay una música sonando, se detiene antes de iniciar la nueva. */

    public void reproducirMusica(String ruta) {
        detenerMusica(); // Asegurar que no haya música previa

        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("❌ Archivo de música no encontrado: " + ruta);
                return;
            }

            // 🔹 Optimización: Reutilizar Clip si ya existe, sin recrearlo cada vez
            if (musicaClip == null) {
                musicaClip = AudioSystem.getClip();
            }

            musicaClip.open(AudioSystem.getAudioInputStream(url));
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

    public void fadeOutMusica(int tiempo) {
        if (musicaClip == null || !musicaClip.isRunning()) return;

        FloatControl volumeControl = (FloatControl) musicaClip.getControl(FloatControl.Type.MASTER_GAIN);
        float minVolume = volumeControl.getMinimum();
        float currentVolume = volumeControl.getValue();
        int pasos = tiempo / 25; // 🔹 Se reduce el tiempo entre pasos para que sea más rápido

        new javax.swing.Timer(25, new ActionListener() { // 🔹 Ahora cada 25ms en lugar de 50ms
            int contador = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (contador >= pasos) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    musicaClip.stop();
                    musicaClip.close();
                } else {
                    float newVolume = currentVolume - ((currentVolume - minVolume) / pasos) * contador;
                    volumeControl.setValue(newVolume);
                    contador++;
                }
            }
        }).start();
    }



}

