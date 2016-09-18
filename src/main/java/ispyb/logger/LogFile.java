package ispyb.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class LogFile extends File {

	/**	
	 * This stored the message ID and the message then the calculation of the duration is done based on the message ID
	 */
	HashMap<String, LogMessage> messages  = new HashMap<String, LogMessage>();
	
	public LogFile(String arg0) {
		super(arg0);
	}

	/**
	 * This will read each line of the file doing the next actions:
	 * 1) Parse Line
	 * 2) Process Line
	 * 3) Return results
	 * @param packageName
	 * @return
	 */
	public HashMap<String, LogMessage> parse(String packageName){
		try (BufferedReader br = new BufferedReader(new FileReader(this))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	LogMessage message = this.parseLine(line, packageName);
		    	if (message != null){
		    		this.process(message);
		    	}
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/** Print report **/
		for (String id : this.messages.keySet()){
			System.out.println(this.messages.get(id));
			
			
		}
		return this.messages;
	}
	
	
	/**
	 * Sets the duration of the message
	 * @param message
	 */
	private void process(LogMessage message) {
		if (message.get("TYPE").equals("START")){
			this.messages.put(message.get("ID").toString(), message);
		}
		if (message.get("TYPE").equals("END")){
			if (this.messages.get(message.get("ID")) != null){
				this.messages.get(message.get("ID")).setDate(message.get("DATE").toString());
				this.messages.get(message.get("ID")).put("DURATION", message.get("DURATION").toString());
				
			}
		}
	}

	private LogMessage parseLine(String line, String packageName){
		if (line.contains(packageName)){
			try{
				String parsed = line.substring(line.indexOf("{"), line.indexOf("}") + 1);
//				System.out.println(parsed);
				return new Gson().fromJson(parsed, LogMessage.class);
			}
			catch(Exception e){
//				e.printStackTrace();
			}
		}
		return null;
		
	}
//	private static void writeResult(String outputFilePath, String result, String packageName)
//			throws FileNotFoundException {
//		File file = new File(outputFilePath);
//		log(String.format("Writing output to: %s", outputFilePath));
//		PrintWriter writer = new PrintWriter(file);
//		try {
//			writer = new PrintWriter(file);
//			writer.print("function " + packageName + "(){\n return " + result + ";\n }");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			writer.close();
//
//		}
//
//	}

}
