package com.jiial.bumbleB.examples.integrationTests;

import com.jiial.bumbleB.annotations.Example;
import com.jiial.bumbleB.examples.integrationSteps.ShopSteps;
import com.jiial.bumbleB.examples.Item;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;

import static com.jiial.bumbleB.examples.extensions.ExtendedFramework.after;
import static com.jiial.bumbleB.framework.Framework.*;


public class ShoppingCartTest {

    private final ExampleDefinition.ExampleBuilder exampleBuilder = new ExampleDefinition.ExampleBuilder();
    private ShopSteps shopSteps;
    private final String FIRST_NAME = "Bob";
    private final String LAST_NAME = "Barker";
    private final Map<String, String> CONTACT_INFO = Map.of(
            "Country", "USA",
            "City", "New York",
            "Street", "Main street 13 C 9",
            "Phone", "12345678",
            "Email", "bob.barker@email.com"
    );
    private final Item POTATO = new Item("Potato", 1.0);
    private final Item CUCUMBER = new Item("Cucumber", 1.5);
    private final List<Item> ITEMS = List.of(POTATO, POTATO);
    private final List<Item> EMPTY_ITEMS = List.of();

    @BeforeEach
    public void init() {
        shopSteps = new ShopSteps();
    }

    @Example
    public void userCanAddAndRemoveItemsFromTheCart() {
        exampleBuilder
                .name("User is able to add items to cart and remove them")
                .steps(
                        given(shopSteps::addItemsToStock, ITEMS),
                        and(shopSteps::userLogsIn, FIRST_NAME, LAST_NAME),
                        and(shopSteps::setContactInfo, CONTACT_INFO),
                        when(shopSteps::addItemToCart, POTATO),
                        and(shopSteps::removeItemFromCart, POTATO),
                        and(shopSteps::addItemToCart, POTATO),
                        and(shopSteps::addItemToCart, POTATO),
                        and(shopSteps::removeItemFromCart, POTATO),
                        and(shopSteps::removeItemFromCart, POTATO),
                        then(shopSteps::assertThatCartIsEmpty)
                                .because("Every item that was added was also removed")
                )
                .build()
                .run();
    }

    @Example
    public void removingFromEmptyCartThrowsAnException() {
        exampleBuilder
                .name("User can't delete items from an empty cart")
                .steps(
                        given(shopSteps::addItemsToStock, EMPTY_ITEMS),
                        and(shopSteps::userLogsIn, FIRST_NAME, LAST_NAME),
                        and(shopSteps::setContactInfo, CONTACT_INFO),
                        then(shopSteps::removingFromCartThrowsException, CUCUMBER)
                                .because("The cart is empty"),
                        and(shopSteps::removingFromEmptyStockThrowsException, CUCUMBER)
                )
                .build()
                .run();
    }

    @Example
    public void itemPricesWorkAsExpected() {
        exampleBuilder
                .name("Item prices are counted correctly")
                .steps(
                        given(shopSteps::addItemsToStock, ITEMS),
                        and(shopSteps::userLogsIn, FIRST_NAME, LAST_NAME),
                        when(shopSteps::addItemsToStock, List.of(CUCUMBER, CUCUMBER)),
                        and(shopSteps::addItemToCart, POTATO),
                        and(shopSteps::addItemToCart, POTATO),
                        and(shopSteps::addItemToCart, CUCUMBER),
                        and(shopSteps::addItemToCart, CUCUMBER),
                        then(shopSteps::assertCartPrice, 5.0),
                        after(shopSteps::removeItemFromCart, POTATO),
                        then(shopSteps::assertCartPrice, 4.0)
                )
                .build()
                .run();
    }
}
