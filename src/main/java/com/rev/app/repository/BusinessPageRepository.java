package com.rev.app.repository;

import com.rev.app.entity.BusinessPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessPageRepository extends JpaRepository<BusinessPage, Long> {
    Optional<BusinessPage> findByOwnerId(Long ownerId);

    Optional<BusinessPage> findByOwnerUsername(String username);
}
