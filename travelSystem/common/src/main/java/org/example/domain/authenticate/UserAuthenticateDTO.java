package org.example.domain.authenticate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticateDTO {
    private Long id;

    private String username;

    private String name;

    private List<String> perms;
}
