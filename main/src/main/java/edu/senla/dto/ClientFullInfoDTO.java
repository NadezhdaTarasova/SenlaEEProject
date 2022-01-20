package edu.senla.dto;

import edu.senla.entity.Order;
import edu.senla.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientFullInfoDTO {

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String address;

    private String username;

    private String password;

    private List<Order> orders;

    private Role role;

}