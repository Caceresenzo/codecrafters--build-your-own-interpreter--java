package interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: ./your_program.sh tokenize <filename>");
			System.exit(1);
		}

		String command = args[0];
		String filePath = args[1];

		if (!command.equals("tokenize")) {
			System.err.println("Unknown command: " + command);
			System.exit(1);
		}

		String fileContents = "";
		try {
			fileContents = Files.readString(Path.of(filePath));
		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
			System.exit(1);
		}

		if (fileContents.length() > 0) {
			throw new RuntimeException("Scanner not implemented");
		} else {
			System.out.println("EOF  null");
		}
	}

}