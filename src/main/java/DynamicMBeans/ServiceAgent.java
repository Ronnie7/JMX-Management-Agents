package DynamicMBeans;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import javax.management.monitor.CounterMonitor;
import javax.management.monitor.Monitor;
import javax.management.monitor.MonitorNotification;
import javax.management.timer.Timer;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Ronnie on 12/3/2016.
 */
public class ServiceAgent {

    static String counterMonitorClass = "javax.management.monitor.CounterMonitor";
    private MBeanServer mBeanServer = null;
    private ObjectName counterMonitorName = null;
    private ObjectName timerOName = null;
    private CounterMonitor counterMonitor = null;

    public ServiceAgent() {
        try {
            mBeanServer = MBeanServerFactory.createMBeanServer();
            initializeCounterMonitor();

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

    private void initializeCounterMonitor() {
        ObjectName counterMonitorName = null;
        counterMonitor = new CounterMonitor();
        //Get the Domain name from MBean server
        String domain = mBeanServer.getDefaultDomain();
        //create a new counter Mbean and add it to the MBean server..
        try {
            counterMonitorName = new ObjectName(domain + ":name=" + counterMonitorClass);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            return;
        }
        try {
            mBeanServer.registerMBean(counterMonitor, counterMonitorName);
        } catch (Exception e) {

        }
    }


    public void setCounterMonitorListener(ObjectName observedObjName, NotificationListener listener, String attrName) {
        //Register a notification listener with the CounterMonitor Mbean, enabling the listener to receive notification transmitted by counter monitor
        try {
            Integer threshold = new Integer(1);
            Integer offset = new Integer(1);
            counterMonitor.setObservedObject(observedObjName);
            counterMonitor.setObservedAttribute(attrName);
            counterMonitor.setNotify(true);
            counterMonitor.setOffset(offset);
            counterMonitor.setGranularityPeriod(1000);


            NotificationFilter notificationFilter = null;

            Object handback = null;
            counterMonitor.addNotificationListener(listener, notificationFilter, handback);

            if (counterMonitor.isActive() == false)
                counterMonitor.start();
        } catch (Exception e) {
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

    public void handleNotification(Notification notification, Object handback) {
        if (notification instanceof MonitorNotification) {
            MonitorNotification notif = (MonitorNotification) notification;
            /*get Monitor responsible for the notification*/
            Monitor monitor = (Monitor) notif.getSource();
            /*Test the notification types transmitted by the monitor*/
            String t = notif.getType();
            Object observedObj = notif.getObservedObject();
            String observedAttr = notif.getObservedAttribute();

            try {
                if (t.equals(MonitorNotification.OBSERVED_OBJECT_ERROR)) {
                    System.out.println(observedObj.getClass().getName() + " is not registered with the server");
                } else if (t.equals(MonitorNotification.OBSERVED_ATTRIBUTE_ERROR)) {
                    System.out.println(observedAttr + " is not contained in " + observedObj.getClass().getName());
                } else if (t.equals(MonitorNotification.OBSERVED_ATTRIBUTE_TYPE_ERROR)) {
                    System.out.println(observedAttr + "type is not correct. ");
                } else if (t.equals(MonitorNotification.THRESHOLD_ERROR)) {
                    System.out.println("THRESHOLD type is not correct. ");
                } else if (t.equals(MonitorNotification.RUNTIME_ERROR)) {
                    System.out.println("unknown runtime error");
                } else if (t.equals(MonitorNotification.THRESHOLD_VALUE_EXCEEDED)) {
                    System.out.println(observedAttr + "has reached the threshold\n");
                } else {
                    System.out.println("unknown event type");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startTimer() throws ServiceAgentException {
        if (timerOName == null) {
            try {
                timerOName = new ObjectName("TimerServices:name=SimplerTimer");
                mBeanServer.registerMBean(new Timer(), timerOName);
                mBeanServer.invoke(timerOName, "start", null, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (((Boolean) mBeanServer.invoke(timerOName, "isActive", null, null)).booleanValue() == false) {
                    mBeanServer.invoke(timerOName, "start", null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void stopTimer() throws ServiceAgentException {
        try {
            if (((Boolean) mBeanServer.invoke(timerOName, "isActive", null, null)).booleanValue() == false) {
                mBeanServer.invoke(timerOName, "stop", null, null);
            }
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
    }

    /*
    * this method facilitate timer to add and remove notifications
    * */
    public Integer addTimerNotification(String type, String message, Object userData, java.util.Date startDate, long period, long occurrences)
            throws ServiceAgentException {
        Object[] param = new Object[]{
                type,
                message,
                userData,
                startDate,
                new Long(period),
                new Long(occurrences)
        };
        String[] signature = new String[]{
                String.class.getName(),
                String.class.getName(),
                Object.class.getName(),
                Date.class.getName(),
                long.class.getName(),
                long.class.getName()
        };
        Object retVal = null;
        try {
            retVal = mBeanServer.invoke(timerOName, "addNotification", param, signature);

        } catch (Exception e) {
            System.out.println("Error adding notification " + e.toString());
        }
        return (Integer) retVal;
    }

    public void removeTimerNotification(Integer id) throws ServiceAgentException {
        Object[] param = new Object[]{
                id
        };
        String[] signature = new String[]{
                Integer.class.getName()
        };
        try {
            mBeanServer.invoke(timerOName, "removeNotification", param, signature);
        } catch (Exception e) {
            System.out.println("Error removing notification " + e.toString());
        }
    }

    public void addTimerListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ServiceAgentException {
        try {
            mBeanServer.addNotificationListener(timerOName, listener, filter, handback);
        } catch (Exception e) {
            System.out.println("error adding timer listener" + e.toString());
        }
    }

    public void removeTimerListener(NotificationListener listener) throws ServiceAgentException {
        try {
            mBeanServer.removeNotificationListener(timerOName, listener);
        } catch (Exception e) {
            System.out.println("error removing timer listener" + e.toString());
        }
    }




    public static void main(String[] args) {

        ServiceAgent serviceAgent = new ServiceAgent();

        //ObjectInstance objInst = serviceAgent.addResource("DateTime", properties,mbeanClassName);
       /* serviceAgent.setCounterMonitorListener(objInst.getObjectName(),
                this,
                "Second");*/
    }
}
