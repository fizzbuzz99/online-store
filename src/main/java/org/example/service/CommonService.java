package org.example.service;

import org.example.model.Product;
import org.example.model.StoreUser;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommonService {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ProductRepository productRepository;

    public List<StoreUser> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<StoreUser> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

}
