package com.example.demo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class api {
	@GetMapping("/api/test")
	public String te() {
		return "Test deploy heroku";
	}
}
