package com.acpulse.repository;

import com.acpulse.model.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LectureScheduleRepository extends JpaRepository<LectureSchedule, Integer> {
    List<LectureSchedule> findByLecturerId(Integer lecturerId);
    List<LectureSchedule> findByRoomId(Integer roomId);
    List<LectureSchedule> findByRoomIdAndDayOfWeek(Integer roomId, LectureSchedule.DayOfWeek dayOfWeek);
}
