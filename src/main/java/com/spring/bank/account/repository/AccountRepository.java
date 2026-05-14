package com.spring.bank.account.repository;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);
    boolean existsByNumber(String number);
    boolean existsByUserIdAndType(Long userId, AccountTypeEnum type);
    Optional<Account> findFirstByUserIdAndType(Long userId, AccountTypeEnum type);
    List<Account> findAllByTypeAndStatus(AccountTypeEnum type, AccountStatusEnum status);
    List<Account> findByUserId(Long userId);
}
