package dev.vality.anapi.v2.testutil;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@UtilityClass
public class RandomUtil {

    private static final Random random = new Random();

    public static int randomInt(int from, int to) {
        return random.nextInt(to - from) + from;
    }

    public static String randomIntegerAsString(int from, int to) {
        return String.valueOf(random.nextInt(to - from) + from);
    }

    public static String randomString(int length) {
        return new String(randomBytes(length), StandardCharsets.UTF_8);
    }

    public static byte[] randomBytes(int length) {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return array;
    }

}
