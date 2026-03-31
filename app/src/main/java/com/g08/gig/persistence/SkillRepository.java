package com.g08.gig.persistence;
import com.g08.gig.models.Pro;

public interface SkillRepository {
    String[] getAllSkillsByPro(Pro pro);
    boolean addSkillsForPro(int proId, String[] skills);
    boolean deleteAllSkillsByProId(int proID);
}


