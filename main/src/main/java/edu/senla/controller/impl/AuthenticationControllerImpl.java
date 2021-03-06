package edu.senla.controller.impl;

import edu.senla.controller.AuthenticationController;
import edu.senla.model.dto.AuthResponseDTO;
import edu.senla.model.dto.UserFullInfoDTO;
import edu.senla.model.dto.CourierFullInfoDTO;
import edu.senla.security.JwtProvider;
import edu.senla.service.ClientService;
import edu.senla.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final ClientService clientService;
    private final CourierService courierService;
    private final JwtProvider jwtProvider;

    @GetMapping(value = "/clients")
    public AuthResponseDTO authenticateClient(@RequestBody String authRequestJson) {
        UserFullInfoDTO clientDTO = clientService.getClientByUsernameAndPassword(authRequestJson);
        String token = jwtProvider.generateToken(clientDTO.getUsername());
        return new AuthResponseDTO(token);
    }

    @GetMapping(value = "/couriers")
    public AuthResponseDTO authenticateCourier(@RequestBody String authRequestCourierJson) {
        CourierFullInfoDTO courierDTO = courierService.getCourierByPhoneAndPassword(authRequestCourierJson);
        String token = jwtProvider.generateToken(courierDTO.getPhone());
        return new AuthResponseDTO(token);
    }
}



