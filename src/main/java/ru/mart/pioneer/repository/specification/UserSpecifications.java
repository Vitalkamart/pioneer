package ru.mart.pioneer.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.mart.pioneer.model.EmailData;
import ru.mart.pioneer.model.PhoneData;
import ru.mart.pioneer.model.User;

import java.time.LocalDate;

public class UserSpecifications {

    public static Specification<User> withNameLike(String name) {
        return (root, query, cb) ->
                name == null || name.trim().isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> withDateAfter(LocalDate date) {
        return (root, query, cb) ->
                date == null
                        ? cb.conjunction()
                        : cb.greaterThan(root.get("dateOfBirth"), date);
    }

    public static Specification<User> withExactPhone(String phone) {
        return (root, query, cb) -> {
            if (phone == null || phone.isEmpty()) {
                return cb.conjunction();
            }
            Join<User, PhoneData> phones = root.join("phones", JoinType.INNER);
            query.distinct(true);
            return cb.equal(phones.get("phone"), phone);
        };
    }

    public static Specification<User> withExactEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) {
                return cb.conjunction();
            }
            Join<User, EmailData> emails = root.join("emails", JoinType.INNER);
            query.distinct(true);
            return cb.equal(emails.get("email"), email);
        };
    }
}
