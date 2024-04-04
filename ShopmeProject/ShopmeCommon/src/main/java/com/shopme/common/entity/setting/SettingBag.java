package com.shopme.common.entity.setting;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SettingBag {

    private List<Setting> listSettings;

    public Optional<Setting> get(String key) {
        int index = listSettings.indexOf(new Setting(key));
        return index >= 0 ? Optional.of(listSettings.get(index)) : Optional.empty();
    }

    public Optional<String> getValue(String key) {
        return get(key).map(Setting::getValue);
    }

    public void update(String key, String value) {
        if (value != null) {
            get(key).ifPresent(setting -> setting.setValue(value));
        }
    }

    public List<Setting> list() {
        return listSettings;
    }

}
