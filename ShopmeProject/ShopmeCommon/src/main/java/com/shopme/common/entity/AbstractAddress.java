package com.shopme.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAddress extends IdBasedEntity {

    @Column(length = 45, nullable = false)
    protected String firstName;

    @Column(length = 45, nullable = false)
    protected String lastName;

    @Column(length = 15, nullable = false)
    protected String phoneNumber;

    @Column(length = 64, nullable = false)
    protected String addressLine1;

    @Column(length = 64)
    protected String addressLine2;

    @Column(length = 45, nullable = false)
    protected String city;

    @Column(length = 45, nullable = false)
    protected String state;

    @Column(length = 10, nullable = false)
    protected String postalCode;
}
