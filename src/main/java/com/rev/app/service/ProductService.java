package com.rev.app.service;

import com.rev.app.entity.Product;
import com.rev.app.entity.User;
import com.rev.app.exception.AccessDeniedException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private static final int MAX_LINK_LENGTH = 255;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getActiveProductsForOwner(Long ownerId) {
        return productRepository.findByOwnerIdAndActiveTrueOrderByCreatedAtDesc(ownerId);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProductsForOwner(Long ownerId) {
        return productRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    public Product create(User owner, String name, String description, BigDecimal price, String link) {
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }

        String normalizedLink = link == null ? null : link.trim();
        if (normalizedLink != null && normalizedLink.length() > MAX_LINK_LENGTH) {
            throw new IllegalArgumentException("Link is too long. Please keep it under 255 characters.");
        }

        Product p = new Product();
        p.setOwner(owner);
        p.setName(normalizedName);
        p.setDescription(description);
        p.setPrice(price);
        p.setLink(normalizedLink == null || normalizedLink.isEmpty() ? null : normalizedLink);
        p.setActive(true);
        return productRepository.save(p);
    }

    public void deactivate(Long productId, User currentUser) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (!p.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not authorized to update this product.");
        }
        p.setActive(false);
        productRepository.save(p);
    }
}
