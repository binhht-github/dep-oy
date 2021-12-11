package com.spring.service.schedule_time;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.spring.dto.model.ScheduleTimeDTO;
import com.spring.model.ScheduleTime;

public interface ScheduleTimeService {

	// read all schedule_time
	public List<ScheduleTimeDTO> readAll();

	public ScheduleTimeDTO create(ScheduleTimeDTO dto);

	public ScheduleTimeDTO update(ScheduleTimeDTO dto);

	public ScheduleTimeDTO delete(Long id);

	// soft-delete
	public ScheduleTimeDTO updateDeleteAt(Long id, Boolean deleteAt);

	// read all schedule_time by deleteAt=TRUE(Recycle_Bin)
	public List<ScheduleTimeDTO> readAllDeleteAtTrue();

	// read all schedule_time by deleteAt=FALSE
	public List<ScheduleTimeDTO> readAllDeleteAtFalse();

	// read all schedule_time by DentistProfile_ID
	// hiển thị giờ làm việc của bác sĩ
	public List<ScheduleTimeDTO> readAllTimeByDentistId(Long dentistProfileId);

	public List<LocalDate> readAllTimeByDentistId2(Long dentistProfileId);

	// findById
	public ScheduleTimeDTO readById(Long id);

	// find hour by dayOfWeek and dentist_Id
	public List<ScheduleTimeDTO> readHourByDayAndDentistId(LocalDate dayOfWeek, Long dentistId);
	
	public int addADuto();
	
	public int cleaerRecycle();

}
