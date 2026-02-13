//package com.g08.gig.business.services;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.g08.gig.business.validation.UserValidator;
//import com.g08.gig.models.Client;
//import com.g08.gig.persistence.fake.FakeClientRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//
//public class ClientuserServiceImplTest {
//    private static final String VALID_FIRST = "ClientTest";
//    private static final String VALID_LAST = "Tester";
//    private static final String VALID_EMAIL = "client@example.com";
//
//    private static final String VALID_PHONE = "1234567890";
//
//    private static final String VALID_PASSWORD = "password123";
//
//    private static final String VALID_ADDRESS = "Testing valid Address";
//
//
//    private FakeClientRepository repo;
//    private ClientUserServiceImpl service;
//
//
//    @BeforeEach
//    void setUp() {
//        repo = new FakeClientRepository();
//        service = new ClientUserServiceImpl(repo, new UserValidator());
//    }
//
//    @Test
//    void addClient_withValidData_returnsClientAndStoresInRepo() {
//        Client added = service.addClient(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE, VALID_PASSWORD, VALID_PASSWORD);
//
//        assertNotNull(added);
//        assertTrue(added.getId() > 0);
//    }
//}
