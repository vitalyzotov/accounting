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
@RequestMapping("/auth/signup")
@CrossOrigin
public class SignupController {

    private final UserManagementFacade userManagementFacade;

    public SignupController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    @PostMapping
    public UserRegistrationResponse registerNewUser(@RequestBody UserRegistrationRequest request) {
        User user = userManagementFacade.registerNewUser(request.name(), request.password(),
                request.firstName(), request.lastName(), request.displayName());
        return new UserRegistrationResponse(user.name());
    }

    record UserRegistrationRequest(@Schema(description = "Name of the user") String name,
                                   @Schema(description = "Password of the user as plaintext") String password,
                                   @Schema(description = "First name") String firstName,
                                   @Schema(description = "Last name") String lastName,
                                   @Schema(description = "Display name") String displayName) {
    }

    record UserRegistrationResponse(@Schema(description = "Name of the registered user") String name) {
    }

}

