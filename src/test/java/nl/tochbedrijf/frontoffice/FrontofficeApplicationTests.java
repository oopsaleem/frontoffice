package nl.tochbedrijf.frontoffice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Optional;
import java.util.UUID;
import nl.tochbedrijf.frontoffice.domain.Book;
import nl.tochbedrijf.frontoffice.repository.BookRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrontofficeApplicationTests {

    public static final String PATH_PREFIX = "/api/books";

    @Autowired private MockMvc mockMvc;

    @Autowired private BookRepository bookRepository;

    final Book animalFarm =
            new Book(null, "Animal farm", "George Orwell", UUID.randomUUID().toString(), true);

    Long insertAnimalFarm() {
        Book saved = bookRepository.save(animalFarm);
        return saved.getId();
    }

    @BeforeEach
    void deleteAllBeforeTests() throws Exception {
        bookRepository.deleteAll();
    }

    @Test
    void contextLoads() {}

    @Test
    void shouldReturnNoBooks() throws Exception {
        mockMvc.perform(get(PATH_PREFIX)).andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test
    void shouldRetrieveBook() throws Exception {
        Long id = insertAnimalFarm();
        mockMvc
                .perform(get(String.format(PATH_PREFIX + "/%d", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(animalFarm.getTitle()))
                .andExpect(jsonPath("$.author").value(animalFarm.getAuthor()));
    }

    @Test
    void shouldFindByTitleContains() throws Exception {
        insertAnimalFarm();
        mockMvc
                .perform(get(PATH_PREFIX + "/titleContains/Animal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(animalFarm.getTitle()))
                .andExpect(jsonPath("$[0].author").value(animalFarm.getAuthor()));
    }

    @Test
    void findByTitleContainsIsCaseSensitive() throws Exception {
        insertAnimalFarm();
        mockMvc
                .perform(get(PATH_PREFIX+ "/titleContains/animal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.empty()));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Long id = insertAnimalFarm();

        mockMvc
                .perform(
                        put(String.format(PATH_PREFIX+"/%d", id))
                                .header("Content-Type", "application/json")
                                .content(
                                        String.format(
                                                "{ \"author\": \"%s\", \"title\":\"%s\"}", animalFarm.getAuthor(), "1984")))
                .andExpect(status().isOk());

        Optional<Book> updatedBook = bookRepository.findById(id);
        assertThat(updatedBook).isPresent();
        Book book = updatedBook.get();
        assertThat(book.getTitle()).isEqualTo("1984");
    }

    @Test
    void shouldNotBeAbleToUpdateBook() throws Exception {
        Long id = insertAnimalFarm();

        mockMvc
                .perform(
                        put(String.format(PATH_PREFIX+"/%d", id + 1))
                                .header("Content-Type", "application/json")
                                .content(
                                        String.format(
                                                "{ \"author\": \"%s\", \"title\":\"%s\"}", animalFarm.getAuthor(), "1984")))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found with ID: " + (id + 1)));
    }

    @Test
    void shouldDeleteEntity() throws Exception {
        Long id = insertAnimalFarm();

        mockMvc.perform(delete(String.format(PATH_PREFIX + "/%d", id))).andExpect(status().isNoContent());

        var optionalDeletedBook = bookRepository.findById(id);
        assertThat(optionalDeletedBook).isPresent();
        var deletedBook = optionalDeletedBook.get();
        assertThat(deletedBook.isActive()).isFalse();

    }

    @Test
    void shouldNotDeleteNonExistentEntity() throws Exception {
        Long id = insertAnimalFarm();
        mockMvc.perform(delete(String.format(PATH_PREFIX+"/%d", id + 1))).andExpect(status().isNotFound());
    }

    @Test
    void shouldInsertEntity() throws Exception {
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(PATH_PREFIX)
                                        .header("Content-Type", "application/json")
                                        .content(
                                                String.format(
                                                        "{ \"author\": \"%s\", \"title\":\"%s\"}",
                                                        animalFarm.getAuthor(), animalFarm.getTitle())))
                        .andExpect(status().isCreated())
                        .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
        Long id = jsonObject.get("id").getAsLong();

        Optional<Book> bookFromDatabase = bookRepository.findById(id);
        assertThat(bookFromDatabase).isPresent();
        Book book = bookFromDatabase.get();
        assertThat(book.getAuthor()).isEqualTo(animalFarm.getAuthor());
        assertThat(book.getTitle()).isEqualTo(animalFarm.getTitle());
    }

    @Test
    void shouldPartiallyInsertEntity() throws Exception {
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(PATH_PREFIX)
                                        .header("Content-Type", "application/json")
                                        .content(
                                                String.format(
                                                        "{ \"Wrong_Attribute_Name\": \"%s\", \"title\":\"%s\"}",
                                                        animalFarm.getAuthor(), animalFarm.getTitle())))
                        .andExpect(status().isCreated())
                        .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
        Long id = jsonObject.get("id").getAsLong();

        Optional<Book> bookFromDatabase = bookRepository.findById(id);
        assertThat(bookFromDatabase).isPresent();
        Book book = bookFromDatabase.get();
        assertThat(book.getAuthor()).isNull();
        assertThat(book.getTitle()).isEqualTo(animalFarm.getTitle());
    }
}
