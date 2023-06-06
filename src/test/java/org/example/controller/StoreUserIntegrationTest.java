package org.example.controller;

import org.example.deals.DealType;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreUserIntegrationTest {

    @Autowired
    public MockMvc mvc;

    private MockMvcWrapper mvcWrapper;

    @BeforeEach
    public void init() {
        mvcWrapper = new MockMvcWrapper(mvc);
    }

    @Test
    public void testReceiptNoSpecialDeals() throws Exception {
        // Given
        StoreUser user = mvcWrapper.addNewUser("Johny123");
        Product product1 = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product1").price(12.35).inventory(100).build()));
        Product product2 = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product2").price(100.50).inventory(100).build()));

        // When
        BasketItem basketItem = BasketItem.builder().product(product1).quantity(4).build();
        // And
        MvcResult result = mvcWrapper.addItemToBasket(basketItem, user.getId());

        // Then
        basketItem = mvcWrapper.convertToBasketItem(result);
        assertNotNull(basketItem.getId());

        // When
        result = mvcWrapper.getReceipt(user.getId());
        // Then
        Receipt receipt = mvcWrapper.convertToReceipt(result);
        assertEquals(user.getId(), receipt.getUserId());
        assertEquals(1, receipt.getItems().size());
        FinalOffer offer = receipt.getItems().get(basketItem.getId());
        assertEquals(49.4, offer.getPrice());
        assertEquals(4, offer.getQuantity());
        assertNull(offer.getDealApplied());
    }

    @Test
    public void testReceiptThatDealIsAppliedToProduct() throws Exception {

        // Given
        StoreUser user = mvcWrapper.addNewUser("Johny444");
        Deal deal = mvcWrapper.addNewDeal(DealType.BuyOneGet50PrcOff);
        Product product1 = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product1").price(12.35)
                        .inventory(100).deal(deal).build()));
        Product product2 = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product2").price(100.50)
                        .inventory(100).deal(deal).build()));

        // When
        BasketItem basketItem1 = BasketItem.builder().product(product1).quantity(1).build();
        BasketItem basketItem2 = BasketItem.builder().product(product2).quantity(3).build();
        // And
        basketItem1 = mvcWrapper.convertToBasketItem(mvcWrapper.addItemToBasket(basketItem1, user.getId()));
        basketItem2 = mvcWrapper.convertToBasketItem(mvcWrapper.addItemToBasket(basketItem2, user.getId()));

        // Then
        Receipt receipt = mvcWrapper.convertToReceipt(mvcWrapper.getReceipt(user.getId()));
        assertEquals(user.getId(), receipt.getUserId());
        assertEquals(2, receipt.getItems().size());

        FinalOffer offer1 = receipt.getItems().get(basketItem1.getId());
        assertEquals(1, offer1.getQuantity());
        assertEquals(12.35, offer1.getPrice());
        assertEquals(DealType.BuyOneGet50PrcOff.name(), offer1.getDealApplied());

        FinalOffer offer2 = receipt.getItems().get(basketItem2.getId());
        assertEquals(3, offer2.getQuantity());
        /*
        * Here the price is 100.50 * 2 + 100.50/2 = 201 + 50.25 = 251.25
        * */
        assertEquals(251.25, offer2.getPrice());
        assertEquals(DealType.BuyOneGet50PrcOff.name(), offer2.getDealApplied());

        // Then - totalPx = 12.35 + 251.25 = 263.6
        assertEquals(263.6, receipt.getTotalPrice());
    }

    @Test
    public void testReceiptThatDifferentDealAreAppliedToProducts() throws Exception {

        // Given
        StoreUser user = mvcWrapper.addNewUser("Johny444");
        Deal deal1 = mvcWrapper.addNewDeal(DealType.BuyOneGet50PrcOff);
        Deal deal2 = mvcWrapper.addNewDeal(DealType.BuyTwoGet3RdFree);
        Product product1 = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product1").price(12.35)
                        .inventory(100).deal(deal1).build()));
        Product product2 = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product2").price(100.50)
                        .inventory(100).deal(deal2).build()));

        // When
        BasketItem basketItem1 = BasketItem.builder().product(product1).quantity(4).build();
        BasketItem basketItem2 = BasketItem.builder().product(product2).quantity(3).build();
        // And
        basketItem1 = mvcWrapper.convertToBasketItem(mvcWrapper.addItemToBasket(basketItem1, user.getId()));
        basketItem2 = mvcWrapper.convertToBasketItem(mvcWrapper.addItemToBasket(basketItem2, user.getId()));

        // Then
        Receipt receipt = mvcWrapper.convertToReceipt(mvcWrapper.getReceipt(user.getId()));
        assertEquals(user.getId(), receipt.getUserId());
        assertEquals(2, receipt.getItems().size());

        FinalOffer offer1 = receipt.getItems().get(basketItem1.getId());
        assertEquals(4, offer1.getQuantity());
        // Price is 2*12.35 + 2*(12.35/2) = 24.7 + 12.35 = 37.05
        assertEquals(37.05, offer1.getPrice());
        assertEquals(DealType.BuyOneGet50PrcOff.name(), offer1.getDealApplied());

        FinalOffer offer2 = receipt.getItems().get(basketItem2.getId());
        assertEquals(4, offer2.getQuantity());
        /*
         * Here the price is 100.50 * 3 = 301.5
         * */
        assertEquals(301.5, offer2.getPrice());

        /*
        * but quantity is 4 since buying 2 user gets 1 free
        * */
        assertEquals(4, offer2.getQuantity());
        assertEquals(DealType.BuyTwoGet3RdFree.name(), offer2.getDealApplied());

    }

    /**
     * Failing test since missing requirement regarding inventory constraints
     * */
    @Test
    public void testUserCanNotBuyQuantityThatExceedsInventory() throws Exception {

        // Given
        StoreUser user = mvcWrapper.addNewUser("Johny444");
        Product product = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product1").price(12.35)
                        .inventory(5).build()));

        // When
        BasketItem item = BasketItem.builder().product(product)
                .quantity(product.getInventory() + 1).build();
        // And
        MvcResult result = mvcWrapper.addItemToBasket(item, user.getId());

        // Then
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    /**
     * Failing test since missing requirement regarding how to deal when admin removed deal
     * */
    @Test
    public void testRemovedDealShouldNotBeAppliedToReceipt() throws Exception {
        // Given
        StoreUser user = mvcWrapper.addNewUser("Johny444");
        Deal deal = mvcWrapper.addNewDeal(DealType.BuyOneGet50PrcOff);
        Product product = mvcWrapper.convertToProduct(mvcWrapper
                .addNewProduct(Product.builder().name("Product1").price(12.35)
                        .inventory(100).deal(deal).build()));

        // When
        BasketItem basketItem = BasketItem.builder().product(product).quantity(4).build();
        // And
        basketItem = mvcWrapper.convertToBasketItem(mvcWrapper.addItemToBasket(basketItem, user.getId()));

        // Then
        Receipt receipt = mvcWrapper.convertToReceipt(mvcWrapper.getReceipt(user.getId()));
        FinalOffer offer = receipt.getItems().get(basketItem.getId());
        assertEquals(4, offer.getQuantity());
        // Price is 2*12.35 + 2*(12.35/2) = 24.7 + 12.35 = 37.05
        assertEquals(37.05, offer.getPrice());
        assertEquals(DealType.BuyOneGet50PrcOff.name(), offer.getDealApplied());

        // When
        mvcWrapper.removeDalById(deal.getId());

        // Then
        receipt = mvcWrapper.convertToReceipt(mvcWrapper.getReceipt(user.getId()));
        offer = receipt.getItems().get(basketItem.getId());
        // Price should be 4*12.35
        assertEquals(49.4, offer.getPrice());
    }

}
