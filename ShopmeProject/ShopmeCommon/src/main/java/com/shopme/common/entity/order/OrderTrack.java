package com.shopme.common.entity.order;

import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class OrderTrack extends IdBasedEntity {

    @Column(length = 256)
    private String notes;

    private Date updatedTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
