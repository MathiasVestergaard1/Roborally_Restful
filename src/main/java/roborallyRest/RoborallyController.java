package roborallyRest;

import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@RestController
public class RoborallyController {

	@GetMapping("/roborally/boards")
	public ResponseEntity<Object> getData() throws IOException {
		JSONObject boards = null;
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/roborally_test?characterEncoding=utf8", "root", "mads3241");

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT * FROM boardsettings;");

			boards = new JSONObject();

			while (response.next()) {
				JSONObject board = new JSONObject();
				JSONObject boardSettings = new JSONObject();

				boardSettings.put("width", response.getString("width"));
				boardSettings.put("height", response.getString("height"));

				ResultSet obstaclesQuery = statement.executeQuery("SELECT * FROM obstacle WHERE Board = '" + response.getString("Name") + "';");
				JSONArray obstacles = new JSONArray();

				while (obstaclesQuery.next()) {
					JSONObject position = new JSONObject();

					position.put("x", obstaclesQuery.getString("X"));
					position.put("y", obstaclesQuery.getString("Y"));

					JSONObject obstacle = new JSONObject();

					obstacle.put("heading", obstaclesQuery.getString("Heading"));
					obstacle.put("position", position);
					obstacle.put("type", obstaclesQuery.getString("Type"));

					obstacles.add(obstacle);
				}

				board.put("obstacles", obstacles);

				ResultSet checkpointsQuery = statement.executeQuery("SELECT * FROM checkpoint WHERE Board = '" + response.getString("Name") + "';");
				JSONArray checkpoints = new JSONArray();

				while (checkpointsQuery.next()) {
					JSONObject position = new JSONObject();

					position.put("x", checkpointsQuery.getString("X"));
					position.put("y", checkpointsQuery.getString("Y"));

					JSONObject checkpoint = new JSONObject();

					checkpoint.put("position", position);

					checkpoints.add(checkpoint);
				}

				board.put("checkpoints", checkpoints);

				boards.put(response.getString("Name"), board);
			}


			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(
				boards,
				HttpStatus.OK
		);
	}
/*
	@GetMapping("/roborally/games")
	List<Game> all() {
		return repository.findAll();
	}


	@PostMapping("/roborally/newgame")
	public Game newGame(@RequestBody Game game) {
		return repository.save(game);
	}*/

	@PostMapping("/roborally")
	public Object setData(@RequestBody Object object) throws IOException {
		Files.write(Paths.get("save.json"), object.toString().getBytes());

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}
}
