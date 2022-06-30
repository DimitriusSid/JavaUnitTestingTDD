package bookstroread;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("<= BookShelf Specification =>")
public class BookShelfSpec {

    private BookShelf shelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;
    private Book cleanCode;

    @BeforeEach
    void init() throws Exception {
        shelf = new BookShelf();
        effectiveJava = new Book("Effective Java", "Joshua Bloch", LocalDate.of(2008, Month.MAY, 8));
        codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks", LocalDate.of(1975, Month.JANUARY, 1));
        cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.MAY, 1));
    }

    @Test
    @DisplayName(value = "bookshelf is empty when no book is added to it")
    void shelfEmptyWhenNoBookAdded() {
        List<Book> books = shelf.books();
        assertTrue(books.isEmpty(), () -> "BookShelf should be empty");
    }

    @Test
    @DisplayName(value = "bookshelf contains two books when two books are added")
    void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
        shelf.add(effectiveJava, codeComplete);
        List<Book> books = shelf.books();
        assertEquals(2, books.size(), () -> "BookShelf should have two books");
    }

    @Test
    @DisplayName(value = "empty bookshelf remains empty when add is called without books")
    void emptyBookShelfWhenAddIsCalledWithoutBooks() {
        shelf.add();
        List<Book> books = shelf.books();
        assertTrue(books.isEmpty(), () -> "BookShelf should be empty");
    }

    @Test
    @DisplayName(value = "bookshelf returns an immutable books collection to client")
    void booksReturnedFromBookShelfIsImmutableForClient() {
        shelf.add(effectiveJava, codeComplete);
        List<Book> books = shelf.books();
        try {
            books.add(mythicalManMonth);
            fail(() -> "Should not be able to add book to books");
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException, () -> "Should throw UnsupportedOperationException");
        }
    }

    @Test
    @DisplayName(value = "bookshelf is arranged lexicographically by book title")
    void bookshelfArrangedByBookTitle() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth);
        List<Book> books = shelf.arrange();
        assertEquals(Arrays.asList(codeComplete, effectiveJava, mythicalManMonth), books,
                ()-> "Books in a bookshelf should be arranged lexicographically by book title");
    }

    @Test
    @DisplayName(value = "bookshelf is arranged by user provided criteria")
    void booksInBookShelfAreInInsertionOrderAfterCallingArrange() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth);
        shelf.arrange();
        List<Book> books = shelf.books();
        assertEquals(Arrays.asList(effectiveJava, codeComplete, mythicalManMonth), books,
                () -> "Books in bookshelf are in insertion order");
    }

    @Test
    @DisplayName(value = "books inside bookshelf are grouped according to user provided criteria")
    void bookshelfArrangedByUserProvidedCriteria() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth);
        Comparator<Book> reversed = Comparator.<Book>naturalOrder().reversed();
        List<Book> books = shelf.arrange(reversed);
        assertThat(books).isSortedAccordingTo(reversed);
    }

    @Test
    @DisplayName(value = "books inside bookshelf are grouped by publication year")
    void groupBookInsideBookShelfByPublicationYear() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
        Map<Year, List<Book>> booksByPublicationYear = shelf.groupByPublicationYear();

        assertThat(booksByPublicationYear)
                .containsKey(Year.of(2008))
                .containsValues(Arrays.asList(effectiveJava, cleanCode));

        assertThat(booksByPublicationYear)
                .containsKey(Year.of(2004))
                .containsValues(Collections.singletonList(codeComplete));

        assertThat(booksByPublicationYear)
                .containsKey(Year.of(1975))
                .containsValues(Collections.singletonList(mythicalManMonth));
    }

    @Test
    @DisplayName(value = "books inside bookshelf are grouped according to user provided criteria (group by author name)")
    void groupBooksByUserProvidedCriteria() {
        shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
        Map<String, List<Book>> booksByAuthor = shelf.groupBy(Book::author);

        assertThat(booksByAuthor)
                .containsKey("Joshua Bloch")
                .containsValues(Collections.singletonList(effectiveJava));

        assertThat(booksByAuthor)
                .containsKey("Steve McConnel")
                .containsValues(Collections.singletonList(codeComplete));

        assertThat(booksByAuthor)
                .containsKey("Frederick Phillips Brooks")
                .containsValues(Collections.singletonList(mythicalManMonth));

        assertThat(booksByAuthor)
                .containsKey("Robert C. Martin")
                .containsValues(Collections.singletonList(cleanCode));

    }





}
