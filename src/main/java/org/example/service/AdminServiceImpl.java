package org.example.service;

import com.sun.istack.NotNull;
import org.example.model.*;
import org.example.repository.BasketItemRepository;
import org.example.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl extends CommonService implements AdminService {

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;


    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {

        Optional<Product> product = getProductById(id);
        if (product.isEmpty()) return;

        Product p = product.get();
        for (BasketItem item : p.getItems()) {
            basketItemRepository.deleteById(item.getId());
        }

        productRepository.deleteById(id);
    }

    @Override
    public Deal saveDeal(Deal deal) {
        return dealRepository.save(deal);
    }

    @Override
    public Optional<Deal> getDealById(Long id) {
        return dealRepository.findById(id);
    }

    @Override
    public void deleteDealById(Long id) {
        dealRepository.deleteById(id);
    }

    @Override
    public StoreUser saveUser(StoreUser user) {
        Basket basket = Basket.builder().storeUser(user).build();
        user.setBasket(basket);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(@NotNull Long id) {
        userRepository.deleteById(id);
    }

    public void clean() {
        if (userRepository.count() > 0) userRepository.deleteAll();
        if (productRepository.count() > 0) productRepository.deleteAll();
    }
}
