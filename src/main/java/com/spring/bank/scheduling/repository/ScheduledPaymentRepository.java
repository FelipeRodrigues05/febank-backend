package com.spring.bank.scheduling.repository;

import com.spring.bank.scheduling.enums.ScheduledPaymentStatus;
import com.spring.bank.scheduling.model.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {

    List<ScheduledPayment> findByStatusAndScheduledDateLessThanEqual(ScheduledPaymentStatus status, LocalDate date);

    List<ScheduledPayment> findByFromAccountId(Long accountId);
}
