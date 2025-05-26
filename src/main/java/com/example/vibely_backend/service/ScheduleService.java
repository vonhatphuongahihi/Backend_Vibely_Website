package com.example.vibely_backend.service;

import com.example.vibely_backend.dto.request.ScheduleRequest;
import com.example.vibely_backend.entity.Schedule;
import com.example.vibely_backend.repository.ScheduleRepository;
import com.example.vibely_backend.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final GoogleCalendarService googleCalendarService;
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public Schedule createSchedule(String userId, ScheduleRequest request) {
        log.info("Creating new schedule for user: {}", userId);

        if (request.getSubject() == null || request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Thiếu thông tin lịch trình");
        }

        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setSubject(request.getSubject());

        // Chuyển đổi thời gian sang múi giờ Việt Nam
        LocalDateTime startTime = request.getStartTime().atZone(VIETNAM_ZONE).toLocalDateTime();
        LocalDateTime endTime = request.getEndTime().atZone(VIETNAM_ZONE).toLocalDateTime();

        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setCategoryColor(request.getCategoryColor() != null ? request.getCategoryColor() : "#0000FF");

        // Lưu vào database
        log.info("Đang lưu lịch vào database");
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Đã lưu lịch vào database với ID: {}", savedSchedule.getId());

        try {
            // Tạo sự kiện trên Google Calendar
            log.info("Bắt đầu tạo sự kiện trên Google Calendar");
            var googleEvent = googleCalendarService.createGoogleCalendarEvent(savedSchedule);
            // Lưu ID của sự kiện Google Calendar
            savedSchedule.setGoogleCalendarEventId(googleEvent.getId());
            log.info("Đã tạo sự kiện Google Calendar với ID: {}", googleEvent.getId());
            return scheduleRepository.save(savedSchedule);
        } catch (Exception e) {
            log.error("Lỗi khi tạo sự kiện Google Calendar: {}", e.getMessage(), e);
            // Không throw exception để không ảnh hưởng đến việc lưu schedule
            return savedSchedule;
        }
    }

    public List<Schedule> getUserSchedules(String userId) {
        log.info("Getting schedules for user: {}", userId);
        return scheduleRepository.findByUserIdOrderByStartTimeAsc(userId);
    }

    public Schedule updateSchedule(String id, String userId, ScheduleRequest request) {
        log.info("Updating schedule {} for user: {}", id, userId);

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch trình không tồn tại"));

        if (!schedule.getUserId().equals(userId)) {
            log.warn("User {} không có quyền chỉnh sửa lịch {}", userId, id);
            throw new RuntimeException("Bạn không có quyền chỉnh sửa lịch này");
        }

        schedule.setSubject(request.getSubject());

        // Chuyển đổi thời gian sang múi giờ Việt Nam
        LocalDateTime startTime = request.getStartTime().atZone(VIETNAM_ZONE).toLocalDateTime();
        LocalDateTime endTime = request.getEndTime().atZone(VIETNAM_ZONE).toLocalDateTime();

        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setCategoryColor(request.getCategoryColor());

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        try {
            if (schedule.getGoogleCalendarEventId() != null) {
                googleCalendarService.updateGoogleCalendarEvent(schedule.getGoogleCalendarEventId(), updatedSchedule);
            } else {
                var googleEvent = googleCalendarService.createGoogleCalendarEvent(updatedSchedule);
                updatedSchedule.setGoogleCalendarEventId(googleEvent.getId());
                scheduleRepository.save(updatedSchedule);
            }
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật sự kiện Google Calendar: {}", e.getMessage(), e);
        }
        return updatedSchedule;
    }

    public void deleteSchedule(String userId, String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Lịch trình không tồn tại"));
        if (!schedule.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa lịch này");
        }
        try {
            if (schedule.getGoogleCalendarEventId() != null) {
                googleCalendarService.deleteGoogleCalendarEvent(schedule.getGoogleCalendarEventId());
            }
        } catch (Exception e) {
            log.error("Lỗi khi xóa sự kiện Google Calendar: {}", e.getMessage(), e);
        }
        scheduleRepository.deleteByIdAndUserId(scheduleId, userId);
    }

    public Schedule getScheduleById(String userId, String scheduleId) {
        log.info("Getting schedule {} for user: {}", scheduleId, userId);
        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId);
        if (schedule == null) {
            throw new IllegalArgumentException("Không tìm thấy lịch trình");
        }
        return schedule;
    }

    public List<Schedule> getSchedulesByUserId(String userId) {
        log.debug("Lấy danh sách lịch cho userId: {}", userId);
        return scheduleRepository.findByUserId(userId);
    }
}
