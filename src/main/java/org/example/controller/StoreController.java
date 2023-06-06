package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.StoreServiceException;
import org.example.model.*;
import org.example.service.AdminServiceImpl;
import org.example.service.StoreServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class StoreController {

    private final Logger log = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private AdminServiceImpl adminService;
    @Autowired
    private StoreServiceImpl storeService;

    ObjectMapper jsonObjectMapper = new ObjectMapper();

    @RequestMapping(value = "/admin/user/add", method = RequestMethod.POST)
    public ResponseEntity<StoreUser> addUser(@RequestBody StoreUser storeUser) {
        StoreUser u = adminService.saveUser(storeUser);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<StoreUser> getUser(@PathVariable("userId") Long userId) {
        Optional<StoreUser> user = adminService.findUserById(userId);
        return user.map(storeUser -> new ResponseEntity<>(storeUser, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.OK));
    }

    @RequestMapping(value = "/admin/user/all", method = RequestMethod.GET)
    public ResponseEntity<List<StoreUser>> getAllUsers() {
        return new ResponseEntity<>(adminService.findAllUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/user/remove/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<StoreUser> removeUser(@PathVariable("userId") Long userId) {
        log.info("Removing user " + userId);
        Optional<StoreUser> user = adminService.findUserById(userId);
        if (user.isEmpty()) return new ResponseEntity<>(null, HttpStatus.OK);
        log.info("... removing now for real");
        adminService.deleteUserById(userId);
        log.info("DONE");
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/product/add", method = RequestMethod.POST)
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product p = adminService.saveProduct(product);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/product/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Product> getProduct(@PathVariable("productId") Long productId) {
        Optional<Product> product = adminService.getProductById(productId);
        return product.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.OK));
    }

    @RequestMapping(value = "/admin/product/remove/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<Product> removeProductById(@PathVariable("productId") Long productId) {
        adminService.deleteProductById(productId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/product/deal", method = RequestMethod.POST)
    public ResponseEntity<Deal> addDeal(@RequestBody Deal deal) {
        return new ResponseEntity<>(adminService.saveDeal(deal), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/product/deal/{dealId}", method = RequestMethod.GET)
    public ResponseEntity<Deal> getDeal(@PathVariable("dealId") Long dealId) {
        Optional<Deal> deal = adminService.getDealById(dealId);
        return deal.map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.OK));
    }

    @RequestMapping(value = "/admin/product/deal/remove/{dealId}", method = RequestMethod.DELETE)
    public ResponseEntity<Deal> removeDealById(@PathVariable("dealId") Long dealId) {
        adminService.deleteDealById(dealId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/store/user/{userId}/item", method = RequestMethod.POST)
    public ResponseEntity<BasketItem> addBasketItem(@RequestBody BasketItem basketItem,
                                                    @PathVariable("userId") Long userId) {
        try {
            BasketItem item = storeService.saveBasketItem(basketItem, userId);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (StoreServiceException e) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/store/user/item/{itemId}/remove", method = RequestMethod.DELETE)
    public ResponseEntity<BasketItem> removeItemById(@PathVariable("itemId") Long itemId) {
        storeService.deleteBasketItem(itemId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/store/user/{userId}/receipt", method = RequestMethod.GET)
    public ResponseEntity<Receipt> getReceipt(@PathVariable("userId") Long userId) {
        if (storeService.findUserById(userId).isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(storeService.getReceipt(userId), HttpStatus.OK);
    }

}
