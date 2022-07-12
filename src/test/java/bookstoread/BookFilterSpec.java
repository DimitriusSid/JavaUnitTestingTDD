package bookstoread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookFilterSpec {
    private Book cleanCode;
    private Book codeComplete;

    @BeforeEach
    void init() {
        cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
        codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
    }

    @Nested
    @DisplayName("book published date")
    class BookPublishedFilterSpec implements FilterBoundaryTests {

        BookFilter filter;

        @BeforeEach
        void init() {
            filter = BookPublishedYearFilter.Before(2007);
        }

        @Override
        public BookFilter get() {
            return filter;
        }

        @Test
        @DisplayName("is after specified year")
        void validateBookPublishedDatePostAskedYear() {
            BookFilter filter = BookPublishedYearFilter.After(2007);
            assertTrue(filter.apply(cleanCode));
            assertFalse(filter.apply(codeComplete));
        }

        @Test
        @DisplayName("is before specified year")
        void validateBookPublishedDatePreAskedYear() {
            BookFilter filter = BookPublishedYearFilter.Before(2007);
            assertFalse(filter.apply(cleanCode));
            assertTrue(filter.apply(codeComplete));
        }

        @Test
        @DisplayName("Composite criteria is based on multiple filters")
        void shouldFilterOnMultiplesCriteria() {
            CompositeFilter compositeFilter = new CompositeFilter();
            compositeFilter.addFilter(b -> false);
            assertFalse(compositeFilter.apply(cleanCode));
        }

        @Test
        @DisplayName("Composite criteria does not invoke after first failure")
        void shouldNotInvokeAfterFirstFailure() {
            CompositeFilter compositeFilter = new CompositeFilter();

            BookFilter invokedMockedFilter = Mockito.mock(BookFilter.class);
            Mockito.when(invokedMockedFilter.apply(cleanCode)).thenReturn(false);
            compositeFilter.addFilter(invokedMockedFilter);

            BookFilter nonInvokedMockedFilter = Mockito.mock(BookFilter.class);
            Mockito.when(nonInvokedMockedFilter.apply(cleanCode)).thenReturn(true);
            compositeFilter.addFilter(nonInvokedMockedFilter);

            assertFalse(compositeFilter.apply(cleanCode));
            Mockito.verify(invokedMockedFilter).apply(cleanCode);
//            Mockito.verifyNoInteractions(nonInvokedMockedFilter);
        }


        @Test
        @DisplayName("Composite criteria invokes all filters")
        void shouldInvokeAllFilters() {
            CompositeFilter compositeFilter = new CompositeFilter();

            BookFilter firstInvokedMockedFilter = Mockito.mock(BookFilter.class);
            Mockito.when(firstInvokedMockedFilter.apply(cleanCode)).thenReturn(true);
            compositeFilter.addFilter(firstInvokedMockedFilter);

            BookFilter secondInvokedMockedFilter = Mockito.mock(BookFilter.class);
            Mockito.when(secondInvokedMockedFilter.apply(cleanCode)).thenReturn(true);
            compositeFilter.addFilter(secondInvokedMockedFilter);

            assertTrue(compositeFilter.apply(cleanCode));
            Mockito.verify(firstInvokedMockedFilter).apply(cleanCode);
            Mockito.verify(secondInvokedMockedFilter).apply(cleanCode);
        }

    }



}
