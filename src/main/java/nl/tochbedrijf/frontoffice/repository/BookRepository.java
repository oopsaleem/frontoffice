package nl.tochbedrijf.frontoffice.repository;

import nl.tochbedrijf.frontoffice.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
