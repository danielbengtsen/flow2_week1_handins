package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void fillDB() {
        EntityManager em = getEntityManager();
        Person p1 = new Person("Kurt", "Kurbad", "13243546");
        Person p2 = new Person("Lars", "Lilulilalida", "87654321");
        Person p3 = new Person("Hov", "Dada", "15263744");
        Address a1 = new Address("Kurtsvej 37", "0000", "Kurtsby");
        Address a2 = new Address("Larsvej 87", "1111", "Larsby");
        Address a3 = new Address("Hovsvej 13", "2222", "Hovsby");
        p1.setAddress(a1);
        p2.setAddress(a2);
        p3.setAddress(a3);
        
        try {
            em.getTransaction().begin();
                em.createNamedQuery("Person.deleteAllRows").executeUpdate();
                em.createNamedQuery("Address.deleteAllRows").executeUpdate();
                em.createNativeQuery("ALTER TABLE PERSON AUTO_INCREMENT = 1").executeUpdate();
                em.createNativeQuery("ALTER TABLE ADDRESS AUTO_INCREMENT = 1").executeUpdate();
                em.persist(p1);
                em.persist(p2);
                em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String zip, String city) throws MissingInputException {
        EntityManager em = getEntityManager();
        
        Person p = null;
        Address a = null;
        
        if(fName.isEmpty() || lName.isEmpty()) {
            throw new MissingInputException("First name and/or last name is missing");
        } else if(street.isEmpty() || zip.isEmpty() || city.isEmpty()) {
            throw new MissingInputException("Street, zip and/or city is missing");
        } else {
            try {
                em.getTransaction().begin();
                    p = new Person(fName, lName, phone);
                    a = new Address(street, zip, city);
                    p.setAddress(a);
                    em.persist(p);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
            return new PersonDTO(p);    
        }
    }

    @Override
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        
        Person p = null;
        
        try {
                p = em.find(Person.class, id);
                if(p == null) {
                    throw new PersonNotFoundException("Could not delete, provided id does not exist");
                } else {
                    em.getTransaction().begin();
                        em.remove(p);
                        em.remove(p.getAddress());
                    em.getTransaction().commit();
                    return new PersonDTO(p);
                }
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO getPerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person p; 
        
        try {
            p = em.find(Person.class, id);
            if(p == null) {
                throw new PersonNotFoundException("No person with provided id found");
            } else {
                return new PersonDTO(p);
            }
        } finally {
            em.close();
        }
            
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = getEntityManager();
        
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> pList = query.getResultList();
        PersonsDTO persons = new PersonsDTO(pList);
        
        return persons;
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws MissingInputException, PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person pers = null;
        Address addr = null;
        
        if(p.getFirstName().isEmpty() || p.getLastName().isEmpty()) {
            throw new MissingInputException("First name and/or last name is missing");
        } else if(p.getStreet().isEmpty() || p.getZip().isEmpty() || p.getCity().isEmpty()) {
            throw new MissingInputException("Street, zip and/or city is missing");
        } else {
            try {
                pers = em.find(Person.class, p.getId());
                if(pers == null) {
                    throw new PersonNotFoundException("No person with provided id found");
                } else {
                    em.getTransaction().begin();
                        pers = new Person(p.getFirstName(), p.getLastName(), p.getPhone());
                        addr = new Address(p.getStreet(), p.getZip(), p.getCity());
                        pers.setAddress(addr);
                        em.persist(pers);
                    em.getTransaction().commit();
                    return new PersonDTO(pers);
                }
            } finally {
                em.close();
            }
        }
    }

}
