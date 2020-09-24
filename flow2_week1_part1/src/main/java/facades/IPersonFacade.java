
package facades;

import dto.PersonDTO;
import dto.PersonsDTO;

public interface IPersonFacade {
  public PersonDTO addPerson(String fName, String lName, String phone);  
  public PersonDTO deletePerson(int id);  
  public PersonDTO getPerson(int id);  
  public PersonsDTO getAllPersons();  
  public PersonDTO editPerson(PersonDTO p);  
}

