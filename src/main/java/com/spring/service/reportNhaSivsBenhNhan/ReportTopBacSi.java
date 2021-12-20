package com.spring.service.reportNhaSivsBenhNhan;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.dto.model.ReportSoBenhNhanKham;
import com.spring.repository.DentistProfileRepository;

@Service
public class ReportTopBacSi {
	@Autowired
	DentistProfileRepository dentistProfileRepository;
	
	public List<ReportSoBenhNhanKham> list() {
		List<ReportSoBenhNhanKham> list = new ArrayList<>();
		
		dentistProfileRepository.list().forEach(obj -> {
			list.add(new ReportSoBenhNhanKham(obj[0] + "", Integer.parseInt(obj[1] + "")));
		});
		return list;
	}
}
