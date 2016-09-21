package ispyb.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class LogFile extends File {

	/**
	 * This stored the message ID and the message then the calculation of the
	 * duration is done based on the message ID
	 */
	HashMap<String, LogMessage> messages = new HashMap<String, LogMessage>();

	public LogFile(String arg0) {
		super(arg0);
	}
	
	public LogFile(File file) {
		super(file.getAbsolutePath());
	}

	public void writeToFile(String outputFilePath, String packageName) throws IOException {
		ArrayList<String> packages = new ArrayList<String>();
		packages.add(packageName);
		this.writeToFile(outputFilePath, packages);
	}
	
	public void writeToFile(String outputFilePath, List<String> packagesName) throws IOException {
		HashMap<String, LogMessage> map = new HashMap<String, LogMessage>();
		for (String packageName : packagesName) {
			map.putAll(this.parse(packageName));
		}
		this.writeToFile(outputFilePath, map);
	}
	
	
	private void writeToFile(String outputFilePath, HashMap<String, LogMessage> map) throws IOException {
		/** Writes result to file **/
		Path path = Paths.get(outputFilePath);
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE )) {
			writer.write(new Gson().toJson(this.rename(map)));
		}
		
	}
	
	/**
	 * Optimize the naming of the attribute to reduce space
	 * @param maps
	 * @return
	 */
	private HashMap<String, LogMessage> rename(HashMap<String, LogMessage> maps){
		List<String> oldKeys = Arrays.asList("DAY_OF_MONTH", "MONTH", "YEAR", "HOUR_OF_DAY", "MINUTE");
		List<String> newKeys =  Arrays.asList("D", "M", "Y", "H", "MI" );
		for (String key : maps.keySet()) {
			LogMessage map = maps.get(key);
			for (int i = 0; i < oldKeys.size(); i++) {
				Object obj = map.remove(oldKeys.get(i));
				map.put(newKeys.get(i), obj);
			}
		}
		return maps;
	}


	/**
	 * Sets the duration of the message
	 * 
	 * @param message
	 */
	private void process(LogMessage message) {
		if (message.get("TYPE").equals("START")) {
			this.messages.put(message.get("ID").toString(), message);
		}
		if (message.get("TYPE").equals("END")) {
			if (this.messages.get(message.get("ID")) != null) {
				this.messages.get(message.get("ID")).setDate(message.get("DATE").toString());
				this.messages.get(message.get("ID")).put("DURATION", message.get("DURATION").toString());
			}
		}
		if (message.get("TYPE").equals("ERROR")) {
			message.setDate(message.get("DATE").toString());
			this.messages.put(message.get("ID").toString(), message);
		}
	}

	/**
	 * Checks if the line contains the word of the name of a package contained on packagesName
	 * @param line
	 * @param packagesName
	 * @return
	 */
	private Boolean isPackage(String line, List<String> packagesName) {
		for (String packageName : packagesName) {
			if (line.contains(packageName)){
				return true;
			}
		}
		return false;
	}
	private LogMessage parseLine(String line, List<String> packagesName) {
		if (this.isPackage(line, packagesName)) {
			try {
				String parsed = line.substring(line.indexOf("{"), line.lastIndexOf("}") + 1);
				/** Check GSON is well parsed **/

				LogMessage message = new Gson().fromJson(parsed, LogMessage.class);
				if (message.get("PARAMS") != null) {
					if (!message.get("PARAMS").toString().isEmpty()) {
						/** Trying to convert params to JSON **/
						try {
							JSONObject jsonObj = new JSONObject(message.get("PARAMS").toString());
							Type type = new TypeToken<HashMap<String, String>>() {
							}.getType();
							message.put("PARAMS", new Gson().fromJson(message.get("PARAMS").toString(), type));
						} catch (Exception e) {
							/** Params is not a JSON **/
							message.put("PARAMS", message.get("PARAMS").toString());
						}
					}
				}
				return message;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public  HashMap<String, LogMessage> parse(List<String> packages) {
		try (BufferedReader br = new BufferedReader(new FileReader(this))) {
			String line;
			while ((line = br.readLine()) != null) {
				LogMessage message = this.parseLine(line, packages);
				if (message != null) {
					this.process(message);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.messages;
		
	}

	/**
	 * This will read each line of the file doing the next actions: 1) Parse
	 * Line 2) Process Line 3) Return results
	 * 
	 * @param packageName
	 * @return
	 */
	public HashMap<String, LogMessage> parse(String packageName) {
		return this.parse(Arrays.asList(packageName));
	}

	public static void writeResultToFile(String absolutePath, HashMap<String, LogMessage> processed) {
		LogFile log = new LogFile(absolutePath);
		try {
			log.writeToFile(absolutePath, processed);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	
}
