package bookstroread;

import java.time.LocalDate;

public record Book
        (String title,
        String author,
        LocalDate publishedOn) implements Comparable<Book> {

    @Override
    public int compareTo(Book that) {
        return this.title.compareTo(that.title);
    }
}

//@Data
//    private final String title;
//    private final String author;
//    private final LocalDate publishedOn;
//
//    public Book(String title, String author, LocalDate publishedOn) {
//        this.title = title;
//        this.author = author;
//        this.publishedOn = publishedOn;
//    }