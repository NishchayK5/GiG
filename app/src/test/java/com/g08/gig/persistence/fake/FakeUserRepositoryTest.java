package com.g08.gig.persistence.fake;

import com.g08.gig.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeUserRepositoryTest {

    private FakeUserRepository repo;

    @BeforeEach
    void setUp() {
        repo = new FakeUserRepository();
    }

    @Test
    void add_assignsIdWhenZero() {
        User user = new User(0, "a@b.com", "A", "B", "555", "secret");
        User added = repo.add(user);
        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals("a@b.com", added.getEmail());
        assertEquals("555", added.getPhoneNumber());
        assertEquals("secret", added.getPassword());
    }

    @Test
    void getById_returnsAddedUser() {
        User added = repo.add(new User(0, "x@y.com", "X", "Y", "555", "pwd"));
        User found = repo.getById(added.getId());
        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
        assertEquals("x@y.com", found.getEmail());
    }

    @Test
    void getById_returnsNullForUnknownId() {
        assertNull(repo.getById(999));
    }

    @Test
    void getByEmail_returnsUserWhenMatch() {
        repo.add(new User(0, "u@u.com", "U", "U", "555", "p"));
        User found = repo.getByEmail("u@u.com");
        assertNotNull(found);
        assertEquals("u@u.com", found.getEmail());
    }

    @Test
    void getByEmail_returnsNullWhenEmailNull() {
        assertNull(repo.getByEmail(null));
    }

    @Test
    void getByEmail_returnsNullWhenNotFound() {
        assertNull(repo.getByEmail("nonexistent@x.com"));
    }

    @Test
    void update_modifiesExistingUser() {
        User added = repo.add(new User(0, "old@x.com", "Old", "Name", "555", "p"));
        User updated = new User(added.getId(), "new@x.com", "New", "Name", "999", "p2");
        assertTrue(repo.update(updated));
        User found = repo.getById(added.getId());
        assertEquals("new@x.com", found.getEmail());
        assertEquals("New", found.getFirstName());
        assertEquals("999", found.getPhoneNumber());
        assertEquals("p2", found.getPassword());
    }

    @Test
    void update_returnsFalseWhenUserNull() {
        assertFalse(repo.update(null));
    }

    @Test
    void update_returnsFalseWhenIdNotExists() {
        User user = new User(999, "x@x.com", "X", "X", "555", "p");
        assertFalse(repo.update(user));
    }

    @Test
    void delete_removesUser() {
        User added = repo.add(new User(0, "d@d.com", "D", "D", "555", "p"));
        assertTrue(repo.delete(added.getId()));
        assertNull(repo.getById(added.getId()));
    }

    @Test
    void delete_returnsFalseForUnknownId() {
        assertFalse(repo.delete(999));
    }

    @Test
    void add_returnsNullWhenUserNull() {
        assertNull(repo.add(null));
    }

    @Test
    void getAll_initiallyEmpty() {
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void getAll_returnsAllAddedUsers() {
        repo.add(new User(0, "a@a.com", "A", "A", "555", "p"));
        repo.add(new User(0, "b@b.com", "B", "B", "555", "p"));
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        repo.add(new User(0, "a@a.com", "A", "A", "555", "p"));
        assertThrows(UnsupportedOperationException.class, () ->
                repo.getAll().add(new User(1, "b@b.com", "B", "B", "555", "p")));
    }
}
