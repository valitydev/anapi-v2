package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.MobilePhone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaskUtilTest {

    @Test
    void constructCardNumber() {
        BankCard bankCard = new BankCard();
        bankCard.setBin("1234");
        bankCard.setLastDigits("5678");
        assertEquals("1234********5678", MaskUtil.constructCardNumber(bankCard));
    }

    @Test
    void constructPhoneNumber() {
        MobilePhone phone = new MobilePhone();
        phone.setCc("7");
        phone.setCtn("1234567890");
        assertEquals("+7********90", MaskUtil.constructPhoneNumber(phone));
    }
}