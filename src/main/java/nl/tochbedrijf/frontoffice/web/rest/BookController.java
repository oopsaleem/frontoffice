package nl.tochbedrijf.frontoffice.web.rest;

import nl.tochbedrijf.frontoffice.domain.Book;
import nl.tochbedrijf.frontoffice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    // 1. Inject BookRepository into BookController
    @Autowired
    private BookRepository bookRepository;

    // 2. Replace in-memory list with repository calls
    // 3. Update GET endpoints to use repository.findAll()
    @GetMapping
    public List<Book> getAllBook() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // 4. Update POST endpoint to use repository.save()
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    // 5. Update PUT endpoint to use repository.save()
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    // 6. Update DELETE endpoint to use repository.deleteById()
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found");
        }
        bookRepository.deleteById(id);
    }
}
