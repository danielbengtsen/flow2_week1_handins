package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import entities.Person;
import entities.RenameMe;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static RenameMe r1,r2;
    
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private Person p1 = new Person("Kurt", "Kurbad", "12345689");
    private Person p2 = new Person("Hansi", "Nantitis", "84756373");
    private Person p3 = new Person("Uffe", "Ufimaven", "24354635");
    
    private String pNotFoundMsg = "No person with provided id found";
    private String pMisInpMsg = "First name and/or last name is missing";
    private String pNotFoundDeleteMsg = "Could not delete, provided id does not exist";
    
    private static final Gson GSON = new GsonBuilder().create();


    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer(){
        //System.in.read();
         //Don't forget this, if you called its counterpart in @BeforeAll
         EMF_Creator.endREST_TestWithDB();
         httpServer.shutdownNow();
    }
    
    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
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
    
    @Test
    public void testServerIsUp() {
        System.out.println("Testing server is UP");
        given().when().get("/person").then().statusCode(200);
    }
   
    @Test
    public void testDummyMsg() throws Exception {
        given()
        .contentType("application/json")
        .get("/person").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("msg", equalTo("Success!"));   
    }
    
    @Test
    public void fillDBTest() {
        given()
        .contentType("application/json")
        .get("/person/filldb").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("msg", equalTo("Success filling db!"));
    }
    
    @Test
    public void getPersonTest() {
        given()
        .contentType("application/json")
        .get("/person/getperson/" + p1.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("phone", equalTo(p1.getPhone()));
    }
    
    @Test
    public void getPersonNegativeTest() {
        given()
        .contentType("application/json")
        .get("/person/getperson/100").then()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))
        .body("message", equalTo(pNotFoundMsg));
    }
    
    @Test
    public void getAllPersonsTest() {
        given()
        .contentType("application/json")
        .get("/person/getallpersons").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("all", hasSize(3));
    }
    
    @Test
    public void addPersonTest() {
        PersonDTO pDTO = new PersonDTO("Dani", "Danimann", "13243546");
        
        given()
        .contentType("application/json")
        .body(pDTO)
        .post("person").then()
        .body("firstName", equalTo(pDTO.getFirstName()))
        .body("lastName", equalTo(pDTO.getLastName()))
        .body("phone", equalTo(pDTO.getPhone()))
        .body("id", equalTo(4));
    }
    
    @Test
    public void addPersonNegativeTest() {
        PersonDTO pDTO = new PersonDTO("", "Danimann", "13243546");
        
        given()
        .contentType("application/json")
        .body(pDTO)
        .post("person").then()
        .assertThat()
        .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
        .body("code", equalTo(400))
        .body("message", equalTo(pMisInpMsg));
    }
    
    @Test
    public void editPersonTest() {
        PersonDTO pDTO = new PersonDTO("Dani", "Danimann", "13243546");
        
        given()
        .contentType("application/json")
        .body(pDTO)
        .put("person/" + p1.getId()).then()
        .body("firstName", equalTo(pDTO.getFirstName()))
        .body("lastName", equalTo(pDTO.getLastName()))
        .body("phone", equalTo(pDTO.getPhone()))
        .body("id", equalTo(p1.getId()));
    }
    
    @Test
    public void editPersonNegativeTest() {
        PersonDTO pNotFound = new PersonDTO("Dani", "Danimann", "13243546");
        PersonDTO pMisInp = new PersonDTO("", "Danimann", "13243546");
        
        ////////////////////////////////////////
        // Testing person not found exception //
        ////////////////////////////////////////
        given()
        .contentType("application/json")
        .body(pNotFound)
        .put("person/" + pNotFound.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))
        .body("message", equalTo(pNotFoundMsg));
        
        
        /////////////////////////////////////
        // Testing missing input exception //
        /////////////////////////////////////
        given()
        .contentType("application/json")
        .body(pMisInp)
        .put("person/" + p1.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
        .body("code", equalTo(400))
        .body("message", equalTo(pMisInpMsg));
    }
    
    @Test
    public void deletePersonTest() {
        given()
        .contentType("application/json")
        .delete("person/" + p1.getId()).then()
        .body("firstName", equalTo(p1.getFirstName()))
        .body("lastName", equalTo(p1.getLastName()))
        .body("phone", equalTo(p1.getPhone()))
        .body("id", equalTo(p1.getId()));
    }
    
    @Test
    public void deletePersonNegativeTest() {
        given()
        .contentType("application/json")
        .delete("person/" + 10).then()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))
        .body("message", equalTo(pNotFoundDeleteMsg));
    }
    
    
}
