package nl.tochbedrijf.frontoffice.services;

import nl.tochbedrijf.frontoffice.domain.Book;

import java.util.UUID;

public class Converter {
    static BookDTO convertToDto(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }

    static Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.id()); // ID might be null for new book
        book.setTitle(bookDTO.title());
        book.setAuthor(bookDTO.author());
        book.setInternalCode(UUID.randomUUID().toString());
        book.setActive(true);
        return book;
    }
}
