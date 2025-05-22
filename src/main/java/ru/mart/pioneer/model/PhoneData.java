package ru.mart.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.mart.pioneer.util.StringConstants;

@Entity
@Table(name = "phones")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class PhoneData extends BaseEntity {

    @Column(name = "phone", length = 13, unique = true, nullable = false)
    @Pattern(regexp = StringConstants.PHONE_REGEXP, message = "Phone must start with 7 and contain 11 digits")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    private void validate() {
        if (this.user == null) {
            throw new IllegalStateException("Phone must be associated with a user");
        }
    }
}
