package com.auth.shopping_list;

import org.springframework.boot.SpringApplication;

public class TestShoppingListApplication {

    public static void main(String[] args) {
        SpringApplication.from(ShoppingListApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
