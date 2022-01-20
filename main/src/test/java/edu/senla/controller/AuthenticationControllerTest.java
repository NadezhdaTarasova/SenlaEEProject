package edu.senla.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.senla.dao.ClientRepositoryInterface;
import edu.senla.dao.CourierRepositoryInterface;
import edu.senla.dao.RoleRepositoryInterface;
import edu.senla.dto.AuthRequestDTO;
import edu.senla.dto.CourierAuthRequestDTO;
import edu.senla.entity.Client;
import edu.senla.entity.Courier;
import edu.senla.enums.Roles;
import edu.senla.security.JwtProvider;
import edu.senla.service.ClientService;
import edu.senla.service.CourierService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private ClientService clientService;

    @SpyBean
    private CourierService courierService;

    @SpyBean
    private JwtProvider jwtProvider;

    @SpyBean
    private ClientRepositoryInterface clientRepository;

    @SpyBean
    private CourierRepositoryInterface courierRepository;

    @SpyBean
    private RoleRepositoryInterface roleRepository;

    private Client client;

    private Courier courier;

    @SneakyThrows
    @BeforeEach
    void creteUsersToOperateWith() {
        client = new Client();
        client.setFirstName("client");
        client.setLastName("client");
        client.setEmail("client");
        client.setPhone("client");
        client.setUsername("client");
        client.setPassword(passwordEncoder.encode("client"));
        client.setRole(roleRepository.getByName(Roles.ROLE_USER.toString()));
        Client client1 = clientRepository.save(client);
        System.out.println(client1);

        courier = new Courier();
        courier.setFirstName("courier");
        courier.setLastName("courier");
        courier.setPhone("courier");
        courier.setPassword(passwordEncoder.encode("courier"));
        courierRepository.save(courier);
    }

    @SneakyThrows
    @Test
    void testAuthenticateClientWhenJsonIsIncorrectBadRequestStatus() {
        String incorrectJson = "incorrectJson";
        mockMvc.perform(MockMvcRequestBuilders
                .get("/authentication/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(incorrectJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(clientService, never()).getClientByUsernameAndPassword(any(), any());
        verify(jwtProvider, never()).generateToken(any());
    }

    @SneakyThrows
    @Test
    void testAuthenticateNonExistentClientNonFoundStatus() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("notExistentClient");
        authRequestDTO.setPassword("notExistentClient");
        String authRequestJson = mapper.writeValueAsString(authRequestDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/authentication/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(clientService, times(1)).getClientByUsernameAndPassword(any(), any());
        verify(jwtProvider, never()).generateToken(any());
    }

    @SneakyThrows
    @Test
    void testAuthenticateClientOkStatus() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("client");
        authRequestDTO.setPassword("client");
        String authRequestJson = mapper.writeValueAsString(authRequestDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/authentication/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson))
                .andDo(print())
                .andExpect(status().isOk());
        verify(clientService, times(1)).getClientByUsernameAndPassword(any(), any());
        verify(jwtProvider, times(1)).generateToken(any());
    }

    @SneakyThrows
    @Test
    void testAuthenticateCourierWhenJsonIsIncorrectBadRequestStatus() {
        String incorrectJson = "incorrectJson";
        mockMvc.perform(MockMvcRequestBuilders
                .get("/authentication/couriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(incorrectJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(courierService, never()).getCourierByPhoneAndPassword(any(), any());
        verify(jwtProvider, never()).generateToken(any());
    }

    @SneakyThrows
    @Test
    void testAuthenticateNonExistentCourierNonFoundStatus() {
        CourierAuthRequestDTO authRequestDTO = new CourierAuthRequestDTO();
        authRequestDTO.setPhone("notExistentCourier");
        authRequestDTO.setPassword("notExistentCourier");
        String authRequestJson = mapper.writeValueAsString(authRequestDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/authentication/couriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(courierService, times(1)).getCourierByPhoneAndPassword(any(), any());
        verify(jwtProvider, never()).generateToken(any());
    }

    @SneakyThrows
    @Test
    void testAuthenticateCourierOkStatus() {
        CourierAuthRequestDTO authRequestDTO = new CourierAuthRequestDTO();
        authRequestDTO.setPhone("courier");
        authRequestDTO.setPassword("courier");
        String authRequestJson = mapper.writeValueAsString(authRequestDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/authentication/couriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson))
                .andDo(print())
                .andExpect(status().isOk());
        verify(courierService, times(1)).getCourierByPhoneAndPassword(any(), any());
        verify(jwtProvider, times(1)).generateToken(any());
    }

}