package ispyb.loggin.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		assertTrue(Files.readAllLines(getFileResource(fileName)).size() > 0);
	}

	@Test
	public void parseFile() throws IOException {
		LogFile logFile = new LogFile(this.getFileResource("server.log").toFile().getAbsolutePath());
		logFile.parse("BIOSAXS_UI");
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