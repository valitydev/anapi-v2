package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.MobilePhone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskUtil {

    private static final String MASK_SYMBOL = "*";
    private static final int PAN_LENGTH = 16;
    private static final int PHONE_UNMASKED_LAST_DIGITS = 2;

    public static String constructCardNumber(BankCard card) {
        int maskedLength = PAN_LENGTH - card.getBin().length() - card.getLastDigits().length();
        return String.format("%s%s%s", card.getBin(), MASK_SYMBOL.repeat(maskedLength), card.getLastDigits());
    }

    public static String constructPhoneNumber(MobilePhone phone) {
        String ctn = phone.getCtn();
        int maskedLength = ctn.length() - PHONE_UNMASKED_LAST_DIGITS;
        return String.format("+%s%s%s", phone.getCc(), MASK_SYMBOL.repeat(maskedLength), ctn.substring(maskedLength));
    }

}
