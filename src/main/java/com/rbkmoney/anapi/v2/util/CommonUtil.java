package com.rbkmoney.anapi.v2.util;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommonUtil {

    public static List<String> merge(@Nullable String id, @Nullable List<String> ids) {
        if (id != null) {
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(id);
        }
        return ids;
    }

}
