package com.spring.service.scheduleauto;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.spring.repository.ScheduleTimeRepository;
import com.spring.service.email.MailServices;

@EnableScheduling
@Service
public class schedule_auto {
	@Autowired
	ScheduleTimeRepository 	scheduleTimeRepository;
	@Autowired
	MailServices mailServices;
	
	@Scheduled(cron = "0 0 1 * * ?") // hàng ngày lúc 1h sáng
	public void addScheduleTime() {
//		scheduleTimeRepository.postAuto();
//		scheduleTimeRepository.clearRecycle();
		mailServices.push("binhhtph11879@fpt.edu.vn", "abc", "<html>" + "<body>"
				+ "Hệ thống vừa thêm lịch khám ngày: "+scheduleTimeRepository.postAuto()+" lúc "+ LocalDateTime.now()+
				"dọc rác thành công " + scheduleTimeRepository.clearRecycle()+
				"</body>" + "</html>"); 
	}
	
	@Scheduled(cron = "0 0 12 * * ?") // hàng ngày lúc 12h đêm
	public void addScheduleTime2() {
		mailServices.push("binhhtph11879@fpt.edu.vn", "abc", "<html>" + "<body>"
				+ "Hệ thống vừa thêm lịch khám ngày: "+scheduleTimeRepository.postAuto()+" lúc "+ LocalDateTime.now()+
				"dọc rác thành công " + scheduleTimeRepository.clearRecycle()+
				"</body>" + "</html>"); 
	}
	
//	@Scheduled(cron = "0 */2 * ? * *") // 30s
	@Scheduled(fixedDelay = 30000)
	public void CleaerRecycle() {
		System.out.println("dọn rác");
//		scheduleTimeRepository.postAuto();
		mailServices.push("binhhtph11879@fpt.edu.vn", "abc", "<html>" + "<body>"
				+ "Kết quả khám của bạn là:"+LocalDateTime.now()+"     số:  "+ scheduleTimeRepository.postAuto() +
				"</body>" + "</html>"); 
	}
}
