package ru.vzotov.accounting.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.accounting.domain.model.UserRepository;
import ru.vzotov.person.domain.model.Person;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(value = "accounting-app-tx", readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.find(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        return new ru.vzotov.accounting.infrastructure.security.User(
                user.name(), user.password(),
                new SimpleGrantedAuthority(user.person().personId().authority()),
                getAuthorities(user.roles(), user.person()));

    }

    private static List<GrantedAuthority> getAuthorities(List<String> roles, Person person) {
        return Stream.concat(Stream.of(person.personId().authority()), roles.stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
