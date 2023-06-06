package org.example.service;

import org.example.deals.DealCalculator;
import org.example.exception.StoreServiceException;
import org.example.model.*;
import org.example.repository.BasketItemRepository;
import org.example.repository.BasketRepository;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StoreServiceImpl extends CommonService implements StoreService {

    private final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private BasketItemRepository basketItemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DealCalculator dealCalculator;

    @Override
    public Basket findBasketById(Long id) {
        return basketRepository.getReferenceById(id);
    }

    @Override
    public Basket findBasketByUserId(Long id) {
        if (userRepository.findById(id).isEmpty()) return null;
        return userRepository.findById(id).get().getBasket();
    }

    @Override
    public BasketItem saveBasketItem(BasketItem basketItem, Long userId) throws StoreServiceException {

        Optional<StoreUser> user = userRepository.findById(userId);
        if (user.isEmpty()) throw new StoreServiceException("User does not exist");

        StoreUser u = user.get();
        basketItem.setBasket(u.getBasket());

        return basketItemRepository.save(basketItem);
    }

    @Override
    public BasketItem findBasketItemById(Long id) {
        return basketItemRepository.getReferenceById(id);
    }

    @Override
    public void deleteBasketItem(Long id) {
        basketItemRepository.deleteById(id);
    }

    @Override
    public Receipt getReceipt(Long userId) {
        Optional<StoreUser> user = userRepository.findById(userId);
        if (user.isEmpty()) return null;

        Map<Long, FinalOffer> receiptList = new HashMap<>();

        for (BasketItem item : user.get().getBasket().getItems()) {
            receiptList.put(item.getId(), dealCalculator.getOffer(item));
        }

        double totalPrice = receiptList.values().stream()
                .reduce(0.0, (t, f) -> t + f.getPrice(), Double::sum);

        return Receipt.builder()
                .userId(userId)
                .userName(user.get().getName())
                .items(receiptList)
                .totalPrice(totalPrice)
                .build();
    }

}
