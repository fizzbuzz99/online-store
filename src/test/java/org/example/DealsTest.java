package org.example;

import org.example.deals.DealCalculator;
import org.example.deals.DealType;
import org.example.model.BasketItem;
import org.example.model.Deal;
import org.example.model.FinalOffer;
import org.example.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealsTest {

    private DealCalculator dealCalculator = new DealCalculator();

    @Test
    public void testDealBuyOneGet50PrcOffOddQtyPx100() {

        Deal deal = Deal.builder().type(DealType.BuyOneGet50PrcOff).build();
        Product product = Product.builder().price(100).deal(deal).build();
        BasketItem basketItem = BasketItem.builder().product(product).quantity(7).build();

        FinalOffer dealOffer = dealCalculator.getOffer(basketItem);

        assertEquals(7, dealOffer.getQuantity());

        /*
        * 50% price off is applied to half of quantity.
        * 3*100 + 3*50 + 1*100 = 550
        * */
        assertEquals(550.0, dealOffer.getPrice());

    }

    @Test
    public void testDealBuyOneGet50PrcOffOddQtyPx1() {

        Deal deal = Deal.builder().type(DealType.BuyOneGet50PrcOff).build();
        Product product = Product.builder().price(1).deal(deal).build();
        BasketItem basketItem = BasketItem.builder().product(product).quantity(7).build();

        FinalOffer dealOffer = dealCalculator.getOffer(basketItem);

        assertEquals(7, dealOffer.getQuantity());

        /*
         * 50% price off is applied to half of quantity.
         * 3*100 + 3*50 + 1*100 = 550
         * */
        assertEquals(5.5, dealOffer.getPrice());

    }

    @Test
    public void testBuyTwoGet3RdFreeDealOneItem() {

        Deal deal = Deal.builder().type(DealType.BuyTwoGet3RdFree).build();
        Product product = Product.builder().price(10).deal(deal).build();
        BasketItem basketItem = BasketItem.builder().product(product).quantity(1).build();

        FinalOffer dealOffer = dealCalculator.getOffer(basketItem);

        assertEquals(1, dealOffer.getQuantity());
        assertEquals(10, dealOffer.getPrice());
    }

    @Test
    public void testBuyTwoGet3RdFreeDealTwoItems() {

        Deal deal = Deal.builder().type(DealType.BuyTwoGet3RdFree).build();
        Product product = Product.builder().price(10).deal(deal).build();
        BasketItem basketItem = BasketItem.builder().product(product).quantity(2).build();

        FinalOffer dealOffer = dealCalculator.getOffer(basketItem);

        assertEquals(3, dealOffer.getQuantity());
        assertEquals(20, dealOffer.getPrice());
    }

    @Test
    public void testBuyTwoGet3RdFreeDeal100Items() {

        Deal deal = Deal.builder().type(DealType.BuyTwoGet3RdFree).build();
        Product product = Product.builder().price(10).deal(deal).build();
        BasketItem basketItem = BasketItem.builder().product(product).quantity(100).build();

        FinalOffer dealOffer = dealCalculator.getOffer(basketItem);

        assertEquals(150, dealOffer.getQuantity());
        assertEquals(1000, dealOffer.getPrice());
    }

}
