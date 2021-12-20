package com.spring.service.thongkekhachhangdakham;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.dto.model.thongkekhachhangdadatlich;
import com.spring.dto.model.thongkekhachhangdakham;
import com.spring.repository.CustomerProfileRepository;

@Service
public class tkkhdkServices {
	@Autowired
	CustomerProfileRepository customerProfileRepository;
	
	public List<thongkekhachhangdakham> ThongKeDaKham() {
		List<thongkekhachhangdakham> list = new ArrayList<>();

		customerProfileRepository.list().forEach(obj -> {
			list.add(new thongkekhachhangdakham(obj[0]+"", Integer.parseInt(obj[1]+""))); 
		});
		return list;
	}
	
	public List<thongkekhachhangdadatlich> ThongKeDaDatLich() {
		List<thongkekhachhangdadatlich> list = new ArrayList<>();

		customerProfileRepository.list2().forEach(obj -> {
			list.add(new thongkekhachhangdadatlich(obj[0]+"", Integer.parseInt(obj[1]+""))); 
		});
		return list;
	}
}
