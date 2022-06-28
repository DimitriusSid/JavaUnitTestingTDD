package bookstroread;

import java.util.*;
import java.util.stream.Collectors;

public class BookShelf {

    private final List<Book> books = new ArrayList<>();

    public List<Book> books() {
        return Collections.unmodifiableList(books);
    }

    public void add(Book... booksToAdd) {
        books.addAll(Arrays.asList(booksToAdd));
    }

    public List<Book> arrange() {
        return books.stream().sorted().collect(Collectors.toList());
    }
}
