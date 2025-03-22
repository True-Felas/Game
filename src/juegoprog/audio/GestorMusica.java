package juegoprog.audio;

import juegoprog.graficos.Pantalla;

import javax.sound.sampled.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class GestorMusica {
    private Clip musicaClip; // Clip de audio que reproduce la música
    private LineListener listenerActual;

    /** Reproduce un archivo de música en bucle. */
    public void reproducirMusica(String ruta) {
        detenerMusica();

        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("❌ Archivo de música no encontrado: " + ruta);
                return;
            }

            musicaClip = AudioSystem.getClip();
            musicaClip.open(AudioSystem.getAudioInputStream(url));
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicaClip.start();

        } catch (Exception e) {
            System.err.println("❌ Error al cargar la música: " + e.getMessage());
        }
    }

    /** Reproduce dos archivos de música secuenciales: intro y luego loop. */
    public void reproducirMusicaSecuencial(String rutaIntro, String rutaLoop, Pantalla ventana) {
        if (musicaClip != null && musicaClip.isOpen()) {
            System.out.println("⌛ Clip aún abierto, esperando para iniciar secuencia...");

            new javax.swing.Timer(200, e -> {
                reproducirMusicaSecuencial(rutaIntro, rutaLoop, ventana);
            }).start();

            return;
        }


        detenerMusica();

        try {
            URL urlIntro = getClass().getResource(rutaIntro);
            if (urlIntro == null) {
                System.err.println("❌ Archivo de música no encontrado: " + rutaIntro);
                return;
            }

            musicaClip = AudioSystem.getClip();
            musicaClip.open(AudioSystem.getAudioInputStream(urlIntro));
            musicaClip.start();

            listenerActual = event -> {
                if (event.getType() == LineEvent.Type.STOP && ventana.isEnCinematica()) {
                    detenerMusica();
                    reproducirMusica(rutaLoop);
                    System.out.println("🔁 Iniciando loop tras intro: " + rutaLoop);
                }
            };

            musicaClip.addLineListener(listenerActual);

        } catch (Exception e) {
            System.err.println("❌ Error al cargar la música: " + e.getMessage());
        }
    }

    /** Detiene la música actual y limpia el listener si existe. */
    public void detenerMusica() {
        if (musicaClip != null) {
            if (listenerActual != null) {
                musicaClip.removeLineListener(listenerActual);
                listenerActual = null;
            }
            musicaClip.stop();
            musicaClip.close();
            musicaClip = null;
        }
    }

    /** Aplica un efecto de fade out antes de detener la música. */
    public void fadeOutMusica(int tiempo) {
        if (musicaClip == null || !musicaClip.isRunning()) return;

        FloatControl volumeControl = (FloatControl) musicaClip.getControl(FloatControl.Type.MASTER_GAIN);
        float minVolume = volumeControl.getMinimum();
        float currentVolume = volumeControl.getValue();
        int pasos = tiempo / 25;

        new javax.swing.Timer(25, new ActionListener() {
            int contador = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (contador >= pasos) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    detenerMusica();
                } else {
                    float newVolume = currentVolume - ((currentVolume - minVolume) / pasos) * contador;
                    volumeControl.setValue(newVolume);
                    contador++;
                }
            }
        }).start();
    }
}
