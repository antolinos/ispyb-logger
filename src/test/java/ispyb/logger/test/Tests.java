package ispyb.logger.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

import com.google.gson.Gson;

import ispyb.logger.LogFile;
import ispyb.logger.LogMessage;

public class Tests {

	private Path getFileResource(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		return Paths.get(new File(classLoader.getResource(fileName).getFile()).getAbsolutePath());
	}

	private String getResourceFolder() {
		return this.getFileResource("server.log").getParent().toFile().getAbsolutePath();
	}

	@Test
	public void readFiles() throws IOException {
		String fileName = "server.log";
		assertTrue(Files.readAllLines(getFileResource(fileName), Charset.defaultCharset()).size() > 0);
	}

	@Test
	public void parseFiles() throws IOException {
		File resourceFolder= new File(this.getResourceFolder());
		for (File file : resourceFolder.listFiles()) {
			try{
				if (file.isFile()){
					LogFile logFile = new LogFile(file);
					String outputFile = this.getResourceFolder() + "/" + file.getName() + ".json";
					logFile.writeToFile(outputFile,
							Arrays.asList(
											"BIOSAXS_WS", 
										    "BIOSAXS_WS_ERROR", 
										    "BIOSAXS_UI", 
										    "BIOSAXS_UI_ERROR", 
										    "BIOSAXS_DB", 
										    "BIOSAXS_MOBILE", 
										    "CRIMS_WS_ERROR", 
										    "CRIMS_WS",
										    "BIOSAXS_WORKFLOW_ERROR", 
										    "BIOSAXS_WORKFLOW", 
										    "ISPyB_API_ERROR",
										    "ISPyB_API")
										    );
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}


	@Test
	public void parseJSON() {
		try {
			String test = "{'PARAMS':{'shippingId':'307351'}, 'PACKAGE':'CRIMS_WS','COMMENTS':'','DATE':'Fri Sep 16 00:00:01 CEST 2016','ID':'1473976801235','METHOD':'getDataCollectionFromShippingId','DURATION':'-1','TYPE':'START','LOGGER':'v2.0'}";
			LogMessage log = new Gson().fromJson(test, LogMessage.class);
			assertTrue(log.get("PACKAGE").equals("CRIMS_WS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}