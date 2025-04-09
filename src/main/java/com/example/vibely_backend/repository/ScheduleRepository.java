package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    List<Schedule> findByUserIdOrderByStartTimeAsc(String userId);

    Schedule findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    List<Schedule> findByUserId(String userId);
}