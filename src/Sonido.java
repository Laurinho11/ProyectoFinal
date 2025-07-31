package uno;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class Sonido {

    public static void reproducirSonido(String ruta) {
        try {
            InputStream audioSrc = Sonido.class.getResourceAsStream(ruta);
            if (audioSrc == null) {
                System.err.println("No se encontró el sonido: " + ruta);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
