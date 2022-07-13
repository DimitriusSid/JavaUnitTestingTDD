package bookstoread;

import java.time.Year;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class BookShelf {

    private final int capacity;
    private final List<Book> books = new ArrayList<>();

    public BookShelf() {
        this.capacity = Integer.MAX_VALUE;
    }

    public BookShelf(int capacity) {
        this.capacity = capacity;
    }

    public List<Book> books() {
        return Collections.unmodifiableList(books);
    }

    public void add(Book... booksToAdd) throws BookShelfCapacityReached {
        Arrays.stream(booksToAdd)
                        .forEach(book -> {
                            if (books.size() == capacity) {
                                throw new BookShelfCapacityReached(String.format("BookShelf capacity" +
                                        " of %d is reached. You can't add more books.", capacity));
                            }
                            books.add(book);
                        });
    }

    public List<Book> arrange() {
        return arrange(Comparator.naturalOrder());
    }

    public List<Book> arrange(Comparator<Book> criteria) {
        return books.stream().sorted(criteria).collect(Collectors.toList());
    }

    public Map<Year, List<Book>> groupByPublicationYear() {
        return groupBy(book -> Year.of(book.getPublishedOn().getYear()));
    }

    public <K> Map <K, List<Book>> groupBy(Function<Book, K> function) {
        return books.stream().collect(groupingBy(function));
    }

    public Progress progress() {
        int booksRead = Long.valueOf(books.stream().filter(Book::isRead).count()).intValue();
        int booksInProgress = Long.valueOf(books.stream().filter(Book::isInProgress).count()).intValue();
        int booksToRead = books.size() - booksRead;
        int percentageCompleted = booksRead * 100 / books.size();
        int percentageToRead = booksToRead * 100 / books.size();
        return new Progress(percentageCompleted, percentageToRead, booksInProgress);
    }

    public List<Book> findBooksByTitle(String toSearch) {
        return findBooksByTitle(toSearch, book -> true);
    }

    public List<Book> findBooksByTitle(String title, BookFilter filter) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title))
                .filter(filter::apply)
                .collect(Collectors.toList());

    }
}
