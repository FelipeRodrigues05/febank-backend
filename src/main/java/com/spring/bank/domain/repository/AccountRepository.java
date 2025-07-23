package com.spring.bank.domain.repository;

import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);

    boolean existsByNumber(String number);

    Optional<Account> findFirstByUserIdAndType(Long userId, AccountTypeEnum type);
    Optional<List<Account>> findAllByType(AccountTypeEnum type);
}
