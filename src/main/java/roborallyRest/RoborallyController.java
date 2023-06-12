package roborallyRest;

import java.io.FileReader;
import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@RestController
public class RoborallyController {
	private String url;
	private String port;
	private String database;
	private String username;
	private String password;

	RoborallyController() {
		JSONObject jsonFile = new JSONObject();
		try {
			FileReader reader = new FileReader("config.json");
			JSONParser jsonParser = new JSONParser();
			jsonFile = (JSONObject) jsonParser.parse(reader);

		} catch (IOException | NullPointerException | ParseException ignored) {}

		this.url = (String) jsonFile.get("url");
		this.port = (String) jsonFile.get("port");
		this.database = (String) jsonFile.get("database");
		this.username = (String) jsonFile.get("username");
		this.password = (String) jsonFile.get("password");
	}

	@GetMapping("/roborally/boards")
	ResponseEntity<Object> getBoards() throws IOException {
		JSONObject boards = null;
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT * FROM boardsettings WHERE `Type` = 'Board';");

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

	@GetMapping("/roborally/saves")
	ResponseEntity<Object> getSaves() throws IOException {
		JSONObject boards = null;
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT * FROM boardsettings WHERE `Type` = 'Save';");

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

	@GetMapping("/roborally/games")
	ResponseEntity<Object> all() {
		JSONArray games = null;

		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT Name FROM boardsettings WHERE `Type` = 'Game';");

			games = new JSONArray();

			while (response.next()) {
				games.add(response.getString("Name"));
			}

			connection.close();

		}catch (Exception e) {
			e.printStackTrace();
		}


		return new ResponseEntity<>(
				games,
				HttpStatus.OK
		);
	}

	@PostMapping("/roborally/updatePlayer")
	public Object setPlayer(@RequestBody JSONObject jsonObject) throws SQLException {
		String Heading = (String) jsonObject.get("Heading");
		int ID = (int) jsonObject.get("ID");
		int X = (int) jsonObject.get("x");
		int Y = (int) jsonObject.get("y");
		String playerHand = (String) jsonObject.get("playerHand");
		String playerProgram = (String) jsonObject.get("playerProgram");
		String Board = (String) jsonObject.get("Board");


		Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

		String command = "UPDATE player SET Heading = ?, X = ?, Y = ?, playerHand = ?, playerProgram = ? WHERE Board = ? AND ID = ?;";
		try (PreparedStatement updateStmt = connection.prepareStatement(command)) {
			updateStmt.setObject(1, Heading);
			updateStmt.setObject(2, X);
			updateStmt.setObject(3, Y);
			updateStmt.setObject(4, playerHand);
			updateStmt.setObject(5, playerProgram);
			updateStmt.setObject(6, Board);
			updateStmt.setObject(7, ID);
			updateStmt.execute();
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		connection.close();

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}

	@PostMapping("/roborally/updateCheckpoint")
	public Object setCheckpoint(@RequestBody JSONObject jsonObject) throws SQLException {
		int X = (int) jsonObject.get("x");
		int Y = (int) jsonObject.get("y");
		int ID = (int) jsonObject.get("ID");
		String Board = (String) jsonObject.get("Board");


		Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

		String checkpointCommand = "UPDATE obstacle SET X = ?, Y = ? WHERE Board = ? AND `Type` = ?;";
		try (PreparedStatement updateStmt = connection.prepareStatement(checkpointCommand)) {
			updateStmt.setObject(1, X);
			updateStmt.setObject(2, Y);
			updateStmt.setObject(3, Board);
			updateStmt.setObject(4, "Checkpoint");
			updateStmt.execute();
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		String playerCommand = "UPDATE player SET Points = Points + 1 WHERE ID = ? AND Board = ?;";

		try (PreparedStatement updateStmt = connection.prepareStatement(playerCommand)) {
			updateStmt.setObject(1, ID);
			updateStmt.setObject(2, Board);
			updateStmt.execute();
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		connection.close();

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}
}
