package ru.vzotov.accounting.interfaces.accounting.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.accounting.interfaces.accounting.facade.UserManagementFacade;

@RestController
@RequestMapping("/signup")
@CrossOrigin
public class SignupController {

    private final UserManagementFacade userManagementFacade;

    public SignupController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    @PostMapping
    public UserRegistrationResponse registerNewUser(@RequestBody UserRegistrationRequest request) {
        User user = userManagementFacade.registerNewUser(request.getName(), request.getPassword(),
                request.getFirstName(), request.getLastName(), request.getDisplayName());
        return new UserRegistrationResponse(user.name());
    }
}

class UserRegistrationRequest {
    @Schema(description = "Name of the user")
    private String name;

    @Schema(description = "Password of the user as plaintext")
    private String password;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Display name")
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

class UserRegistrationResponse {
    @Schema(description = "Name of the registered user")
    private String name;

    public UserRegistrationResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
