package com.demo.ticketing.utils;

import java.util.UUID;

public class IdGenerator {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

}
