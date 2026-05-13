package com.spring.bank.pix.repository;

import com.spring.bank.pix.enums.ScheduledPixStatus;
import com.spring.bank.pix.model.ScheduledPix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduledPixRepository extends JpaRepository<ScheduledPix, Long> {
    List<ScheduledPix> findByStatusAndScheduledDateLessThanEqual(ScheduledPixStatus status, LocalDate date);
    List<ScheduledPix> findByFromAccountId(Long accountId);
}
