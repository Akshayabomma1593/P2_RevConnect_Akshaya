package com.rev.app.repository;

import com.rev.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwnerIdAndActiveTrueOrderByCreatedAtDesc(Long ownerId);

    List<Product> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Product> findByIdInAndOwnerIdAndActiveTrue(Collection<Long> ids, Long ownerId);
}
