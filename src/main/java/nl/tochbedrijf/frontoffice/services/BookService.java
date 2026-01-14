package nl.tochbedrijf.frontoffice.services;


import nl.tochbedrijf.frontoffice.controller.exceptions.BookNotFoundException;
import nl.tochbedrijf.frontoffice.domain.Book;
import nl.tochbedrijf.frontoffice.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findByActiveIsTrue()
                .stream()
                .map(Converter::convertToDto)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        return bookRepository.findByActiveIsTrueAndIdEquals(id)
                .map(Converter::convertToDto)
                .orElseThrow(() ->
                        new BookNotFoundException( // This is the way Exception should be defined
                                "Book not found with ID: " + id));
    }

    public List<BookDTO> findBooksByTitleContains(String title) {
        return bookRepository.findByActiveIsTrueAndTitleContains(title)
                .stream()
                .map(Converter::convertToDto)
                .collect(Collectors.toList());
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book newBook = Converter.convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(newBook);
        return Converter.convertToDto(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO updatedBook) {
        return bookRepository.findByActiveIsTrueAndIdEquals(id)
                .map(bookItem -> {
                    bookItem.setTitle(updatedBook.title());
                    bookItem.setAuthor(updatedBook.author());
                    return Converter.convertToDto(bookRepository.save(bookItem));
                })
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with ID: " + id));
    }

    public void deleteBook(Long id) {
        Optional<Book> optById = bookRepository.findByActiveIsTrueAndIdEquals(id);
        if(optById.isEmpty())
            throw new BookNotFoundException("Book not found with ID: " + id);

        Book book = optById.get();
        book.setActive(false);
        bookRepository.save(book);
    }

}
