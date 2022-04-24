package com.jiial.bumbleB.examples;

import com.jiial.bumbleB.annotations.Step;

import java.util.HashMap;
import java.util.Map;

public class Stock {
    private final Map<Item, Integer> items = new HashMap<>();

    @Step("item {item} is added to the stock")
    public void add(Item item) {
        if (items.containsKey(item)) {
            items.put(item, items.get(item) + 1);
        } else {
            items.put(item, 1);
        }
    }

    public void remove(Item item) {
        if (items.containsKey(item) && items.get(item) > 1) {
            items.put(item, items.get(item) - 1);
        } else if (items.containsKey(item) && items.get(item) == 1) {
            items.remove(item);
        } else {
            throw new RuntimeException("Tried to remove from item when the stock was already empty!");
        }
    }

    @Step("the stock is equal to")
    public Map<Item, Integer> getItems() {
        return items;
    }
}
