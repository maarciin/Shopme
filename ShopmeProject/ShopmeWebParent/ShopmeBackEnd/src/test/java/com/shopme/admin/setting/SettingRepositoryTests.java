package com.shopme.admin.setting;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepositoryTests {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testCreateGeneralSettings() {
        //given
//        Setting siteName = new Setting("SITE_NAME", "Shopme", SettingCategory.GENERAL);
        Setting siteName = new Setting("SITE_LOGO", "Shopme.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2021 Shopme Ltd.", SettingCategory.GENERAL);

        //when
//        Setting savedSetting = settingRepository.save(siteName);
        settingRepository.saveAll(List.of(siteName, copyright));
        Iterable<Setting> allSettings = settingRepository.findAll();

        //then
//        assertThat(savedSetting).isNotNull();
        assertThat(allSettings).size().isGreaterThan(0);
    }

    @Test
    public void testListSettingsByCategory() {
        //given
        SettingCategory generalCategory = SettingCategory.GENERAL;

        //when
        List<Setting> categories = settingRepository.findByCategory(generalCategory);

        //then
        categories.forEach(System.out::println);
        assertThat(categories.size()).isGreaterThan(0);

    }

}