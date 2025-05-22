package ru.mart.pioneer.security;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mart.pioneer.model.User;

import java.util.Collection;

import static ru.mart.pioneer.util.StringConstants.USER_ROLE;

@Getter
@Accessors(chain = true)
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private String password;

    public UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.username = user.getName();
        this.password = user.getPassword();
        this.authorities = AuthorityUtils.createAuthorityList(USER_ROLE);
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void clearPassword() {
        this.password = Strings.EMPTY;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
