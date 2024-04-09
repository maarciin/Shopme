package com.shopme.setting;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingBag;

import java.util.List;

public class CurrencySettingBag extends SettingBag {

    public CurrencySettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getSymbol() {
        return super.getValue("CURRENCY_SYMBOL").orElseThrow();
    }

    public String getSymbolPosition() {
        return super.getValue("CURRENCY_SYMBOL_POSITION").orElseThrow();
    }

    public String getDecimalPointType() {
        return super.getValue("DECIMAL_POINT_TYPE").orElseThrow();
    }

    public String getThousandPointType() {
        return super.getValue("THOUSANDS_POINT_TYPE").orElseThrow();
    }

    public int getDecimalDigits() {
        return Integer.parseInt(super.getValue("DECIMAL_DIGITS").orElseThrow());
    }
}
