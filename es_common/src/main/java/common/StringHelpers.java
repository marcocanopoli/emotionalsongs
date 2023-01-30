package common;

import java.util.List;

public class StringHelpers {

    private StringHelpers() {

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
