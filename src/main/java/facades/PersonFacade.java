package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import entities.RenameMe;
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
        
        try {
            em.getTransaction().begin();
                em.createNamedQuery("Person.deleteAllRows").executeUpdate();
                em.createNativeQuery("ALTER TABLE PERSON AUTO_INCREMENT = 1").executeUpdate();
                em.persist(p1);
                em.persist(p2);
                em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone) {
        EntityManager em = getEntityManager();
        
        Person p = new Person(fName, lName, phone);
        
        try {
            em.getTransaction().begin();
                em.persist(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        return new PersonDTO(p);
    }

    @Override
    public PersonDTO deletePerson(int id) {
        EntityManager em = getEntityManager();
        
        PersonDTO pDTO = null;
        
        try {
            em.getTransaction().begin();
                pDTO = new PersonDTO(em.find(Person.class, id));
                Query query = em.createQuery("DELETE FROM Person p WHERE p.id = :id", Person.class);
                query.setParameter("id", id);
                query.executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        return pDTO;
    }

    @Override
    public PersonDTO getPerson(int id) {
        EntityManager em = getEntityManager();
        
        Person p = em.find(Person.class, id);
            
        return new PersonDTO(p);
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
    public PersonDTO editPerson(PersonDTO p) {
        EntityManager em = getEntityManager();
        Person pers = new Person();
        
        try {
            em.getTransaction().begin();
                pers = em.find(Person.class, p.getId());
                pers.setFirstName(p.getFirstName());
                pers.setLastName(p.getLastName());
                pers.setPhone(p.getPhone());
                em.persist(pers);
            em.getTransaction().commit();
            return new PersonDTO(pers);
        } finally {
            em.close();
        }
      
    }

}
