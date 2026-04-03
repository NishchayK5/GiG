package com.g08.gig.persistence.fake;

import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeReviewRepositoryTest {

    private FakeReviewRepository repo;

    private Client reviewer1;
    private Client reviewer2;
    private Pro pro1;
    private Pro pro2;

    @BeforeEach
    void setUp() {
        repo = new FakeReviewRepository();

        reviewer1 = new Client(1, "client1@test.com", "Dayaveer", "Gill", "2041111111", "123 Main St", "pass");
        reviewer2 = new Client(2, "client2@test.com", "Aman", "Singh", "2042222222", "456 Main St", "pass");

        pro1 = new Pro(10, "pro1@test.com", "Alex", "Smith", "2043333333", "pass",
                new String[]{"Electrical"}, "Winnipeg");
        pro2 = new Pro(11, "pro2@test.com", "Sam", "Brown", "2044444444", "pass",
                new String[]{"Plumbing"}, "Winnipeg");
    }

    @Test
    void add_nullReview_returnsNull() {
        assertNull(repo.add(null));
    }

    @Test
    void add_withIdZero_assignsNewIdAndStoresReview() {
        Review review = new Review(0, reviewer1, pro1, "Great Work", "Very professional", 5);

        Review added = repo.add(review);

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals("Great Work", added.getTitle());
        assertEquals("Very professional", added.getBody());
        assertEquals(5, added.getStars());
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void add_withExplicitUniqueId_storesReview() {
        Review added = repo.add(new Review(50, reviewer1, pro1, "Solid", "Good job", 4));

        assertNotNull(added);
        assertEquals(50, added.getId());
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void add_duplicateId_returnsNull() {
        Review first = repo.add(new Review(50, reviewer1, pro1, "First", "Body", 4));
        Review duplicate = repo.add(new Review(50, reviewer2, pro2, "Second", "Body", 5));

        assertNotNull(first);
        assertNull(duplicate);
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void getById_existingId_returnsReview() {
        Review added = repo.add(new Review(0, reviewer1, pro1, "Great Work", "Very professional", 5));

        Review found = repo.getById(added.getId());

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
    }

    @Test
    void getById_missingId_returnsNull() {
        assertNull(repo.getById(999));
    }

    @Test
    void delete_existingId_removesReview() {
        Review added = repo.add(new Review(0, reviewer1, pro1, "Great Work", "Very professional", 5));

        boolean deleted = repo.delete(added.getId());

        assertTrue(deleted);
        assertNull(repo.getById(added.getId()));
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void delete_missingId_returnsFalse() {
        assertFalse(repo.delete(999));
    }

    @Test
    void getAllReviewsForPro_returnsOnlyReviewsForThatPro() {
        Review r1 = repo.add(new Review(0, reviewer1, pro1, "Good", "Body 1", 4));
        Review r2 = repo.add(new Review(0, reviewer2, pro1, "Excellent", "Body 2", 5));
        repo.add(new Review(0, reviewer1, pro2, "Okay", "Body 3", 3));

        List<Review> result = repo.getAllReviewsForPro(pro1);

        assertEquals(2, result.size());
        assertEquals(pro1, result.get(0).getPro());
        assertEquals(pro1, result.get(1).getPro());
        assertEquals(r1.getId(), result.get(0).getId());
        assertEquals(r2.getId(), result.get(1).getId());
    }

    @Test
    void getAllReviewsForPro_noMatches_returnsEmptyList() {
        repo.add(new Review(0, reviewer1, pro2, "Okay", "Body", 3));

        List<Review> result = repo.getAllReviewsForPro(pro1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllReviewsForPro_nullPro_returnsNull() {
        assertNull(repo.getAllReviewsForPro(null));
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        repo.add(new Review(0, reviewer1, pro1, "Good", "Body", 4));

        assertThrows(UnsupportedOperationException.class, () ->
                repo.getAll().add(new Review(99, reviewer2, pro2, "Bad", "Body", 1)));
    }
    @Test
    void getById_returnsNullWhenOnlyNonMatchingReviewsExist() {



        repo.add(new Review(10, reviewer1, pro1, "Title 1", "Body 1", 5));
        repo.add(new Review(20, reviewer1, pro1, "Title 2", "Body 2", 4));

        Review result = repo.getById(999);

        assertNull(result);
    }
    @Test
    void delete_checksNonMatchingReviewBeforeDeletingMatchingReview() {


        repo.add(new Review(10, reviewer1, pro1, "Title 1", "Body 1", 5));
        repo.add(new Review(20, reviewer1, pro1, "Title 2", "Body 2", 4));

        boolean deleted = repo.delete(20);

        assertTrue(deleted);
        assertNotNull(repo.getById(10));
        assertNull(repo.getById(20));
    }
}