package org.example.service;

import org.example.exception.StoreServiceException;
import org.example.model.Basket;
import org.example.model.BasketItem;
import org.example.model.Receipt;

public interface StoreService {

    /* Basket */
    Basket findBasketById(Long id);
    Basket findBasketByUserId(Long id) throws StoreServiceException;

    /* BasketItem */
    BasketItem saveBasketItem(BasketItem basketItem, Long userId) throws StoreServiceException;
    BasketItem findBasketItemById(Long id);
    void deleteBasketItem(Long id);

    /* Order */
    Receipt getReceipt(Long userId);

}
