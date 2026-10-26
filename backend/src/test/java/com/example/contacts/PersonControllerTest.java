package com.example.contacts;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateReadUpdateDeleteAndSearchPeopleViaMockMvc() throws Exception {
        // 1. Create (POST /api/people -> 201 Created)
        String createJson = """
                {
                  "firstName": "Katherine",
                  "lastName": "Johnson",
                  "email": "katherine@nasa.gov",
                  "phone": "+1 757 864 1000"
                }
                """;

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Katherine"))
                .andExpect(jsonPath("$.lastName").value("Johnson"));

        Long createdId = repository.findAll().get(0).getId();

        // 2. Read & Search (GET /api/people?lastName=john -> 200 OK)
        mockMvc.perform(get("/api/people").param("lastName", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Katherine"));

        // 3. Update (PUT /api/people/{id} -> 200 OK)
        String updateJson = """
                {
                  "firstName": "Katherine",
                  "lastName": "Goble-Johnson",
                  "email": "katherine@nasa.gov",
                  "phone": "+1 757 864 1000"
                }
                """;

        mockMvc.perform(put("/api/people/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Goble-Johnson"));

        // 4. Delete (DELETE /api/people/{id} -> 204 No Content, then GET -> 404 Not Found)
        mockMvc.perform(delete("/api/people/{id}", createdId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/people/{id}", createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidPersonPayloadsWithBadRequest() throws Exception {
        // Blank firstName/lastName -> 400 Bad Request
        String blankNameJson = """
                {
                  "firstName": "   ",
                  "lastName": "",
                  "email": "valid@example.com"
                }
                """;

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankNameJson))
                .andExpect(status().isBadRequest());

        // Invalid email format -> 400 Bad Request
        String invalidEmailJson = """
                {
                  "firstName": "Ada",
                  "lastName": "Lovelace",
                  "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailJson))
                .andExpect(status().isBadRequest());
    }
}

