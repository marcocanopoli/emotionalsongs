package common;

import java.util.List;

public class StringHelpers {

    private StringHelpers() {

    }

    public static String toAlphabetic(int i) {
        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char) ((int) 'A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }

    public static String getSongsListDurationString(List<Song> songsList) {
        Integer duration = 0;

        for (Song song : songsList) {
            duration += song.getDurationInt();
        }

        return duration == 0 ? "0" :
                String.format("%d ore, %02d minuti, %02d secondi", duration / 3600, (duration % 3600) / 60, (duration % 60));
    }
}
