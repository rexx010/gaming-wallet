package com.gamingwallet.common.util;

import java.util.UUID;

public final class ReferenceGenerator {
    private ReferenceGenerator(){}
    public static String generate(){
        return UUID.randomUUID().toString();

    }
}
