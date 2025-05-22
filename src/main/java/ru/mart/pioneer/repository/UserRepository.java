package ru.mart.pioneer.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mart.pioneer.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = {"emails", "phones"})
    Optional<User> findById(@NonNull Long id);

    @Query("SELECT u FROM User u LEFT JOIN u.emails e LEFT JOIN u.phones p " +
            "WHERE e.email = :identifier OR p.phone = :identifier")
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);

    Optional<User> findByEmails_Email(String email);
}
