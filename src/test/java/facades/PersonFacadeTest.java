package facades;

import dto.ExceptionDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    
    private Person p1 = new Person("Kurt", "Kurbad", "12345689");
    private Person p2 = new Person("Hansi", "Nantitis", "84756373");
    private Person p3 = new Person("Uffe", "Ufimaven", "24354635");
    
    private String pNotFoundMsg = "No person with provided id found";
    private String pMisInpMsg = "First name and/or last name is missing";
    private String pNotFoundDeleteMsg = "Could not delete, provided id does not exist";
    

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
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

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }
    
    @Test
    public void getAllPersonsTest() {
        List<Person> preExpected = new ArrayList();
        preExpected.add(p1);
        preExpected.add(p2);
        preExpected.add(p3);
        PersonsDTO expected = new PersonsDTO(preExpected);
        PersonsDTO result = facade.getAllPersons();
        
        assertEquals(expected.size(), result.size());
    }
    
    @Test
    public void getPersonTest() throws PersonNotFoundException {
        PersonDTO expected = new PersonDTO(p1);
        PersonDTO result = facade.getPerson(p1.getId());
        
        assertEquals(expected.getId(), result.getId());
    }
    
    
    @Test()
    public void getPersonTestNegative() {
        Exception exception = assertThrows(PersonNotFoundException.class, () -> {
            facade.getPerson(10);
        });
        
        String expectedMessage = pNotFoundMsg;
        String resultMessage = exception.getMessage();
        
        assertTrue(resultMessage.contains(expectedMessage));
    }
    
    @Test
    public void fillDBTest() throws PersonNotFoundException {
        PersonDTO expected = new PersonDTO(p1);
        facade.fillDB();
        PersonDTO result = facade.getPerson(p1.getId());
        
        // Check if the p1 id is still the same.
        assertEquals(expected.getId(), result.getId());
    }

    @Test
    public void addPersonTest() throws MissingInputException {
        Person newP = new Person("Niels", "Johansen", "13243546");
        PersonDTO expected = new PersonDTO(newP);
        PersonDTO result = facade.addPerson(newP.getFirstName(), newP.getLastName(), newP.getPhone());
        
        assertEquals(expected.getPhone(), result.getPhone());
    }
    
    @Test
    public void addPersonNegativeTest() {
        Exception exception = assertThrows(MissingInputException.class, () -> {
            facade.addPerson("", "Hansen", "99999999");
        });
        
        String expectedMessage = pMisInpMsg;
        String resultMessage = exception.getMessage();
        
        assertTrue(resultMessage.contains(expectedMessage));
    }
    
    @Test
    public void editPersonTest() throws PersonNotFoundException, MissingInputException {
        PersonDTO expected = new PersonDTO(p1);
        PersonDTO result = facade.getPerson(p1.getId());
        
        // Check if the two are the same.
        assertEquals(expected.getFirstName(), result.getFirstName());
        
        expected.setFirstName("Gert");
        result = facade.editPerson(result);
        
        // Check if result's name has changed.
        assertNotEquals(expected.getFirstName(), result.getFirstName());
    }
    
    @Test
    public void editPersonNegativeTest() {
        PersonDTO pNotFound = new PersonDTO("Person", "Notfound", "99999999");
        PersonDTO pMisInp = new PersonDTO(p1);
        pMisInp.setFirstName("");
        
        ////////////////////////////////////////
        // Testing person not found exception //
        ////////////////////////////////////////
        Exception exception = assertThrows(PersonNotFoundException.class, () -> {
            facade.editPerson(pNotFound);
        });
        
        String expectedMessage = pNotFoundMsg;
        String resultMessage = exception.getMessage();
        
        assertTrue(resultMessage.contains(expectedMessage));
        
        
        /////////////////////////////////////
        // Testing missing input exception //
        /////////////////////////////////////
        exception = assertThrows(MissingInputException.class, () -> {
            facade.editPerson(pMisInp);
        });
        
        expectedMessage = pMisInpMsg;
        resultMessage = exception.getMessage();
        
        assertTrue(resultMessage.contains(expectedMessage));
    }
    
    @Test
    public void deletePersonTest() throws PersonNotFoundException {
        PersonDTO expected = new PersonDTO(p1);
        PersonDTO result = facade.deletePerson(p1.getId());
        
        // Check if they are the same.
        assertEquals(expected.getId(), result.getId());
        
        PersonsDTO all = facade.getAllPersons();
        
        // Check if p1 is deleted from all.
        assertEquals(false, all.contains(result));
    }
    
    @Test
    public void deletePersonNegativeTest() {
        Exception exception = assertThrows(PersonNotFoundException.class, () -> {
            facade.deletePerson(10);
        });
        
        String expectedMessage = pNotFoundDeleteMsg;
        String resultMessage = exception.getMessage();
        
        System.out.println(resultMessage);
        
        assertTrue(resultMessage.contains(expectedMessage));
    }

}
