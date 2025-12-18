package nl.tochbedrijf.frontoffice.services;


import nl.tochbedrijf.frontoffice.domain.Book;
import nl.tochbedrijf.frontoffice.repository.BookRepository;
import nl.tochbedrijf.frontoffice.services.dtos.BookDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with ID: " + id));
    }

    public List<BookDTO> findBooksByTitleContains(String title) {
        return bookRepository.findBooksByTitleContains(title)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book newBook = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(newBook);
        return convertToDto(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO updatedBook) {
        return bookRepository.findById(id)
                .map(bookItem -> {
                    bookItem.setTitle(updatedBook.getTitle());
                    bookItem.setAuthor(updatedBook.getAuthor());
                    return convertToDto(bookRepository.save(bookItem));
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with ID: " + id));
    }

    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new RuntimeException("Book not found with ID: " + id);
        }
    }


    // Utils code
    private BookDTO convertToDto(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }

    private Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId()); // ID might be null for new book
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setInternalCode(UUID.randomUUID().toString());
        return book;
    }
}
