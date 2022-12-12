package common;

import java.io.Serial;
import java.io.Serializable;

public class Song implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    private final int id;
    private final String title;
    private final String author;
    private final int year;
    private final String album;
    private final String genre;
    private final int duration;

    public Song(int id, String title, String author, int year, String album, String genre, int duration) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return (id + "\t" + year + "\t" + author + "\t" + title + "\t" + album + "\t" + genre + "\t" + duration);
    }

    public String getAuthor() {
        return this.author;
    }
}
