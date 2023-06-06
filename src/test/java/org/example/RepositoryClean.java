package org.example;

import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryClean {

    @Autowired
    private DealRepository dealRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BasketItemRepository basketItemRepository;
    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private UserRepository userRepository;

    public void clean() {
        if (basketItemRepository.count() > 0) basketItemRepository.deleteAll();
        if (productRepository.count() > 0) productRepository.deleteAll();
        if (basketRepository.count() > 0) basketRepository.deleteAll();
        if (userRepository.count() > 0) userRepository.deleteAll();
    }

}
