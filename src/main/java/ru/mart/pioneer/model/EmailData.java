package ru.mart.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "emails")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class EmailData extends BaseEntity {

    @Column(name = "email", length = 200, nullable = false, unique = true)
    @Size(max = 200, message = "Email must be less than 200 characters")
    @Email(message = "Email should be valid")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    private void validate() {
        if (this.user == null) {
            throw new IllegalStateException("Email must be associated with a user");
        }
    }
}
