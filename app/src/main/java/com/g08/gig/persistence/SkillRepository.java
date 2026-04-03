/**
 * File: SkillRepository.java

 * Purpose:
 * This class is responsible for all the retrieval and storage of skills of the pro to the database.
 *
 * Note:
 * Skills are stored in a separate table to eliminate redundancy and ensure the database adheres to BCNF.
 */
package com.g08.gig.persistence;
import com.g08.gig.models.Pro;

public interface SkillRepository {
    String[] getAllSkillsByPro(Pro pro);
    boolean addSkillsForPro(int proId, String[] skills);
    boolean deleteAllSkillsByProId(int proID);
}


