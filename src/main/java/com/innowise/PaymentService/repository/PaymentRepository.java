package com.innowise.PaymentService.repository;

import com.innowise.PaymentService.dto.SumResult;
import com.innowise.PaymentService.entity.Payment;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, Long> {

    List<Payment> findAllByOrderId(Long orderId);
    List<Payment> findAllByUserId(Long userId);
    List<Payment> findAllByStatus(String status);

    @Aggregation(pipeline = {
            "{ '$match': { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
            "{ '$group': { _id: null, totalSum: { $sum: '$amount' } } }",
            "{ '$project': { _id: 0, totalSum: 1 } }"
    })
    SumResult getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate);

}
