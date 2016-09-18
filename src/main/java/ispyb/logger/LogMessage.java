package ispyb.logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressWarnings("serial")
public class LogMessage extends HashMap<String, Object> {

	public LogMessage() {
		super();
	}

	/**
	 * Format : Fri Sep 16 01:47:22 CEST 2016
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		if (date != null) {
			DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
			Date myDate;
			try {
				myDate = (Date) formatter.parse(date);
				Calendar cal = Calendar.getInstance();
				cal.setTime(myDate);
				
			
				this.put("DAY_OF_MONTH", cal.get(Calendar.DAY_OF_MONTH));
				this.put("MONTH", cal.get(Calendar.MONTH));
				this.put("YEAR", cal.get(Calendar.YEAR));
				this.put("HOUR_OF_DAY", cal.get(Calendar.HOUR_OF_DAY));
				this.put("MINUTE", cal.get(Calendar.MINUTE));
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

	}

}
