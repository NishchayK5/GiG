package com.g08.gig.persistence.fake;

import com.g08.gig.models.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeClientRepositoryTest {

    private FakeClientRepository repo;
    private FakeUserRepository userRepo;

    @BeforeEach
    void setUp() {
        userRepo = new FakeUserRepository();
        repo = new FakeClientRepository(userRepo);
    }

    @Test
    void add_assignsIdWhenZero() {
        Client client = new Client(0, "c@b.com", "C", "B", "555", "123 Main St", "secret");
        Client added = repo.add(client);
        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals("c@b.com", added.getEmail());
        assertEquals("123 Main St", added.getAddress());
        assertEquals("secret", added.getPassword());
    }

    @Test
    void getById_returnsAddedClient() {
        Client added = repo.add(new Client(0, "x@y.com", "X", "Y", "555", "456 Oak Ave", "pwd"));
        Client found = repo.getById(added.getId());
        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
        assertEquals("x@y.com", found.getEmail());
        assertEquals("456 Oak Ave", found.getAddress());
    }

    @Test
    void getById_returnsNullForUnknownId() {
        assertNull(repo.getById(999));
    }

    @Test
    void update_modifiesExistingClient() {
        Client added = repo.add(new Client(0, "old@x.com", "Old", "Name", "5555555555", "Old St", "p"));

        userRepo.add(new Client(
                added.getId(),
                added.getEmail(),
                added.getFirstName(),
                added.getLastName(),
                added.getPhoneNumber(),
                added.getAddress(),
                added.getPassword()
        ));

        Client updated = new Client(added.getId(), "new@x.com", "New", "Name", "9999999999", "New St", "p2");
        assertTrue(repo.update(updated));

        Client found = repo.getById(added.getId());
        assertEquals("new@x.com", found.getEmail());
        assertEquals("New St", found.getAddress());
        assertEquals("p2", found.getPassword());
    }

    @Test
    void update_returnsFalseWhenClientNull() {
        assertFalse(repo.update(null));
    }

    @Test
    void update_returnsFalseWhenIdNotExists() {
        Client client = new Client(999, "x@x.com", "X", "X", "555", "Addr", "p");
        assertFalse(repo.update(client));
    }

    @Test
    void delete_removesClient() {
        Client added = repo.add(new Client(0, "d@d.com", "D", "D", "555", "Addr", "p"));
        assertTrue(repo.delete(added.getId()));
        assertNull(repo.getById(added.getId()));
    }

    @Test
    void delete_returnsFalseForUnknownId() {
        assertFalse(repo.delete(999));
    }

    @Test
    void add_returnsNullWhenClientNull() {
        assertNull(repo.add(null));
    }

    @Test
    void add_clientWithExistingId_returnsNull() {
        Client first = repo.add(new Client(0, "a@a.com", "A", "A", "555", "Addr", "p"));
        assertNotNull(first);
        Client duplicate = new Client(first.getId(), "b@b.com", "B", "B", "555", "Addr2", "p");
        assertNull(repo.add(duplicate));
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void update_returnsFalseWhenUserNotInUserRepo() {
        Client added = repo.add(new Client(0, "x@x.com", "X", "Y", "555", "Addr", "p"));
        Client updated = new Client(added.getId(), "new@x.com", "New", "Y", "999", "New Addr", "p");
        assertFalse(repo.update(updated));
    }

    @Test
    void getAll_initiallyEmpty() {
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void getAll_returnsAllAddedClients() {
        repo.add(new Client(0, "a@a.com", "A", "A", "555", "Addr1", "p"));
        repo.add(new Client(0, "b@b.com", "B", "B", "555", "Addr2", "p"));
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        repo.add(new Client(0, "a@a.com", "A", "A", "555", "Addr", "p"));
        assertThrows(UnsupportedOperationException.class, () ->
                repo.getAll().add(new Client(1, "b@b.com", "B", "B", "555", "Addr", "p")));
    }
}
