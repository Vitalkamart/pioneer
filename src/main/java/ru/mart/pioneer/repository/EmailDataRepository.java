package ru.mart.pioneer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mart.pioneer.model.EmailData;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
}
