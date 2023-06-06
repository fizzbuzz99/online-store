package org.example.service;

import org.example.RepositoryClean;
import org.example.deals.DealType;
import org.example.exception.StoreServiceException;
import org.example.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StoreServiceTest {

    @Autowired
    private StoreServiceImpl storeService;

    @Autowired
    protected AdminServiceImpl adminService;
    @Autowired
    private RepositoryClean repositoryClean;

    @AfterEach
    public void init() {
        repositoryClean.clean();
    }

    @Test
    public void testServiceInstance() {
        assertNotNull(storeService);
    }


    @Test
    public void testBasketItemFailedRelationAndNulls() throws StoreServiceException {

        // Given
        StoreUser user = adminService.saveUser(StoreUser.builder().name("Client123").build());

        try {
            // When
            storeService.saveBasketItem(BasketItem.builder().product(Product.builder().id(1000L).build()).build(), user.getId());
            // Then
            fail("Should fail due to not existing product id");
        } catch (DataIntegrityViolationException e) {
            // ok
        }

        try {
            // When
            storeService.saveBasketItem(BasketItem.builder().build(), user.getId());
            // Then
            fail("Should fail due to null product");
        } catch (DataIntegrityViolationException e) {
            // ok
        }

    }

    @Test
    public void testBasketItemCreateRetrieveDelete() throws StoreServiceException {

        // Given
        StoreUser user = adminService.saveUser(StoreUser.builder().name("client1").build());
        Product product1 = adminService.saveProduct(Product.builder().name("milk").price(12).inventory(100).build());
        Product product2 = adminService.saveProduct(Product.builder().name("coffee").price(50).inventory(100).build());

        // Then - user does not have a basket till user adds first item to be purchesed
        assertNotNull(user.getBasket());

        // When - CREATE
        BasketItem item1 = storeService.saveBasketItem(BasketItem.builder().product(product1).quantity(3).build(), user.getId());

        //Then
        Basket basket = storeService.findBasketByUserId(user.getId());
        assertNotNull(basket);
        assertEquals(36.0, calcTotalPrice(basket));
        assertEquals(36.0, calcTotalPrice(storeService.findBasketById(basket.getId())));

        // When - CREATE
        BasketItem item2 = storeService.saveBasketItem(BasketItem.builder().product(product2).quantity(2).build(), user.getId());

        // Then
        assertEquals(2, storeService.findBasketById(basket.getId()).getItems().size());
        assertEquals(136.0, calcTotalPrice(storeService.findBasketById(basket.getId())));

        // When - DELETE
        storeService.deleteBasketItem(item2.getId());
        assertEquals(1, storeService.findBasketById(basket.getId()).getItems().size());
        assertEquals(36.0, calcTotalPrice(storeService.findBasketById(basket.getId())));
    }

    @Test
    public void testGetReceiptDefault() throws StoreServiceException {
        testDeal(null, 12, 2, 2, 24.0);
    }

    @Test
    public void testGetReceipt50PrcOff() throws StoreServiceException {
        testDeal(DealType.BuyOneGet50PrcOff, 12, 2, 2, 18.0);
    }

    @Test
    public void testGetReceipt50PrcOffBuy3Items() throws StoreServiceException {
        testDeal(DealType.BuyOneGet50PrcOff, 12, 3, 3, 30.0);
    }


    public void testDeal(DealType dealType, double productPx, int purchasedQty,
                         Integer expectedQty, Double expectedPx) throws StoreServiceException {
        // Given
        StoreUser user = adminService.saveUser(StoreUser.builder().name("client1").build());
        Product product = Product.builder().name("milk").price(productPx)
                .inventory(100).build();
        if (dealType != null) {
            product.setDeal(adminService.saveDeal(Deal.builder().type(dealType).build()));
        }
        adminService.saveProduct(product);

        // When
        BasketItem basketItem = BasketItem.builder().product(product).quantity(purchasedQty).build();
        storeService.saveBasketItem(basketItem, user.getId());

        // And
        Receipt receipt = storeService.getReceipt(user.getId());

        // Then
        assertNotNull(receipt);
        assertEquals(user.getId(), receipt.getUserId());
        assertEquals(user.getName(), receipt.getUserName());
        assertEquals(1, receipt.getItems().size());

        Optional<BasketItem> userItem = storeService.findBasketByUserId(user.getId()).getItems().stream().findFirst();
        assertTrue(userItem.isPresent());

        Optional<Map.Entry<Long, FinalOffer>> entry = receipt.getItems().entrySet().stream().findFirst();
        assertTrue(entry.isPresent());

        assertEquals(expectedQty, entry.get().getValue().getQuantity());
        assertEquals(expectedPx, entry.get().getValue().getPrice());
    }

    private double calcTotalPrice(Basket basket) {
        return basket.getItems().stream().reduce(0.0, (total, item) -> total + item.getPrice(), Double::sum);
    }

}
