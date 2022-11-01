package com.example.onlineshopbot.entity;

import com.example.onlineshopbot.enums.RoleName;
import com.example.onlineshopbot.enums.BotState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DbUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "field_id")
    private String fieldId;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "code")
    private String code;

    @Column(name = "language")
    private String language;

    @CollectionTable(
            name = "db_users_authorities",
            joinColumns = {@JoinColumn(name = "db_user_id")}
    )
    @Column(name = "authority_id")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<RoleName> authorities;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    private BotState state;

    @Column(name = "enable")
    private Boolean enable = false;

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
        return this.enable;
    }
}
