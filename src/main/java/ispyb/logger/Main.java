package ispyb.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.Gson;

public class Main {

	private static List<String> packages = Arrays.asList("BIOSAXS_WS", "BIOSAXS_WS_ERROR", 
//			"BIOSAXS_UI",
			"BIOSAXS_UI_ERROR", "BIOSAXS_DB",
			"BIOSAXS_MOBILE", "CRIMS_WS_ERROR", "CRIMS_WS", "BIOSAXS_WORKFLOW_ERROR", "BIOSAXS_WORKFLOW", "ISPyB_API_ERROR", "ISPyB_API");

	public static String getSize(File file) {
		if (file.exists()) {
			double bytes = file.length();
			double kilobytes = (bytes / 1024);
			double megabytes = (kilobytes / 1024);
			return "[" + new DecimalFormat("#.#").format(megabytes) + "MB]";
		} else {
			return "File does not exists!";
		}
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		Option input = new Option("i", "input", true, "input file or folder where jboss logs file are");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file or folder");
		output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

		long startTime = System.nanoTime();

		String inputFilePath = cmd.getOptionValue("input");
		String outputFilePath = cmd.getOptionValue("output");

		HashMap<String, LogMessage> processed = new HashMap<String, LogMessage>();

		File inputFile = new File(inputFilePath);
		File outputFile = new File(outputFilePath);

		if (outputFile.isDirectory()){
			System.out.println("outputFile " + outputFile.getAbsolutePath() + " is folder ");
		}
		else{
			System.out.println("outputFile " + outputFile.getAbsolutePath() + " is file ");
		}
		
		
		if (inputFile.isDirectory()) {
			/** Read all files **/
			File resourceFolder = new File(inputFilePath);
			for (File file : resourceFolder.listFiles()) {
				try {
					LogFile log = new LogFile(file);
					
					HashMap<String, LogMessage> processedData = log.parse(Main.packages);
					System.out.println("Found " + processedData.size() + " annotations on " + log.getAbsolutePath());
					/**
					 * Storing everything in the same hashmap because all output
					 * goes to the same file
					 **/
					if (outputFile.isFile()) {
						processed.putAll(processedData);
					}
					if (outputFile.isDirectory()) {
						File outputFilePathFile = new File(outputFile.getAbsolutePath() + "/" + file.getName()+ ".json");
						LogFile.writeResultToFile(outputFilePathFile.getAbsolutePath(), processedData);
						System.out.println("Writting to " + outputFilePathFile.getAbsolutePath() + getSize(outputFilePathFile));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/** If output is file we add everything to the same file **/
			if (outputFile.isFile()) {
				File outputFilePathFile = new File(outputFile.getAbsolutePath() + "/" + inputFile.getName()+ ".json");
				LogFile.writeResultToFile(outputFilePathFile.getAbsolutePath(), processed);
				System.out.println("Found " + processed.size() + " annotations");
				System.out.println("Writting to " + outputFilePathFile.getAbsolutePath() + getSize(outputFilePathFile));
			} 
				
			

		} else {
			/** Process one file **/
			File resourceFile = new File(inputFilePath);
			LogFile logFile = new LogFile(resourceFile);
			if (outputFile.isDirectory()) {
				File outputFilePathResult = new File(outputFile.getAbsolutePath() + "/" + inputFile.getName() + ".json");
				logFile.writeToFile(outputFilePathResult.getAbsolutePath(), packages);
				System.out.println("Input file" + inputFile.getAbsolutePath() + getSize(inputFile));
				System.out.println("Output file created on " + outputFilePathResult + getSize(outputFilePathResult));
			} else {
				logFile.writeToFile(outputFile.getAbsolutePath(), packages);
				System.out.println("Input file" + inputFile.getAbsolutePath() + getSize(inputFile));
				System.out.println("Output file created on " + outputFile.getAbsolutePath() + getSize(outputFile));
			}
		}

		long endTime = System.nanoTime();
		long duration = (endTime - startTime);

		System.out.println("Done in " + duration / 1000000 + " ms");

		/** Creating index.json **/
		if (outputFile.isDirectory()){
			File[] files = outputFile.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getAbsolutePath().endsWith("json"));
					
				}
			});
			
			
			Path path = Paths.get(outputFilePath + "/index.json");
			
			try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE )) {
				List<String> indeces = new ArrayList<String>();
				for (File jsonFile : files) {
					indeces.add(jsonFile.getName());
				}
				Arrays.sort(indeces.toArray());
				writer.write(new Gson().toJson(indeces));
			}
			System.out.println("Index created on " + path.toFile().getAbsolutePath());
			
		}
	}

}