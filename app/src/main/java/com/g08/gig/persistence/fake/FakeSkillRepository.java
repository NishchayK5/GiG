package com.g08.gig.persistence.fake;
import com.g08.gig.models.Pro;
import com.g08.gig.persistence.SkillRepository;

import java.util.HashMap;
import java.util.Map;

public class FakeSkillRepository implements SkillRepository {

    private final Map<Integer, String[]> proToSkillMap;
    public FakeSkillRepository(){
        proToSkillMap = new HashMap<>();
    }

    public String[] getAllSkillsByPro(Pro pro){
        if(pro==null) return null;

        return proToSkillMap.getOrDefault(pro.getId(), new String[0]);
    }

    public boolean addSkillsForPro(int proId, String[] skill ){
        proToSkillMap.put(proId, skill);
        return true;
    }

    public boolean deleteAllSkillsByProId(int proId){
        return proToSkillMap.remove(proId)!=null;
    }
}

