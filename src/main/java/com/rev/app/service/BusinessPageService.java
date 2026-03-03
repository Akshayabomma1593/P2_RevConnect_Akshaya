package com.rev.app.service;

import com.rev.app.entity.BusinessPage;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.BusinessPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BusinessPageService {

    private final BusinessPageRepository businessPageRepository;

    public BusinessPageService(BusinessPageRepository businessPageRepository) {
        this.businessPageRepository = businessPageRepository;
    }

    public BusinessPage getOrCreate(User owner) {
        return businessPageRepository.findByOwnerId(owner.getId()).orElseGet(() -> {
            BusinessPage page = new BusinessPage();
            page.setOwner(owner);
            page.setPageName(owner.getFullName() != null ? owner.getFullName() : owner.getUsername());
            return businessPageRepository.save(page);
        });
    }

    @Transactional(readOnly = true)
    public BusinessPage findByUsername(String username) {
        return businessPageRepository.findByOwnerUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Business page not found for user: " + username));
    }

    public BusinessPage save(BusinessPage page) {
        return businessPageRepository.save(page);
    }
}
