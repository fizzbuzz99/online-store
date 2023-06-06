package org.example.repository;

import org.example.model.Basket;
import org.example.model.StoreUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Test
    public void test() {
        StoreUser user = userRepository.save(StoreUser.builder().name("client1").build());

        Optional<StoreUser> optUser = userRepository.findById(user.getId());

        if (optUser.isEmpty()) fail();

        user = optUser.get();

        Basket basket = Basket.builder().storeUser(user).build();
        user.setBasket(basket);

        StoreUser u = userRepository.save(user);

        Basket userBasket = basketRepository.getReferenceById(u.getBasket().getId());

        assertEquals(u.getBasket(), userBasket);
    }
}
