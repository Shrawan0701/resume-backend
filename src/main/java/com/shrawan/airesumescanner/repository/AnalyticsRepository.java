package com.shrawan.airesumescanner.repository;

import com.shrawan.airesumescanner.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
}
