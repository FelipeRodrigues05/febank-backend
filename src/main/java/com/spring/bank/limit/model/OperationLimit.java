package com.spring.bank.limit.model;

import com.spring.bank.limit.enums.LimitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "operation_limits",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "limit_type"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "limit_type")
    private LimitType limitType;

    private BigDecimal maxAmount;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
