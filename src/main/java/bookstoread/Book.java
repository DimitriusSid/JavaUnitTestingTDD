package bookstoread;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Book implements Comparable<Book> {
    private final String title;
    private final String author;
    private final LocalDate publishedOn;
    private LocalDate startedReadingOn;
    private LocalDate finishedReadingOn;
    private boolean isRead;
    private boolean inProgress;

    public void startedReadingOn(LocalDate startedOn) {
        this.startedReadingOn = startedOn;
    }

    public void finishedReadingOn(LocalDate finishedOn) {
        this.finishedReadingOn = finishedOn;
    }

    public boolean isRead() {
        return startedReadingOn != null && finishedReadingOn != null;
    }

    public boolean isProgress() {
        return startedReadingOn != null && finishedReadingOn == null;
    }

    @Override
    public int compareTo(Book that) {
        return this.title.compareTo(that.title);
    }
}