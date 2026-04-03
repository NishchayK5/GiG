package com.g08.gig.business.services;

import com.g08.gig.business.constants.FieldLimits;
import com.g08.gig.business.constants.ValidationMessages;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;
import com.g08.gig.persistence.ReviewRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository repo;

    @InjectMocks
    private ReviewServiceImpl service;

    private Client reviewer;
    private Pro pro;

    @BeforeEach
    void setUp() {
        reviewer = new Client(
                1,
                "client@test.com",
                "Dayaveer",
                "Gill",
                "2041111111",
                "123 Main St",
                "hashedPass"
        );

        pro = new Pro(
                2,
                "pro@test.com",
                "Alex",
                "Smith",
                "2042222222",
                "hashedPass",
                new String[]{"Electrical"},
                "Winnipeg"
        );
    }

    @Test
    void addReview_validInput_trimsTitleAndReturnsStoredReview() {
        Review stored = new Review(10, reviewer, pro, "Great Work", "Very professional", 5);
        when(repo.add(any(Review.class))).thenReturn(stored);

        Review result = service.addReview(reviewer, pro, "  Great Work  ", "Very professional", 5);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Great Work", result.getTitle());
        assertEquals("Very professional", result.getBody());
        assertEquals(5, result.getStars());

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(repo).add(captor.capture());

        Review passed = captor.getValue();
        assertEquals(0, passed.getId());
        assertEquals(reviewer, passed.getReviewer());
        assertEquals(pro, passed.getPro());
        assertEquals("Great Work", passed.getTitle());
        assertEquals("Very professional", passed.getBody());
        assertEquals(5, passed.getStars());
    }

    @Test
    void addReview_nullTitle_throwsValidationException() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                service.addReview(reviewer, pro, null, "Body", 5));

        assertEquals(ValidationMessages.REQUEST_TITLE_REQUIRED, ex.getMessage());
        assertEquals("title", ex.getField());
        verify(repo, never()).add(any());
    }

    @Test
    void addReview_blankTitle_throwsValidationException() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                service.addReview(reviewer, pro, "   ", "Body", 5));

        assertEquals(ValidationMessages.REQUEST_TITLE_REQUIRED, ex.getMessage());
        assertEquals("title", ex.getField());
        verify(repo, never()).add(any());
    }

    @Test
    void addReview_titleTooLong_throwsValidationException() {
        String longTitle = "x".repeat(FieldLimits.TITLE_MAX_LENGTH + 1);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                service.addReview(reviewer, pro, longTitle, "Body", 5));

        assertEquals(ValidationMessages.TITLE_MAX_LENGTH, ex.getMessage());
        assertEquals("title", ex.getField());
        verify(repo, never()).add(any());
    }

    @Test
    void getAllReviewsForPro_returnsRepoResults() {
        Review r1 = new Review(1, reviewer, pro, "Good", "Body 1", 4);
        Review r2 = new Review(2, reviewer, pro, "Excellent", "Body 2", 5);

        when(repo.getAllReviewsForPro(pro)).thenReturn(List.of(r1, r2));

        List<Review> result = service.getAllReviewsForPro(pro);

        assertEquals(2, result.size());
        assertEquals("Good", result.get(0).getTitle());
        assertEquals("Excellent", result.get(1).getTitle());
        verify(repo).getAllReviewsForPro(pro);
    }

    @Test
    void deleteReview_existingId_returnsTrue() {
        when(repo.delete(5)).thenReturn(true);

        boolean result = service.deleteReview(5);

        assertTrue(result);
        verify(repo).delete(5);
    }

    @Test
    void deleteReview_missingId_returnsFalse() {
        when(repo.delete(999)).thenReturn(false);

        boolean result = service.deleteReview(999);

        assertFalse(result);
        verify(repo).delete(999);
    }

    @Test
    void getReviewById_existingId_returnsReview() {
        Review stored = new Review(7, reviewer, pro, "Solid", "Nice work", 4);
        when(repo.getById(7)).thenReturn(stored);

        Review result = service.getReviewById(7);

        assertNotNull(result);
        assertEquals(7, result.getId());
        assertEquals("Solid", result.getTitle());
        verify(repo).getById(7);
    }

    @Test
    void getReviewById_missingId_returnsNull() {
        when(repo.getById(404)).thenReturn(null);

        Review result = service.getReviewById(404);

        assertNull(result);
        verify(repo).getById(404);
    }
}