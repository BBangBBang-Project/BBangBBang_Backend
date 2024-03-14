package test.bbang.repository;


import org.springframework.stereotype.Repository;
import test.bbang.Entity.Bread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class TestRepository {
    private static final Map<Long, Bread> store = new HashMap<>();
    private static long sequence = 0L;
    public void save(Bread bread){

        Bread findBread = findByName(bread.getName());

        if(findBread != null){
            findBread.setStock(bread.getStock()+findBread.getStock());
        }
        else{
            bread.setId(++sequence);
            store.put(bread.getId(),bread);
        }
    }

    public Bread findByName(String name){
        for (Bread bread : store.values()) {
            if (bread.getName().equals(name)) {
                return bread;
            }
        }
        return null;
    }

    public List<Bread> findAll(){
        return new ArrayList<>(store.values());
    }

    public void update(Long id, Bread updateParam) {

    }

    public void clearStore(){
        store.clear();
    }
}
