```mermaid
classDiagram
    direction LR
    class User
    class UserManagementFacade {
        registerNewUser(name, password, firstName, lastName, displayName) User
    }
    class UserManagementFacadeImpl
    UserManagementFacade --|> UserManagementFacadeImpl
    User -- UserManagementFacade
```
