package com.acpulse.repository;

import com.acpulse.model.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LectureScheduleRepository extends JpaRepository<LectureSchedule, Integer> {
    List<LectureSchedule> findByLecturer_Id(Integer lecturerId);
    List<LectureSchedule> findByLecturer_IdOrderByDayOfWeekAscStartTimeAsc(Integer lecturerId);
}