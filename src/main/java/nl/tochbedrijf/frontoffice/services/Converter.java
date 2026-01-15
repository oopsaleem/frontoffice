package nl.tochbedrijf.frontoffice.services;

import nl.tochbedrijf.frontoffice.domain.Book;

import java.util.UUID;

public class Converter {
    static BookDTO convertToDto(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }

    static Book convertToEntity(BookDTO bookDTO) {
        return new Book(bookDTO.id(), bookDTO.title(), bookDTO.author(), UUID.randomUUID().toString(), true);
    }
}
