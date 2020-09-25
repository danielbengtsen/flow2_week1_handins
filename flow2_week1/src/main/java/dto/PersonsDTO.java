/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Danie
 */
public class PersonsDTO {
    
    List<PersonDTO> all = new ArrayList();

    public PersonsDTO(List<Person> personEntities) {
        personEntities.forEach((p) -> {
            all.add(new PersonDTO(p));
        });

    }
    
    public int size() {
        int count = 0;
        for(PersonDTO pdto : all) {
            count++;
        }
        return count;
    }
    
    public boolean contains(PersonDTO pDTO) {
        if(all.contains(pDTO)) {
            return true;
        } else {
            return false;
        }
    }
    
}
