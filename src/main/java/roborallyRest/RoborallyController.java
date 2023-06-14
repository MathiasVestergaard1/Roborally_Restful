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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;


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
	ResponseEntity<Object> getBoards() {
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

				board.put("board", boardSettings);

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
	ResponseEntity<Object> getSaves() {
		JSONObject boards = null;
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT * FROM boardsettings WHERE `Type` = 'Save';");

			boards = new JSONObject();

			while (response.next()) {
				JSONObject board = new JSONObject();
				JSONObject boardSettings = new JSONObject();

				if (Objects.equals(response.getString("stepMode"), "true")) {
					boardSettings.put("stepMode", true);
				} else {
					boardSettings.put("stepMode", false);
				}

				boardSettings.put("width", response.getString("width"));
				boardSettings.put("height", response.getString("height"));
				boardSettings.put("phase", response.getString("Phase"));
				boardSettings.put("currentPlayer", response.getString("currentPlayer"));
				boardSettings.put("currentStep", response.getString("currentStep"));

				board.put("board", boardSettings);

				ResultSet obstaclesQuery = statement.executeQuery("SELECT * FROM obstacle WHERE Board = '" + response.getString("originBoard") + "';");
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

				ResultSet checkpointsQuery = statement.executeQuery("SELECT * FROM checkpoint WHERE Board = '" + response.getString("originBoard") + "';");
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

				ResultSet playersQuery = statement.executeQuery("SELECT * FROM player WHERE Board = '" + response.getString("Name") + "';");
				JSONArray players = new JSONArray();

				while (playersQuery.next()) {
					JSONObject position = new JSONObject();

					position.put("x", playersQuery.getString("X"));
					position.put("y", playersQuery.getString("Y"));

					JSONObject player = new JSONObject();

					player.put("position", position);
					player.put("heading", playersQuery.getString("Heading"));
					player.put("ID", playersQuery.getString("ID"));
					player.put("playerHand", playersQuery.getString("playerHand"));
					player.put("programHand", playersQuery.getString("playerProgram"));
					player.put("points", playersQuery.getString("Points"));


					players.add(player);
				}

				board.put("players", players);

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
	ResponseEntity<Object> games() {
		JSONObject games = null;

		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT Name, playerCount, playerLimit FROM boardsettings WHERE `Type` = 'Game';");

			games = new JSONObject();

			while (response.next()) {
				JSONObject game = new JSONObject();

				game.put("playerCount", response.getString("playerCount"));
				game.put("playerLimit", response.getString("playerLimit"));

				games.put(response.getString("Name"), game);
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

	@PostMapping("/roborally/createBoards")
	public Object createBoards(@RequestBody JSONObject jsonObject) {

		LinkedHashMap<?, ?> boards = (LinkedHashMap<?, ?>) jsonObject.get("boards");

		Set keys = boards.keySet();

		for (Object key : keys) {
			LinkedHashMap<?, ?> board = (LinkedHashMap<?, ?>) boards.get(key);

			LinkedHashMap<?, ?> boardSettings = (LinkedHashMap<?, ?>) board.get("board");

			int width = Integer.parseInt((String) boardSettings.get("width"));
			int height = Integer.parseInt((String) boardSettings.get("height"));

			try {
				Connection connection = DriverManager.getConnection("jdbc:mysql://" +
						url +
						":" +
						port +
						"/" +
						database +
						"?characterEncoding=utf8",
						username, password);

				Statement statement = connection.createStatement();

				statement.executeUpdate("INSERT INTO boardsettings " +
						"(Name, Type, Width, Height) " +
						"VALUES " +
						"('" + key + "', 'Board', '" + width + "', '" + height + "');");

				connection.close();
			} catch(Exception err) {
				System.out.println("An error has occurred.");
				System.out.println("See full details below.");
				err.printStackTrace();
			}

			ArrayList<?> obstacles = (ArrayList<?>) board.get("obstacles");

			for (Object obstacle : obstacles) {
				LinkedHashMap<?, ?> jsonObstacle = (LinkedHashMap<?, ?>) obstacle;
				String heading = (String) jsonObstacle.get("heading");

				LinkedHashMap<?, ?> position = (LinkedHashMap<?, ?>) jsonObstacle.get("position");
				int X = Integer.parseInt((String) position.get("x"));
				int Y = Integer.parseInt((String) position.get("y"));

				String type = (String) jsonObstacle.get("type");

				try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

					Statement statement = connection.createStatement();

					statement.executeUpdate("INSERT INTO obstacle " +
							"(Heading, X, Y, Type, Board) " +
							"VALUES " +
							"('" + heading + "', '"+ X + "', '" + Y + "', '" + type + "', '" + key + "');");

					connection.close();
				} catch(Exception err) {
					System.out.println("An error has occurred.");
					System.out.println("See full details below.");
					err.printStackTrace();
				}

			}

			ArrayList<?> checkpoints = (ArrayList<?>) board.get("checkpoints");

			for (Object checkpoint : checkpoints) {
				LinkedHashMap<?, ?> jsonCheckpoint = (LinkedHashMap<?, ?>) checkpoint;

				LinkedHashMap<?, ?> position = (LinkedHashMap<?, ?>) jsonCheckpoint.get("position");
				int X = Integer.parseInt((String) position.get("x"));
				int Y = Integer.parseInt((String) position.get("y"));

				try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

					Statement statement = connection.createStatement();

					statement.executeUpdate("INSERT INTO checkpoint " +
							"(X, Y, Board) " +
							"VALUES " +
							"('" + X + "', '"+ Y + "', '" + key + "');");

					connection.close();
				} catch(Exception err) {
					System.out.println("An error has occurred.");
					System.out.println("See full details below.");
					err.printStackTrace();
				}

			}
		}
		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}

	@PostMapping("/roborally/newGame")
	public Object newGame(@RequestBody JSONObject jsonObject) {
		String board = (String) jsonObject.get("Board");

		String name = (String) jsonObject.get("Name");

		int ID = (int) jsonObject.get("ID");

		String heading = (String) jsonObject.get("Heading");

		int X = (int) jsonObject.get("x");
		int Y = (int) jsonObject.get("y");

		int playerLimit = (int) jsonObject.get("playerLimit");

		int width = 8;
		int height = 8;

		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT * FROM boardsettings WHERE `Type` = 'Board' AND Name = '" + board + "';");

			while (response.next()) {
				width = response.getInt("width");
				height = response.getInt("height");
			}

			statement.executeUpdate("INSERT INTO boardsettings " +
					"(Name, Type, Width, Height, playerLimit, originBoard) " +
					"VALUES " +
					"('" + name + "', 'Game', '" + width + "', '" + height + "', '" + playerLimit + "', '" + board + "');");

			statement.executeUpdate("INSERT INTO player " +
					"(Heading, ID, X, Y, Board) " +
					"VALUES " +
					"('" + heading + "', '" + ID + "', '" + X + "', '" + Y + "', '" + name + "');");

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(
				HttpStatus.OK
		);

	}

	@PostMapping("/roborally/joinGame")
	public Object joinGame(@RequestBody JSONObject jsonObject) throws SQLException {
		String name = (String) jsonObject.get("name");

		Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

		String command = "UPDATE boardsettings SET playerCount = playerCount + 1 WHERE Name = ? AND `Type` = ?;";
		try (PreparedStatement updateStmt = connection.prepareStatement(command)) {
			updateStmt.setObject(1, name);
			updateStmt.setObject(2, "Game");
			updateStmt.execute();
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		Statement statement = connection.createStatement();

		ResultSet response = statement.executeQuery("SELECT Max(ID) as ID FROM player WHERE Board = '" + name + "';");

		int ID = 0;

		while (response.next()) {
			ID = response.getInt("ID") + 1;
		}

		statement.executeUpdate("INSERT INTO player " +
				"(Heading, ID, X, Y, Board) " +
				"VALUES " +
				"('SOUTH', '" + ID + "', '0', '0', '" + name + "');");

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}


	@PostMapping("/roborally/saveGame")
	public Object saveGame(@RequestBody JSONObject jsonObject) throws SQLException {
		Set keys = jsonObject.keySet();
		String name = (String) keys.toArray()[0];

		LinkedHashMap<?, ?> game = (LinkedHashMap<?, ?>) jsonObject.get(name);
		ArrayList<Object> players = (ArrayList<Object>) game.get("players");

		Connection connection = DriverManager.getConnection("jdbc:mysql://" +
				url +
				":" +
				port +
				"/" +
				database +
				"?characterEncoding=utf8",
				username, password);
		Statement statement = connection.createStatement();

		LinkedHashMap<?, ?> boardsettings = (LinkedHashMap<?, ?>) game.get("board");

		String phase = (String) boardsettings.get("phase");
		int currentStep = Integer.parseInt((String) boardsettings.get("currentStep"));
		int currentPlayer = Integer.parseInt((String) boardsettings.get("currentPlayer"));
		int width = Integer.parseInt((String) boardsettings.get("width"));
		int height = Integer.parseInt((String) boardsettings.get("height"));
		String stepMode = boardsettings.get("stepMode").toString();
		String originBoard = (String) boardsettings.get("originBoard");

		try {
			statement.executeUpdate("INSERT INTO boardsettings " +
					"(Name, Type, Width, Height, Phase, currentPlayer, currentStep, stepMode, originBoard) " +
					"VALUES " +
					"('" + name + "', 'Save', '" + width + "', '" + height + "', '" + phase + "', '" + currentPlayer + "', '" + currentStep + "', '" + stepMode + "', '" + originBoard + "');");
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		for (Object player : players) {
			LinkedHashMap<?, ?> jsonPlayer = (LinkedHashMap<?, ?>) player;

			String Heading = (String) jsonPlayer.get("heading");
			int ID = Integer.parseInt((String) jsonPlayer.get("ID"));

			LinkedHashMap<?, ?> position = (LinkedHashMap<?, ?>) jsonPlayer.get("position");
			int X = Integer.parseInt((String) position.get("x"));
			int Y = Integer.parseInt((String) position.get("y"));

			String playerHand = jsonPlayer.get("playerHand").toString();
			String playerProgram = jsonPlayer.get("programHand").toString();

			int points = Integer.parseInt((String) jsonPlayer.get("points"));

			try {
				statement.executeUpdate("INSERT INTO player " +
						"(Heading, ID, X, Y, playerHand, playerProgram, Points, Board) " +
						"VALUES " +
						"('" + Heading + "','" + ID + "', '" + X + "', '" + Y + "', '" + playerHand + "', '" + playerProgram + "', '" + points + "', '" + name + "');");
			} catch(Exception err) {
				System.out.println("An error has occurred.");
				System.out.println("See full details below.");
				err.printStackTrace();
			}
		}

		ArrayList<Object> obstacles = (ArrayList<Object>) game.get("obstacles");

		for (Object obstacle : obstacles) {
			LinkedHashMap<?, ?> jsonObstacle = (LinkedHashMap<?, ?>) obstacle;
			String Type = (String) jsonObstacle.get("type");

			if (!Objects.equals(Type, "Checkpoint")) {
				break;
			}
			String Heading = (String) jsonObstacle.get("heading");

			LinkedHashMap<?, ?> position = (LinkedHashMap<?, ?>) jsonObstacle.get("position");
			int X = Integer.parseInt((String) position.get("x"));
			int Y = Integer.parseInt((String) position.get("y"));

			try {
				statement.executeUpdate("INSERT INTO obstacle " +
						"(Heading, X, Y, Type, Board) " +
						"VALUES " +
						"('" + Heading + "', '" + X + "', '" + Y + "', '" + Type + "', '" + name + "');");
			} catch(Exception err) {
				System.out.println("An error has occurred.");
				System.out.println("See full details below.");
				err.printStackTrace();
			}
		}

		connection.close();

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}

	@PostMapping("/roborally/overwriteGame")
	public Object overwriteGame(@RequestBody JSONObject jsonObject) throws SQLException {
		Set keys = jsonObject.keySet();
		String name = (String) keys.toArray()[0];

		LinkedHashMap<?, ?> game = (LinkedHashMap<?, ?>) jsonObject.get(name);
		ArrayList<Object> players = (ArrayList<Object>) game.get("players");

		Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

		LinkedHashMap<?, ?> boardsettings = (LinkedHashMap<?, ?>) game.get("board");

		String phase = (String) boardsettings.get("phase");
		int currentStep = Integer.parseInt((String) boardsettings.get("currentStep"));
		int currentPlayer = Integer.parseInt((String) boardsettings.get("currentPlayer"));
		int width = Integer.parseInt((String) boardsettings.get("width"));
		int height = Integer.parseInt((String) boardsettings.get("height"));
		String stepMode = boardsettings.get("stepMode").toString();
		String originBoard = (String) boardsettings.get("originBoard");

		String boardCommand = "UPDATE boardsettings SET `Type` = ?, Width = ?, Height = ?, Phase = ?, currentPlayer = ?, currentStep = ?, stepMode = ?, originBoard = ? WHERE Name = ? AND `Type` = ?;";
		try (PreparedStatement updateStmt = connection.prepareStatement(boardCommand)) {
			updateStmt.setObject(1, "Save");
			updateStmt.setObject(2, width);
			updateStmt.setObject(3, height);
			updateStmt.setObject(4, phase);
			updateStmt.setObject(5, currentPlayer);
			updateStmt.setObject(6, currentStep);
			updateStmt.setObject(7, stepMode);
			updateStmt.setObject(8, originBoard);
			updateStmt.setObject(9, name);
			updateStmt.setObject(10, "Save");
			updateStmt.execute();
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		for (Object player : players) {
			LinkedHashMap<?, ?> jsonPlayer = (LinkedHashMap<?, ?>) player;

			String Heading = (String) jsonPlayer.get("heading");
			int ID = Integer.parseInt((String) jsonPlayer.get("ID"));

			LinkedHashMap<?, ?> position = (LinkedHashMap<?, ?>) jsonPlayer.get("position");
			int X = Integer.parseInt((String) position.get("x"));
			int Y = Integer.parseInt((String) position.get("y"));

			String playerHand = jsonPlayer.get("playerHand").toString();
			String playerProgram = jsonPlayer.get("programHand").toString();

			int points = Integer.parseInt((String) jsonPlayer.get("points"));

			String playerCommand = "UPDATE player SET Heading = ?, X = ?, Y = ?, playerHand = ?, playerProgram = ?, Points = ? WHERE ID = ? AND Board = ?;";
			try (PreparedStatement updateStmt = connection.prepareStatement(playerCommand)) {
				updateStmt.setObject(1, Heading);
				updateStmt.setObject(2, X);
				updateStmt.setObject(3, Y);
				updateStmt.setObject(4, playerHand);
				updateStmt.setObject(5, playerProgram);
				updateStmt.setObject(6, points);
				updateStmt.setObject(7, ID);
				updateStmt.setObject(8, name);
				updateStmt.execute();
			} catch(Exception err) {
				System.out.println("An error has occurred.");
				System.out.println("See full details below.");
				err.printStackTrace();
			}
		}

		ArrayList<Object> obstacles = (ArrayList<Object>) game.get("obstacles");

		for (Object obstacle : obstacles) {
			LinkedHashMap<?, ?> jsonObstacle = (LinkedHashMap<?, ?>) obstacle;
			String Type = (String) jsonObstacle.get("type");

			if (!Objects.equals(Type, "Checkpoint")) {
				break;
			}
			LinkedHashMap<?, ?> position = (LinkedHashMap<?, ?>) jsonObstacle.get("position");
			int X = Integer.parseInt((String) position.get("x"));
			int Y = Integer.parseInt((String) position.get("y"));

			String obstacleCommand = "UPDATE obstacle SET X = ?, Y = ? WHERE Board = ?;";
			try (PreparedStatement updateStmt = connection.prepareStatement(obstacleCommand)) {
				updateStmt.setObject(1, X);
				updateStmt.setObject(2, Y);
				updateStmt.setObject(3, name);
				updateStmt.execute();
			} catch(Exception err) {
				System.out.println("An error has occurred.");
				System.out.println("See full details below.");
				err.printStackTrace();
			}

		}

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}

	@PostMapping("/roborally/updateSave")
	public Object updateSave(@RequestBody JSONObject jsonObject) throws SQLException {
		String name = (String) jsonObject.get("name");
		int currentStep = (int) jsonObject.get("currentStep");

		Connection connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?characterEncoding=utf8", username, password);

		String command = "UPDATE boardsettings SET `Type` = ?, currentStep = ? WHERE Name = ? AND `Type` = ?;";
		try (PreparedStatement updateStmt = connection.prepareStatement(command)) {
			updateStmt.setObject(1, "Save");
			updateStmt.setObject(2, currentStep);
			updateStmt.setObject(3, name);
			updateStmt.setObject(4, "Game");
			updateStmt.execute();
		} catch(Exception err) {
			System.out.println("An error has occurred.");
			System.out.println("See full details below.");
			err.printStackTrace();
		}

		return new ResponseEntity<>(
				HttpStatus.OK
		);
	}

	@GetMapping("/roborally/getGame")
	public Object getGame(@RequestParam(value = "name", defaultValue = "") String name) {

		JSONObject board = new JSONObject();
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" +
					url +
					":" +
					port +
					"/" +
					database +
					"?characterEncoding=utf8",
					username, password);

			Statement statement = connection.createStatement();

			ResultSet response = statement.executeQuery("SELECT * FROM boardsettings WHERE Name = '" + name + "';");


			while (response.next()) {
				JSONObject boardSettings = new JSONObject();

				boardSettings.put("width", response.getString("width"));
				boardSettings.put("height", response.getString("height"));

				board.put("board", boardSettings);

				ResultSet obstaclesQuery = statement.executeQuery("SELECT * FROM obstacle WHERE Board = '" +
						response.getString("originBoard") +
						"';");

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

				ResultSet checkpointsQuery = statement.executeQuery("SELECT * FROM checkpoint WHERE Board = '" + response.getString("originBoard") + "';");
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
			}


			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(
				board,
				HttpStatus.OK
		);
	}
}
