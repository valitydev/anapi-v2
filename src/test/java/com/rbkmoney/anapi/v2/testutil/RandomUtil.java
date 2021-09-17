package com.rbkmoney.anapi.v2.testutil;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtil {

    private static final Random random = new Random();

    public static String randomInteger(int from, int to) {
        return String.valueOf(random.nextInt(to - from) + from);
    }

}
