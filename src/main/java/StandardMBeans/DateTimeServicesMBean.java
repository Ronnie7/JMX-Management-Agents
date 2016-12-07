package StandardMBeans;

/**
 * Created by Ronnie on 12/3/2016.
 */
public interface DateTimeServicesMBean {
    void setDate(String newDate);

    String getDate();

    void setTime(String newTime);

    String getTime();

    void start();

    void stop();
}
