package nl.tochbedrijf.frontoffice.repository;

import nl.tochbedrijf.frontoffice.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBooksByTitleContains(String title);
}
