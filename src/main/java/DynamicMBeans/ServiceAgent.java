package DynamicMBeans;

/**
 * Created by Ronnie on 12/3/2016.
 */
public class ServiceAgent {
    public static void main(String[] args) {
        StandardMBeans.ServiceAgent serviceAgent = new StandardMBeans.ServiceAgent();
        serviceAgent.runAgent(new DateTimeServices());
    }
}
