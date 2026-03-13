package com.g08.gig.persistence.fake;

import com.g08.gig.models.Pro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeProRepositoryTest {

    private FakeProRepository repo;
    private FakeUserRepository userRepo;

    @BeforeEach
    void setUp() {
        userRepo = new FakeUserRepository();
        repo = new FakeProRepository(userRepo);
    }

    @Test
    void add_assignsIdWhenZero() {
        Pro pro = new Pro(0, "p@b.com", "P", "B", "555", "secret", new String[]{"Plumbing"}, "Boston");
        Pro added = repo.add(pro);
        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals("p@b.com", added.getEmail());
        assertArrayEquals(new String[]{"Plumbing"}, added.getSkillSet());
        assertEquals("Boston", added.getCityOfOperation());
        assertEquals("secret", added.getPassword());
    }

    @Test
    void add_handlesNullSkillSet() {
        Pro pro = new Pro(0, "p@b.com", "P", "B", "555", "pwd", null, "NYC");
        Pro added = repo.add(pro);
        assertNotNull(added);
        assertNull(added.getSkillSet());
        assertEquals("NYC", added.getCityOfOperation());
    }

    @Test
    void getById_returnsAddedPro() {
        Pro added = repo.add(new Pro(0, "x@y.com", "X", "Y", "555", "pwd", new String[]{"HVAC"}, "Chicago"));
        Pro found = repo.getById(added.getId());
        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
        assertEquals("x@y.com", found.getEmail());
        assertArrayEquals(new String[]{"HVAC"}, found.getSkillSet());
        assertEquals("Chicago", found.getCityOfOperation());
    }

    @Test
    void getById_returnsNullForUnknownId() {
        assertNull(repo.getById(999));
    }

    @Test
    void update_modifiesExistingPro() {
        Pro added = repo.add(new Pro(0, "old@x.com", "Old", "Name", "5555555555", "p", new String[]{"A"}, "City1"));

        userRepo.add(new Pro(
                added.getId(),
                added.getEmail(),
                added.getFirstName(),
                added.getLastName(),
                added.getPhoneNumber(),
                added.getPassword(),
                added.getSkillSet(),
                added.getCityOfOperation()
        ));

        Pro updated = new Pro(added.getId(), "new@x.com", "New", "Name", "9999999999", "p2", new String[]{"A", "B"}, "City2");
        assertTrue(repo.update(updated));

        Pro found = repo.getById(added.getId());
        assertEquals("new@x.com", found.getEmail());
        assertArrayEquals(new String[]{"A", "B"}, found.getSkillSet());
        assertEquals("City2", found.getCityOfOperation());
        assertEquals("p2", found.getPassword());
    }

    @Test
    void update_returnsFalseWhenProNull() {
        assertFalse(repo.update(null));
    }

    @Test
    void update_returnsFalseWhenIdNotExists() {
        Pro pro = new Pro(999, "x@x.com", "X", "X", "555", "p", null, "City");
        assertFalse(repo.update(pro));
    }

    @Test
    void delete_removesPro() {
        Pro added = repo.add(new Pro(0, "d@d.com", "D", "D", "555", "p", null, "City"));
        assertTrue(repo.delete(added.getId()));
        assertNull(repo.getById(added.getId()));
    }

    @Test
    void delete_returnsFalseForUnknownId() {
        assertFalse(repo.delete(999));
    }

    @Test
    void add_returnsNullWhenProNull() {
        assertNull(repo.add(null));
    }

    @Test
    void add_proWithExistingId_returnsNull() {
        Pro first = repo.add(new Pro(0, "a@a.com", "A", "A", "555", "p", null, "City"));
        assertNotNull(first);
        Pro duplicate = new Pro(first.getId(), "b@b.com", "B", "B", "555", "p", null, "City2");
        assertNull(repo.add(duplicate));
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void update_returnsFalseWhenUserNotInUserRepo() {
        Pro added = repo.add(new Pro(0, "x@x.com", "X", "Y", "555", "p", null, "City"));
        Pro updated = new Pro(added.getId(), "new@x.com", "New", "Y", "999", "p2", null, "City2");
        assertFalse(repo.update(updated));
    }

    @Test
    void getAll_initiallyEmpty() {
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void getAll_returnsAllAddedPros() {
        repo.add(new Pro(0, "a@a.com", "A", "A", "555", "p", null, "City1"));
        repo.add(new Pro(0, "b@b.com", "B", "B", "555", "p", null, "City2"));
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        repo.add(new Pro(0, "a@a.com", "A", "A", "555", "p", null, "City"));
        assertThrows(UnsupportedOperationException.class, () ->
                repo.getAll().add(new Pro(1, "b@b.com", "B", "B", "555", "p", null, "City")));
    }
}
