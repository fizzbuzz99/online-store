package org.example.controller;

import org.example.deals.DealType;
import org.example.model.Deal;
import org.example.model.Product;
import org.example.model.StoreUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminUserIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private MockMvcWrapper mvcWrapper;

    @BeforeEach
    public void init() {
        mvcWrapper = new MockMvcWrapper(mvc);
    }

    @Test
    public void testCreateAndRetrieveNewUser() throws Exception {
        // WHen
        StoreUser user = mvcWrapper.addNewUser("Johnny123");

        //And
        MvcResult result = mvcWrapper.getUserValidateIdName(user);

        // Then
        StoreUser obtainUser = mvcWrapper.convertToUser(result);
        assertEquals(user, obtainUser);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // WHen
        StoreUser user1 = mvcWrapper.addNewUser("Johnny123");
        StoreUser user2 = mvcWrapper.addNewUser("Andy444");

        //And
        MvcResult result = mvcWrapper.getAllUsers();

        // Then
        Set<String> users = mvcWrapper.convertToListOfUsers(result)
                .stream().map(StoreUser::getName).collect(Collectors.toSet());
        assertEquals(2, users.size());
        assertTrue(users.contains(user1.getName()));
        assertTrue(users.contains(user2.getName()));
    }

    @Test
    public void testRemoveExistingUser() throws Exception {

        // When
        StoreUser user = mvcWrapper.addNewUser("Johny1233");

        // Then
        mvcWrapper.removeUserById(user.getId());

        // When
        MvcResult result = mvcWrapper.getUser(user);

        // Then
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void testAddDeal() throws Exception {

        // When
        Deal deal = mvcWrapper.addNewDeal(DealType.BuyOneGet50PrcOff);

        // And
        MvcResult result = mvcWrapper.getDeal(deal);

        // Then
        assertEquals(deal, mvcWrapper.convertToDeal(result));
    }

    @Test
    public void testAddNewProductWithoutDeal() throws Exception {;

        // Given
        Product product = Product.builder().name("ink").price(12.34).inventory(10_000).build();

        // When
        product = mvcWrapper.convertToProduct(mvcWrapper.addNewProduct(product));

        // And
        MvcResult result = mvcWrapper.getProductById(product.getId());

        // Then
        assertEquals(product, mvcWrapper.convertToProduct(result));
    }

    @Test
    public void testAddNewProductWIthDeal() throws Exception {

        // Given
        Product product = Product.builder().name("Product123").inventory(100).price(12.44).build();

        // When
        Deal deal = mvcWrapper.addNewDeal(DealType.BuyOneGet50PrcOff);
        product.setDeal(deal);
        product = mvcWrapper.convertToProduct(mvcWrapper.addNewProduct(product));

        // And
        Product p = mvcWrapper.convertToProduct(mvcWrapper.getProductById(product.getId()));

        // Then
        assertEquals(product, p);
    }

}
