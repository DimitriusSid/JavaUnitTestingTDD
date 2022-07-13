package bookstoread;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("<= BookShelf Specification =>")
@ExtendWith(BooksParameterResolver.class)
public class BookShelfSpec {

    private BookShelf shelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;
    private Book cleanCode;

    @BeforeEach
    void init(Map<String, Book> books) {
        shelf = new BookShelf();
        this.effectiveJava = books.get("Effective Java");
        this.codeComplete = books.get("Code Complete");
        this.mythicalManMonth = books.get("The Mythical Man-Month");
        this.cleanCode = books.get("Clean Code");
    }

    @Nested
    @DisplayName("is empty")
    class isEmpty {
        @Test
        @DisplayName(value = "when no book is added to it")
        void shelfEmptyWhenNoBookAdded() {
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf should be empty");
        }
        @Test
        @DisplayName(value = "when add is called without books")
        void emptyBookShelfWhenAddIsCalledWithoutBooks() {
            shelf.add();
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf should be empty");
        }
    }

    @Nested
    @DisplayName("after adding books")
    class BooksAreAdded {

        @Test
        @DisplayName(value = "contains 2 books")
        void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            assertEquals(2, books.size(), () -> "BookShelf should have two books");
        }

        @Test
        @DisplayName(value = "return an immutable books collection to client")
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
    }

    @Nested
    @DisplayName("search")
    class BookShelfSearchSpec {

        @BeforeEach
        void setup() {
            shelf.add(codeComplete, effectiveJava, mythicalManMonth, cleanCode);
        }

        @Test
        @DisplayName("should find books with title containing text")
        void shouldFindBookWithTitleContainingText() {
            List<Book> books = shelf.findBooksByTitle("code");
            assertThat(books.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("should find book with title containing text and published after specified date")
        void shouldFilterSearchedBooksBasedOnPublishedDate() {
            List<Book> books = shelf.findBooksByTitle("code", book -> book.getPublishedOn()
                    .isBefore(LocalDate.of(2014, 12, 31)));
            assertThat(books.size()).isEqualTo(2);

        }
    }

    @Nested
    @DisplayName("exception handling")
    class BookShelfExceptionSpec {

        /**
         * Variant 1
         */
        @Test
        void throwsExceptionWhenBooksAreAddedAfterCapacityIsReached_V1() {
            BookShelf bookShelf = new BookShelf(2);
            bookShelf.add(effectiveJava, codeComplete);
            try {
                bookShelf.add(mythicalManMonth);
                fail("Should throw BookShelfCapacityReached exception as more books are added then shelf capacity");
            } catch (BookShelfCapacityReached expected) {
                assertEquals("BookShelf capacity of 2 is reached. You can't add more books.", expected.getMessage());
            }
        }

        /**
         * Variant 2
         */

        @RepeatedTest(10)
        @ExtendWith(LoggingTestExecutionExceptionHandler.class)
        void throwsExceptionWhenBooksAreAddedAfterCapacityIsReached_V2() {
            BookShelf bookShelf = new BookShelf(2);
            bookShelf.add(effectiveJava, codeComplete);
            BookShelfCapacityReached throwException = assertThrows(BookShelfCapacityReached.class,
                    () -> bookShelf.add(mythicalManMonth));

            assertEquals("BookShelf capacity of 2 is reached. You can't add more books.", throwException.getMessage());
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
        Map<String, List<Book>> booksByAuthor = shelf.groupBy(Book::getAuthor);

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
