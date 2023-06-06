package org.example.service;

import org.example.RepositoryClean;
import org.example.deals.DealType;
import org.example.exception.StoreServiceException;
import org.example.model.BasketItem;
import org.example.model.Deal;
import org.example.model.Product;
import org.example.model.StoreUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AdminServiceTest {

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private StoreServiceImpl storeService;
    @Autowired
    private RepositoryClean repositoryClean;

    @BeforeEach
    public void init() {
        repositoryClean.clean();
    }

    @Test
    public void testProductCreateRetrieveDelete() {

        // When - CREATE
        Product p1 = adminService.saveProduct(Product.builder().name("p1").price(10.55).inventory(100).build());
        // Then
        assertEquals(1, adminService.getAllProducts().size());

        // When
        Product p2 = adminService.saveProduct(Product.builder().name("p2").price(233.99).inventory(400).build());
        // Then
        assertEquals(2, adminService.getAllProducts().size());

        // When
        Product p3 = adminService.saveProduct(Product.builder().name("p3").price(100.85).inventory(900).build());
        // Then
        assertEquals(3, adminService.getAllProducts().size());

        // When - RETRIEVE
        Optional<Product> product1 = adminService.getProductById(p1.getId());
        // Then
        assertTrue(product1.isPresent());
        assertEquals("p1", product1.get().getName());
        assertEquals(10.55, product1.get().getPrice());
        assertEquals(100, product1.get().getInventory());

        // When - RETRIEVE
        Optional<Product> product2 = adminService.getProductById(p2.getId());
        // Then
        assertTrue(product2.isPresent());
        assertEquals("p2", product2.get().getName());
        assertEquals(233.99, product2.get().getPrice());
        assertEquals(400, product2.get().getInventory());

        // When - RETRIEVE
        Optional<Product> product3 = adminService.getProductById(p3.getId());
        // Then
        assertTrue(product3.isPresent());
        assertEquals("p3", product3.get().getName());
        assertEquals(100.85, product3.get().getPrice());
        assertEquals(900, product3.get().getInventory());

        // When - DELETE
        adminService.deleteProductById(p1.getId());
        // Then
        assertEquals(2, adminService.getAllProducts().size());

        // When - DELETE
        adminService.deleteProductById(p2.getId());
        // Then
        assertEquals(1, adminService.getAllProducts().size());

        // When - DELETE
        adminService.deleteProductById(p3.getId());
        // Then
        assertEquals(0, adminService.getAllProducts().size());

    }

    @Test
    public void testDeleteProductWhenLinkedToBasket() throws StoreServiceException {

        // Given
        StoreUser user = adminService.saveUser(StoreUser.builder().name("Client123").build());
        Product product = adminService.saveProduct(Product.builder().name("p1").price(10.55).inventory(100).build());
        BasketItem basketItem = storeService.saveBasketItem(BasketItem.builder().product(product).quantity(5).build(), user.getId());

        // When
        adminService.deleteProductById(product.getId());

        // Then
        assertFalse(adminService.getProductById(product.getId()).isPresent());

        Optional<StoreUser> u = adminService.findUserById(user.getId());
        assertTrue(u.isPresent());

        assertEquals(0, u.get().getBasket().getItems().size());

    }

    @Test
    public void testUserCreateRetrieveDelete() throws StoreServiceException {

        // When - CREATE
        StoreUser u1 = adminService.saveUser(StoreUser.builder().name("client1").build());
        // Then
        assertEquals(1, adminService.findAllUsers().size());
        assertNotNull(u1.getId());
        assertNotEquals(0L, u1.getId());

        // When - CREATE
        StoreUser u2 = adminService.saveUser(StoreUser.builder().name("client2").build());
        // Then
        assertEquals(2, adminService.findAllUsers().size());
        assertNotNull(u2.getId());
        assertNotEquals(0L, u2.getId());


        // When - RETRIEVE
        Optional<StoreUser> su1 = adminService.findUserById(u1.getId());
        Optional<StoreUser> su2 = adminService.findUserById(u2.getId());

        // Then
        assertTrue(su1.isPresent());
        assertEquals(u1, su1.get());
        assertTrue(su2.isPresent());
        assertEquals(u2, su2.get());

        // When - DELETE
        adminService.deleteUserById(u1.getId());
        // Then
        assertEquals(1, adminService.findAllUsers().size());
        assertFalse(adminService.findUserById(u1.getId()).isPresent());
        assertTrue(adminService.findUserById(u2.getId()).isPresent());

    }

    @Test
    public void testApplyDealToProduct() {

        Deal deal = adminService.saveDeal(Deal.builder().name(DealType.BuyOneGet50PrcOff.name()).build());

        Product product = adminService.saveProduct(Product.builder().deal(deal).price(10.4)
                .name("Product1").inventory(100).build());

        assertEquals(deal, product.getDeal());
    }

}
