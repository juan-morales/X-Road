package org.niis.xroad.centralserver.restapi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.niis.xroad.centralserver.restapi.repository.SecurityServerClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@WithMockUser
public class ClientServiceIntegrationTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private SecurityServerClientRepository securityServerClientRepository;

    @Test
    public void findSubsystems() {
        var clients = securityServerClientRepository.findAll(SecurityServerClientRepository.isSubsystem());
        assertEquals(1, clients.size());
    }

    @Test
    public void findSubsystemsWithCode() {
        // this works, yay!
        var clients = securityServerClientRepository.findAll(
                SecurityServerClientRepository.isSubsystemAndCodeIs("SS1"));
        assertEquals(1, clients.size());
        clients = securityServerClientRepository.findAll(
                SecurityServerClientRepository.isSubsystemAndCodeIs("SS2"));
        assertEquals(0, clients.size());
    }


    @Test
    public void findSubsystemsWithName() {
        var clients = securityServerClientRepository.findAll(
                SecurityServerClientRepository.isSubsystemAndMembernameIs("Member1"));
        assertEquals(1, clients.size());
        clients = securityServerClientRepository.findAll(
                SecurityServerClientRepository.isSubsystemAndMembernameIs("Member2"));
        assertEquals(0, clients.size());
    }

    @Test
    public void findAllUsingSecurityServerClients() {
        var clients = clientService.findAll();
        assertEquals(10, clients.size());
    }

    @Test
    public void findByNameUsingSecurityServerClients() {
        assertEquals(10, clientService.find("e").size());
        assertEquals(10, clientService.find("E").size());
        assertEquals(2, clientService.find("1").size());
        assertEquals(1, clientService.find("3").size());
        assertEquals(1, clientService.find("member9").size());
        assertEquals(0, clientService.find("member9 ").size());
        assertEquals(0, clientService.find("eqwuity").size());
    }

    @Test
    public void findByNameUsingSecurityServerClientsNameIs() {
        assertEquals(1, clientService.findNameIs("member9").size());
        assertEquals(0, clientService.findNameIs("member9 ").size());
    }


    @Test
    public void findAllUsingXroadMembers() {
        var clients = clientService.findAll2();
        assertEquals(10, clients.size());
    }

    @Test
    public void findByNameUsingXroadMembers() {
        assertEquals(1, clientService.find2("member9").size());
        assertEquals(10, clientService.find2("e").size());
        int foo = 2;
    }



}
