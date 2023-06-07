package com.example.Roborally;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@RestController
public class RoborallyController {

	@GetMapping("/roborally")
	public ResponseEntity<Object> getData() throws IOException, ParseException {
		FileReader reader = new FileReader("save.json");
		JSONParser jsonParser = new JSONParser(reader);
		Object jsonString = jsonParser.parse();

		return new ResponseEntity<>(
				jsonString,
				HttpStatus.OK
		);
	}

	@PostMapping("/roborally")
	public Object setData(@RequestBody Object object) throws IOException {
		Files.write(Paths.get("save.json"), object.toString().getBytes());

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}
}
