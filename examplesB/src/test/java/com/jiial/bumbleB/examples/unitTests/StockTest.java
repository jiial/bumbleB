package com.jiial.bumbleB.examples.unitTests;

import com.jiial.bumbleB.annotations.Example;
import com.jiial.bumbleB.examples.Item;
import com.jiial.bumbleB.examples.Stock;
import com.jiial.bumbleB.assertions.Assertions;

import java.util.Map;

import static com.jiial.bumbleB.framework.Framework.*;

public class StockTest {

    private final ExampleDefinition.ExampleBuilder builder = new ExampleDefinition.ExampleBuilder();
    private final Stock stock = new Stock();
    private final Map<Item, Integer> expectedStock = Map.of(
            new Item("Foo", 10.0), 1,
            new Item("Bar", 15.0), 1
    );
    private final Step<?, ?> addItem = given(stock::add, null).describeAs("item {item} is added to the stock");

    @Example
    public void addToStock() {
        builder
                .name("Adding items to stock")
                .steps(
                        addItem.withArgs(new Item("Foo", 10.0)),
                        addItem.withArgs(new Item("Bar", 15.0)),
                        then(stock::isEmpty).satisfies(Assertions::assertFalse)
                                .describeAs("the stock is not empty"),
                        and(stock::getAmount, "Foo").satisfies(Assertions::assertEquals, 1)
                                .describeAs("there is [amount] {item} in the stock"),
                        and(stock::getItems).satisfies(Assertions::assertEquals, expectedStock)
                                .describeAs("the stock is equal to [stock]")
                )
                .build()
                .run();
    }
}
