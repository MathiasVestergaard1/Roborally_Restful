package com.example.Roborally;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class RoborallyController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/roborally")
	public Roborally greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Roborally(counter.incrementAndGet(), String.format(template, name));
	}
}
