package com.g08.gig.persistence.fake;

import com.g08.gig.models.Pro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeSkillRepositoryTest {

    private FakeSkillRepository repo;
    private Pro pro1;
    private Pro pro2;

    @BeforeEach
    void setUp() {
        repo = new FakeSkillRepository();

        pro1 = new Pro(
                1,
                "pro1@test.com",
                "Dayaveer",
                "Gill",
                "2041111111",
                "pass",
                new String[]{"Electrical"},
                "Winnipeg"
        );

        pro2 = new Pro(
                2,
                "pro2@test.com",
                "Izzy",
                "Elskamp",
                "2042222222",
                "pass",
                new String[]{"Plumbing"},
                "Winnipeg"
        );
    }

    @Test
    void getAllSkillsByPro_nullPro_returnsNull() {
        assertNull(repo.getAllSkillsByPro(null));
    }

    @Test
    void getAllSkillsByPro_whenNoSkillsAdded_returnsEmptyArray() {
        String[] result = repo.getAllSkillsByPro(pro1);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void addSkillsForPro_storesSkillsAndReturnsTrue() {
        String[] skills = {"Electrical", "Wiring", "Lighting"};

        boolean added = repo.addSkillsForPro(pro1.getId(), skills);
        String[] result = repo.getAllSkillsByPro(pro1);

        assertTrue(added);
        assertArrayEquals(skills, result);
    }

    @Test
    void addSkillsForPro_overwritesExistingSkillsForSamePro() {
        repo.addSkillsForPro(pro1.getId(), new String[]{"Electrical"});
        repo.addSkillsForPro(pro1.getId(), new String[]{"Plumbing", "Pipes"});

        String[] result = repo.getAllSkillsByPro(pro1);

        assertArrayEquals(new String[]{"Plumbing", "Pipes"}, result);
    }

    @Test
    void addSkillsForPro_differentPros_storeSeparateSkillSets() {
        repo.addSkillsForPro(pro1.getId(), new String[]{"Electrical", "Lighting"});
        repo.addSkillsForPro(pro2.getId(), new String[]{"Plumbing", "Drainage"});

        assertArrayEquals(new String[]{"Electrical", "Lighting"}, repo.getAllSkillsByPro(pro1));
        assertArrayEquals(new String[]{"Plumbing", "Drainage"}, repo.getAllSkillsByPro(pro2));
    }

    @Test
    void deleteAllSkillsByProId_existingProId_returnsTrueAndRemovesSkills() {
        repo.addSkillsForPro(pro1.getId(), new String[]{"Electrical", "Wiring"});

        boolean deleted = repo.deleteAllSkillsByProId(pro1.getId());

        assertTrue(deleted);
        assertArrayEquals(new String[0], repo.getAllSkillsByPro(pro1));
    }

    @Test
    void deleteAllSkillsByProId_missingProId_returnsFalse() {
        assertFalse(repo.deleteAllSkillsByProId(999));
    }
}