package ru.mart.pioneer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mart.pioneer.model.PhoneData;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
}
