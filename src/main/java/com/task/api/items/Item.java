package com.task.api.items;

import com.task.api.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;


@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;
}
