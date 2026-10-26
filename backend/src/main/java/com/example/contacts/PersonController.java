package com.example.contacts;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/people")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {

    private final PersonRepository repository;

    public PersonController(PersonRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Person> findAll(@RequestParam(required = false) String lastName) {
        if (lastName != null && !lastName.isBlank()) {
            return repository.findByLastNameContainingIgnoreCase(lastName.trim());
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Person findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id));
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person) {
        validatePerson(person);
        Person saved = repository.save(new Person(
                person.getFirstName().trim(),
                person.getLastName().trim(),
                person.getEmail() != null ? person.getEmail().trim() : null,
                person.getPhone() != null ? person.getPhone().trim() : null
        ));
        return ResponseEntity.created(URI.create("/api/people/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable Long id, @RequestBody Person incoming) {
        validatePerson(incoming);
        return repository.findById(id)
                .map(existing -> {
                    existing.setFirstName(incoming.getFirstName().trim());
                    existing.setLastName(incoming.getLastName().trim());
                    existing.setEmail(incoming.getEmail() != null ? incoming.getEmail().trim() : null);
                    existing.setPhone(incoming.getPhone() != null ? incoming.getPhone().trim() : null);
                    return repository.save(existing);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id);
        }
        repository.deleteById(id);
    }

    private void validatePerson(Person person) {
        if (person == null
                || person.getFirstName() == null || person.getFirstName().isBlank()
                || person.getLastName() == null || person.getLastName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "firstName and lastName are required");
        }
        if (person.getEmail() != null && !person.getEmail().isBlank()
                && !person.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must be a valid email address");
        }
    }
}

