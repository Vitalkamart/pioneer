package ru.mart.pioneer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mart.pioneer.security.JwtTokenProvider;
import ru.mart.pioneer.security.UserDetailsImpl;
import ru.mart.pioneer.security.UserDetailsServiceImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String authenticate(String login, String password) {
        try {
            log.info("User {} is trying to authenticate", login);

            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(login);

            authenticate(password, userDetails);
            setAuthentication(userDetails);

            return jwtTokenProvider.generateToken(
                    userDetails.getUsername(),
                    userDetails.getUserId()
            );

        } catch (Exception e) {
            log.error("User {} is trying to authenticate", login);
            throw new BadCredentialsException("Invalid login or password");
        }
    }

    private void authenticate(String password, UserDetailsImpl userDetails) {
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        userDetails.clearPassword();

        log.info("User win id: '{}' and name: '{}' is authenticated",
                userDetails.getUserId(), userDetails.getUsername());
    }

    private void setAuthentication(UserDetailsImpl userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
