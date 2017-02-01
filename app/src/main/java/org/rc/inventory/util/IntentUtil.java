package org.rc.inventory.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by my on 31.01.2017.
 */

public class IntentUtil {

    private static IntentUtil instance;


    static {
        instance = new IntentUtil();
    }

    public static IntentUtil getInstance() {
        return instance;
    }

    private Map<String, Object> mIntentExtraMap = new HashMap<>();

    private IntentUtil() {

    }

    public void setExtra(String key, Object obj) {
        mIntentExtraMap.put(key, obj);
    }

    public <T> T getExtra(String key) {
        if(mIntentExtraMap.containsKey(key)) {
            return (T) mIntentExtraMap.get(key);
        }
        return null;
    }


}
