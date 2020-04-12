package com.example.exchangeratesservice.repository;

import com.example.exchangeratesservice.repository.entity.ExchangeRateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRatesRepository extends JpaRepository<ExchangeRateEntity, Integer> {

    List<ExchangeRateEntity> findAllByOrderByMonthCounterAsc();
}
