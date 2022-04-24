package com.jiial.bumbleB.examples.unitTests;

import com.jiial.bumbleB.annotations.Example;
import com.jiial.bumbleB.examples.Item;
import com.jiial.bumbleB.examples.Stock;

import java.util.Map;

import static com.jiial.bumbleB.framework.Framework.*;

public class StockTest {

    private final ExampleDefinition.ExampleBuilder builder = new ExampleDefinition.ExampleBuilder();
    private final Stock stock = new Stock();
    private final Map<Item, Integer> expectedStock = Map.of(
            new Item("Foo", 10.0), 1,
            new Item("Bar", 15.0), 1
    );

    @Example
    public void addToStock() {
        builder
                .name("Adding items to stock")
                .steps(
                        given(stock::add, new Item("Foo", 10.0)),
                        and(stock::add, new Item("Bar", 15.0)),
                        then(stock::getItems)
                                .isEqualTo(expectedStock)
                )
                .build()
                .run();
    }
}
