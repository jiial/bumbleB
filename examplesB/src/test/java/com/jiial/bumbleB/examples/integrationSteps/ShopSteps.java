package com.jiial.bumbleB.examples.integrationSteps;

import com.jiial.bumbleB.annotations.Step;
import com.jiial.bumbleB.examples.Customer;
import com.jiial.bumbleB.examples.Item;
import com.jiial.bumbleB.examples.ShoppingCart;
import com.jiial.bumbleB.examples.Stock;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShopSteps {

    private Stock stock;
    private ShoppingCart shoppingCart;
    private Customer customer;

    @Step("the items {items} have been added to stock")
    public void addItemsToStock(List<Item> items) {
        if (stock == null) {
            stock = new Stock();
        }
        items.forEach(stock::add);
    }

    @Step("customer {firstName} {lastName} is logged in")
    public void userLogsIn(String firstName, String lastName) {
        customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        shoppingCart = new ShoppingCart();
        shoppingCart.setCustomer(customer);
    }

    @Step("the customer's contact information is set to {contactInfo}")
    public void setContactInfo(Map<String, String> contactInfo) {
       contactInfo.keySet().forEach(key -> {
           switch (key) {
               case "Country" -> customer.setCountry(contactInfo.get(key));
               case "City" -> customer.setCity(contactInfo.get(key));
               case "Street" -> customer.setStreet(contactInfo.get(key));
               case "Phone" -> customer.setPhone(contactInfo.get(key));
               case "Email" -> customer.setEmail(contactInfo.get(key));
           }
       });
    }

    @Step("the customer adds item {item} to the cart")
    public void addItemToCart(Item item) {
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
        }
        stock.remove(item);
        shoppingCart.addItem(item);
    }

    @Step("the customer removes item {item} from the cart")
    public void removeItemFromCart(Item item) {
        stock.add(item);
        shoppingCart.removeItem(item);
    }

    @Step("the cart is empty")
    public void assertThatCartIsEmpty() {
        assertThat(shoppingCart.getItems().isEmpty()).isTrue();
    }

    @Step("removing item {item} from cart throws an exception")
    public void removingFromCartThrowsException(Item item) {
        assertThatThrownBy(() -> shoppingCart.removeItem(item)).isInstanceOf(RuntimeException.class)
                .hasMessage("Item not found in the cart!");
    }

    @Step("removing item {item} from stock throws an exception")
    public void removingFromEmptyStockThrowsException(Item item) {
        assertThatThrownBy(() -> stock.remove(item)).isInstanceOf(RuntimeException.class)
                .hasMessage("Tried to remove from item when the stock was already empty!");
    }

    @Step("the shopping cart price is equal to {expected}")
    public void assertCartPrice(Double expected) {
        assertThat(shoppingCart.getTotal()).isEqualTo(expected);
    }
}
