package com.spring.bank.transaction.repository;

import com.spring.bank.transaction.enums.TransactionStatusEnum;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.model.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionQueryRepository {

    private final EntityManager entityManager;

    public List<Transaction> findWithFilters(
            Long accountId,
            TransactionTypeEnum type,
            TransactionStatusEnum status,
            LocalDate from,
            LocalDate to,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String description,
            int page,
            int size
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = criteriaQuery.from(Transaction.class);

        List<Predicate> predicates = buildPredicates(criteriaBuilder, root, accountId, type, status, from, to, minAmount, maxAmount, description);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));

        TypedQuery<Transaction> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long countWithFilters(
            Long accountId,
            TransactionTypeEnum type,
            TransactionStatusEnum status,
            LocalDate from,
            LocalDate to,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String description
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Transaction> root = criteriaQuery.from(Transaction.class);

        List<Predicate> predicates = buildPredicates(criteriaBuilder, root, accountId, type, status, from, to, minAmount, maxAmount, description);

        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder criteriaBuilder,
            Root<Transaction> root,
            Long accountId,
            TransactionTypeEnum type,
            TransactionStatusEnum status,
            LocalDate from,
            LocalDate to,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String description
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (accountId != null) {
            predicates.add(criteriaBuilder.equal(root.get("account").get("id"), accountId));
        }
        if (type != null) {
            predicates.add(criteriaBuilder.equal(root.get("type"), type));
        }
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        if (from != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay()));
        }
        if (to != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to.atTime(23, 59, 59)));
        }
        if (minAmount != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
        }
        if (maxAmount != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }
        if (description != null && !description.isBlank()) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + description.toLowerCase() + "%"
            ));
        }

        return predicates;
    }
}
