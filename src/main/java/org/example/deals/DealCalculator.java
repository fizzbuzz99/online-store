package org.example.deals;

import org.example.model.BasketItem;
import org.example.model.Deal;
import org.example.model.FinalOffer;
import org.springframework.stereotype.Component;

@Component
public class DealCalculator {
    
    public FinalOffer getOffer(BasketItem basketItem) {
        Deal deal = basketItem.getProduct().getDeal();
        if (deal == null) {
            return buildDefaultOffer(basketItem);
        }
        switch (deal.getType()) {
            case BuyOneGet50PrcOff:
                return calculateBuyOneGet50PrcOff(deal, basketItem);
            case BuyTwoGet3RdFree:
                return calculateBuyTwoGet3RdFree(deal, basketItem);
        }
        return buildDefaultOffer(basketItem);
    }

    protected FinalOffer calculateBuyOneGet50PrcOff(Deal deal, BasketItem basketItem) {
        int qty = basketItem.getQuantity();
        double productPrice = basketItem.getProduct().getPrice();
        double ifOddPx = qty%2;
        int halfQty = qty/2;
        double price = halfQty * productPrice + halfQty * productPrice/2 + ifOddPx * productPrice;

        return new FinalOffer(deal.getDescription(), qty, price);
    }

    protected FinalOffer calculateBuyTwoGet3RdFree(Deal deal, BasketItem basketItem) {
        int freeQty = basketItem.getQuantity()/2;
        return new FinalOffer(deal.getDescription(),
                freeQty + basketItem.getQuantity(),
                basketItem.getPrice());
    }

    protected FinalOffer buildDefaultOffer(BasketItem basketItem) {
        return new FinalOffer(null,
                basketItem.getQuantity(),
                basketItem.getPrice());
    }
    
}
