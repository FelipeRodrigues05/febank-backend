package com.spring.bank.card.repository;

import com.spring.bank.card.model.CashbackRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CashbackRepository extends JpaRepository<CashbackRecord, Long> {
    List<CashbackRecord> findByCardIdAndCreditedFalse(String cardId);

    @Query("SELECT COALESCE(SUM(cashbackRecord.cashbackAmount), 0) FROM CashbackRecord cashbackRecord WHERE cashbackRecord.card.id = :cardId")
    BigDecimal sumCashbackAmountByCardId(@Param("cardId") String cardId);
}
