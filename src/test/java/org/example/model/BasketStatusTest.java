package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class BasketStatusTest {

    @Test
    public void test() {

        List<Item> items = Arrays.asList(Item.build(12, 1), Item.build(5, 5));

        double total = items.stream().reduce(0.0, (px, item) -> px + item.qty, Double::sum);

    }

    static class Item {
        double price;
        int qty;

        public Item(double price, int qty) {
            this.price = price;
            this.qty = qty;
        }

        static Item build(double px, int qty) {
            return new Item(px, qty);
        }
    }

}
