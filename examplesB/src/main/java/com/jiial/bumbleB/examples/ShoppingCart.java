package com.jiial.bumbleB.examples;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private Customer customer;
    private List<Item> items;

    public ShoppingCart() {
        items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
        } else {
            throw new RuntimeException("Item not found in the cart!");
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public double getTotal() {
        double sum = 0;
        for (Item item : items) {
            sum += item.price();
        }
        return sum;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
