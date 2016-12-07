package StandardMBeans;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Ronnie on 12/3/2016.
 */
public class DateTimeServices implements DateTimeServicesMBean{

    public void setDate(String newDate) {

    }

    public String getDate(){
        Calendar rightNow  = Calendar.getInstance();
        return DateFormat.getDateInstance().format(rightNow.getTime());
    }

    public void setTime(String newTime) {

    }

    public String getTime(){
        Calendar rightNow  = Calendar.getInstance();
        return DateFormat.getTimeInstance().format(rightNow.getTime());
    }

    public void start() {

    }

    public void stop() {

    }
}
