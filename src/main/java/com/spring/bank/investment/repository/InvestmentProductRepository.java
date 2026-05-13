package com.spring.bank.investment.repository;

import com.spring.bank.investment.enums.ProductType;
import com.spring.bank.investment.model.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {

    List<InvestmentProduct> findByActiveTrue();

    List<InvestmentProduct> findByTypeAndActiveTrue(ProductType type);
}
