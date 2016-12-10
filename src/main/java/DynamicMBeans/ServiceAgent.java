package DynamicMBeans;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Ronnie on 12/3/2016.
 */
public class ServiceAgent {

    private MBeanServer mBeanServer = null;

    public ServiceAgent() {
        try {
            mBeanServer = MBeanServerFactory.createMBeanServer();
            ObjectName adaptorOName = new ObjectName("adaptors:protocol=HTTP");

            HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
            mBeanServer.registerMBean(htmlAdaptorServer, adaptorOName);

            htmlAdaptorServer.start();

        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    public void addResource(String name, Hashtable properties, String className) throws ServiceAgentException {
        try {
            Class cls = Class.forName(className);
            Object obj = cls.newInstance();
            Hashtable allProps = new Hashtable();
            allProps.put("name", name);
            properties.putAll(allProps);
            ObjectName oName = new ObjectName("services", allProps);
            mBeanServer.registerMBean(obj, oName);

        } catch (IllegalArgumentException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }

    public Set getResources(String name) throws ServiceAgentException {
        try {
            Hashtable allProps = new Hashtable();
            allProps.put("name", name);
            ObjectName oName = new ObjectName("services", allProps);
            Set resultSet = mBeanServer.queryMBeans(oName, null);

            return resultSet;
        } catch (MalformedObjectNameException e) {
            throw new ServiceAgentException("Invalid Object  Name " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServiceAgent serviceAgent = new ServiceAgent();
    }
}
