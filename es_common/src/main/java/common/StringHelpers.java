package common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contiene metodi utili alla formattazione di stringhe
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class StringHelpers {

    private StringHelpers() {

    }

    /**
     * Crea una stringa alfabetica a partire da un intero
     *
     * @param i l'intero da convertire
     * @return la stringa alfabetica
     */
    public static String toAlphabetic(int i) {
        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char) ('A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }

    /**
     * Testa l'invalidità di una stringa con una espressione regolare
     *
     * @param regEx la stringa regEx
     * @param text  la stringa da testare
     * @return true se la stringa è invalida
     */
    public static boolean invalidRegExMatch(String regEx, String text) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);

        return !m.find();
    }

    /**
     * Formatta la durata totale di una lista di canzoni
     *
     * @param songsList la lista di canzoni
     * @return la stringa nel formato 'h ore, m minuti, s secondi'
     */
    public static String getSongsListDurationString(List<Song> songsList) {
        Integer duration = 0;

        for (Song song : songsList) {
            duration += song.getDurationInt();
        }

        return duration == 0 ? "0" :
                String.format("%d ore, %02d minuti, %02d secondi", duration / 3600, (duration % 3600) / 60, (duration % 60));
    }
}
