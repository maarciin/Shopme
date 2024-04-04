package com.shopme.common.entity.setting;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Setting {

    @Id
    @Column(name = "`key`", nullable = false, length = 128)
    @EqualsAndHashCode.Include
    private String key;

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private SettingCategory category;

    public Setting(String key) {
        this.key = key;
    }
}
