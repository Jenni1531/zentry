package zentry.back.api.core.models;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", length = 50)
    private String username;

    @Email
    @Column(name = "email", length = 100)
    private String email;

    
    @Column(name = "password", length = 255)
    private String password;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Sin roles por ahora
    }

    @Override
    public String getUsername() {
        return this.email; 
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
