package nl.tochbedrijf.frontoffice.web.rest;

import nl.tochbedrijf.frontoffice.services.BookService;
import nl.tochbedrijf.frontoffice.services.dtos.BookDTO;
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

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getAllBook() {
        return bookService.getAll();
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping("/titleContains/{title}")
    public List<BookDTO> getBooksByTitleContains(@PathVariable String title) {
        return bookService.findByTitleContains(title);
    }

    // 4. Update POST endpoint to use repository.save()
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        return bookService.create(bookDTO);
    }

    // 5. Update PUT endpoint to use repository.save()
    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable Long id, @RequestBody BookDTO updatedBookDTO) {
        return bookService.update(id, updatedBookDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }
}
