package StandardMBeans;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ronnie on 12/3/2016.a
 */
public class ServiceAgent {

    public static void main(String[] args) {
        ServiceAgent serviceAgent = new ServiceAgent();
        serviceAgent.runAgent(new DateTimeServices());
    }

    public void runAgent(Object serviceName) {
        MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();
        try {
            ObjectName objectName = new ObjectName("service:name=DateTime,type=information");
            mBeanServer.registerMBean(serviceName, objectName);
            ObjectName adaptorOName = new ObjectName("Adaptors:protocol=HTTP");
            HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
            mBeanServer.registerMBean(htmlAdaptorServer, adaptorOName);
            System.out.println("JMX server http://localhost:8082 starting...");
            htmlAdaptorServer.start();
            System.out.println("JMX server http://localhost:8082 Running...");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String shutDown = bufferedReader.readLine();
            if (shutDown.contains("shutdown")) {
                System.out.println("Shutting down JMX server http://localhost:8082");
                htmlAdaptorServer.stop();
                System.out.println("Server ShutDown");

            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
