package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@Getter
@Setter
public class Customer extends AbstractAddressWithCountry {

    @Column(nullable = false, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 64)
    private String verificationCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AuthenticationType authenticationType;

    @Column(length = 64)
    private String resetPasswordToken;

    public Customer(Integer id) {
        this.id = id;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

}

