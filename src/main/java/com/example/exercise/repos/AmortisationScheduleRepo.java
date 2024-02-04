package com.example.exercise.repos;

import com.example.exercise.entity.AmortisationSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmortisationScheduleRepo extends JpaRepository<AmortisationSchedules, Long> {

    AmortisationSchedules findTopByAccountId(int id);

    AmortisationSchedules findTopByAccountIdOrderByPeriodDesc(int id);

    List<AmortisationSchedules> findByAccountId(int id);
}
