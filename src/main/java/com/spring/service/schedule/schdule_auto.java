package com.spring.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.spring.repository.ScheduleTimeRepository;

@EnableScheduling
@Service
public class schdule_auto {
	@Autowired
	ScheduleTimeRepository 	scheduleTimeRepository;
	
	@Scheduled(cron = "0 0 1 * * ?") // hàng ngày lúc 1h sáng
	public void addScheduleTime() {
		scheduleTimeRepository.postAuto();
		scheduleTimeRepository.clearRecycle();
	}
	
	@Scheduled(cron = "0 */2 * ? * *") // 30s
	public void CleaerRecycle() {
		System.out.println("dọn rác");
//		scheduleTimeRepository.postAuto();
	}
}
