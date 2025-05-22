package ru.mart.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class User extends BaseEntity {

    @Column(name = "name", length = 500, nullable = false)
    @Size(max = 500, message = "Name must be less than 500 characters")
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "password", length = 500, nullable = false)
    @Size(min = 8, max = 500, message = "Password must be between 8 and 500 characters")
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhoneData> phones = new HashSet<>();

    public void updateEmails(@NonNull Set<String> emailStrings) {
        removeDeletedEmails(emailStrings);
        addNewEmails(emailStrings);
    }

    public void updatePhones(@NonNull Set<String> phoneStrings) {
        removeDeletedPhones(phoneStrings);
        addNewPhones(phoneStrings);
    }

    public void addEmail(String email) {
        if (this.emails.stream().anyMatch(e -> e.getEmail().equals(email))) {
            return;
        }
        EmailData newEmail = new EmailData()
                .setEmail(email)
                .setUser(this);
        this.emails.add(newEmail);
    }

    public void removeEmail(EmailData email) {
        boolean removed = this.emails.remove(email);
        email.setUser(null);
    }

    public void addPhone(String phone) {
        if (this.phones.stream().anyMatch(p -> p.getPhone().equals(phone))) {
            return;
        }
        PhoneData newPhone = new PhoneData()
                .setPhone(phone)
                .setUser(this);
        this.phones.add(newPhone);
    }

    public void removePhone(PhoneData phone) {
        this.phones.remove(phone);
        phone.setUser(null);
    }

    private void removeDeletedEmails(Set<String> emailStrings) {
        Set<EmailData> toRemove = this.emails.stream()
                .filter(email -> !emailStrings.contains(email.getEmail()))
                .collect(Collectors.toSet());

        toRemove.forEach(this::removeEmail);
    }

    private void addNewEmails(Set<String> emailStrings) {
        emailStrings.stream()
                .filter(email -> this.emails.stream()
                        .noneMatch(e -> e.getEmail().equals(email)))
                .forEach(this::addEmail);
    }

    private void removeDeletedPhones(Set<String> phoneStrings) {
        Set<PhoneData> toRemove = this.phones.stream()
                .filter(phone -> !phoneStrings.contains(phone.getPhone()))
                .collect(Collectors.toSet());

        toRemove.forEach(this::removePhone);
    }

    private void addNewPhones(Set<String> phoneStrings) {
        phoneStrings.stream()
                .filter(phone -> this.phones.stream()
                        .noneMatch(p -> p.getPhone().equals(phone)))
                .forEach(this::addPhone);
    }
}
