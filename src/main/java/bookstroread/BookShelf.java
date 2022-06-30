package bookstroread;

import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class BookShelf {

    private final List<Book> books = new ArrayList<>();

    public List<Book> books() {
        return Collections.unmodifiableList(books);
    }

    public void add(Book... booksToAdd) {
        books.addAll(Arrays.asList(booksToAdd));
    }

    public List<Book> arrange() {
        return arrange(Comparator.naturalOrder());
    }

    public List<Book> arrange(Comparator<Book> criteria) {
        return books.stream().sorted(criteria).collect(Collectors.toList());
    }

    public Map<Year, List<Book>> groupByPublicationYear() {
        return groupBy(book -> Year.of(book.publishedOn().getYear()));
    }

    public <K> Map <K, List<Book>> groupBy(Function<Book, K> function) {
        return books.stream().collect(groupingBy(function));
    }
}
