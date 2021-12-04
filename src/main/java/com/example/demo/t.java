package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class t {
	@GetMapping("/api/t")
	public String g () {
		return "test appi ";
	}
}
