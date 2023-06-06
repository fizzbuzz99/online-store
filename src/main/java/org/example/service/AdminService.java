package org.example.service;

import org.example.model.Deal;
import org.example.model.Product;
import org.example.model.StoreUser;

import java.util.Optional;

public interface AdminService {

    /* Product */
    Product saveProduct(Product product);
    void deleteProductById(Long id);

    /* Deal */
    Deal saveDeal(Deal deal);
    Optional<Deal> getDealById(Long id);
    void deleteDealById(Long id);

    /* User */
    StoreUser saveUser(StoreUser user);
    void deleteUserById(Long id);

}
