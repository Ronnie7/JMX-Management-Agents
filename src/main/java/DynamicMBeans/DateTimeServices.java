package DynamicMBeans;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Ronnie on 12/3/2016.
 */
public class DateTimeServices implements DateTimeServicesMBean {

    public static final boolean READABLE = true;
    public static final boolean WRITABLE = true;
    public static final boolean ISIS = true;

    private String userConfiguredDate = null;
    private String userConfiguredTime = null;
    private MBeanAttributeInfo[] attributeInfos = new MBeanAttributeInfo[2];
    private MBeanConstructorInfo[] constructorInfos = new MBeanConstructorInfo[1];
    private MBeanOperationInfo[] operationInfos = new MBeanOperationInfo[2];
    private MBeanInfo mBeanInfo = null;
    private MBeanNotificationInfo[] notificationInfos = null;
    private NotificationBroadcasterSupport broadcasterSupport = new NotificationBroadcasterSupport();
    private long notificationSequence = 0;

    public void setDate(String newDate) {
        String oldValue = getDate();
        String attrType = String.class.getName();
        String attrName = "Date";

        userConfiguredDate = newDate;
        AttributeChangeNotification notif = new AttributeChangeNotification(this, ++notificationSequence, System.currentTimeMillis(), "date has been changed.", attrName, attrType, oldValue, newDate);
        broadcasterSupport.sendNotification(notif);
    }

    public String getDate() {
        if (userConfiguredDate != null)
            return userConfiguredDate;
        Calendar rightNow = Calendar.getInstance();
        return DateFormat.getDateInstance().format(rightNow.getTime());
    }

    public void setTime(String newTime) {
        String oldValue = getTime();
        String attrType = String.class.getName();
        String attrName = "Time";
        userConfiguredTime = newTime;

        AttributeChangeNotification notif = new AttributeChangeNotification(this, ++notificationSequence,
                System.currentTimeMillis(), "Time has been changed.",
                attrName,
                attrType,
                oldValue,
                newTime);
    }

    public String getTime() {
        if (userConfiguredTime != null)
            return userConfiguredTime;
        Calendar rightNow = Calendar.getInstance();
        return DateFormat.getTimeInstance().format(rightNow.getTime());
    }

    public void start() {
        Notification notification = new Notification("services.datetime.start",
                this,
                ++notificationSequence,
                "DateTime service start.");
        broadcasterSupport.sendNotification(notification);
    }

    public void stop() {
        Notification notification = new Notification(
                "services.datetime.stop",
                this,
                ++notificationSequence,
                "DateTime service stopped.");
        broadcasterSupport.sendNotification(notification);
    }

    public Object getAttribute(String attributeName) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attributeName == null) {
            IllegalArgumentException ex = new IllegalArgumentException("Attribute name cannot be null.");
            throw new RuntimeOperationsException(ex, "null attribute name");
        }
        if (attributeName.equals("Date")) {
            return getDate();
        }
        if (attributeName.equals("Time")) {
            return getTime();
        }
        throw (new AttributeNotFoundException("Invalid Attribute: " + attributeName));
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (attribute == null) {
            IllegalArgumentException ex = new IllegalArgumentException("Attribute cannot be null");
            throw new RuntimeOperationsException(ex, "null attribute");
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            IllegalArgumentException ex = new IllegalArgumentException("Attribute name cannot be null");
            throw new RuntimeOperationsException(ex, "null attribute name");
        }
        if (value == null) {
            IllegalArgumentException ex = new IllegalArgumentException("Attribute value cannot be null");
            throw new RuntimeOperationsException(ex, "null attribute value");
        }
        try {
            Class stringCls = Class.forName("java.lang.string");
            if (stringCls.isAssignableFrom(value.getClass()) == false) {
                IllegalArgumentException ex = new IllegalArgumentException("Invalid attribute value class");
                throw new RuntimeOperationsException(ex, "Invalid attribute value");
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if (name.equals("Date")) {
            setDate(value.toString());
        } else if (name.equals("Time")) {
            setTime(value.toString());
        } else {
            throw (new AttributeNotFoundException("Invalid Attribute name; " + name));
        }

    }

    public AttributeList getAttributes(String[] attributesNames) {
        if (attributesNames == null) {
            IllegalArgumentException ex = new IllegalArgumentException("AttributesNames cannot be null");
            throw new RuntimeOperationsException(ex, "null attributesnames");
        }
        AttributeList resultList = new AttributeList();
        if (attributesNames.length == 0)
            return resultList;
        for (String attributesName : attributesNames) {
            try {
                Object value = getAttribute(attributesName);
                resultList.add(value);
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {

                e.printStackTrace();
            } catch (MBeanException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public AttributeList setAttributes(AttributeList attributes) {
        if (attributes == null) {
            IllegalArgumentException ex = new IllegalArgumentException("Attributes cannot be null");
            throw new RuntimeOperationsException(ex, "null attributes list");
        }
        AttributeList resultList = new AttributeList();
        if (attributes.isEmpty())
            return resultList;

        for (Object attribute : attributes) {
            Attribute attr = (Attribute) attribute;
            try {
                setAttribute(attr);
                String name = attr.getName();
                Object value = getAttribute(name);
                resultList.add(new Attribute(name, value));
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidAttributeValueException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            } catch (MBeanException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /*
    * The invoke method enables an operation to be invoked on you dynamic MBeans.
    * The required parameters are:
    * 1.An Operation name that represents the name of the operation to be invoked.
    * 2.An Array of objects containing the parameter values to passed to the operation to be invoked
    * 3.An array of Strings containing the class names representing the parameter types of the operation that is invoked.
    * The return value, if applicable, is a generic Object.
    * */
    public Object invoke(String operationName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        if (operationName == null) {
            IllegalArgumentException ex = new IllegalArgumentException("operationName cannot be null");
            throw new RuntimeOperationsException(ex, "null operation Name");
        }

        if (operationName.equals("stop")) {
            stop();
            return null;
        } else if (operationName.equals("start")) {
            start();
            return null;
        } else {
            throw new ReflectionException(new NoSuchMethodException(operationName),
                    "Invalid operation name"
                            + operationName);
        }
    }

    public MBeanInfo getMBeanInfo() {
        if (mBeanInfo != null)
            return mBeanInfo;
        attributeInfos[0] = new MBeanAttributeInfo("Date",
                String.class.getName(),
                "The Current Date",
                READABLE, WRITABLE, !ISIS);
        attributeInfos[1] = new MBeanAttributeInfo("Time",
                String.class.getName(),
                "The Current time",
                READABLE, WRITABLE, !ISIS);
        Constructor[] constructors = this.getClass().getConstructors();
        constructorInfos[0] = new MBeanConstructorInfo("Construct a date time object.", constructors[0]);

        MBeanParameterInfo[] mBeanParameterInfo = null;
        operationInfos[0] = new MBeanOperationInfo("start", "Starts the DateTime service", mBeanParameterInfo, "void", MBeanOperationInfo.ACTION);
        operationInfos[1] = new MBeanOperationInfo("stop", "Stops the DateTime service", mBeanParameterInfo, "void", MBeanOperationInfo.ACTION);

        mBeanInfo = new MBeanInfo(
                this.getClass().getName(),
                "DateTime service MBean",
                attributeInfos,
                constructorInfos,
                operationInfos,
                getNotificationInfo()
        );

        return mBeanInfo;
    }

    private MBeanNotificationInfo[] getNotificationInfo() {
        if (notificationInfos != null)
            return notificationInfos;

        notificationInfos = new MBeanNotificationInfo[]{
                new MBeanNotificationInfo(new String[]{"service.user.start"},
                        Notification.class.getName(),
                        "DateTime service start."
                ),
                new MBeanNotificationInfo(new String[]{"service.user.stop"},
                        Notification.class.getName(),
                        "DateTime service stop."
                ),
                new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                        AttributeChangeNotification.class.getName(),
                        "DateTime service attribute changes."
                )
        };
        return notificationInfos;
    }

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
        broadcasterSupport.addNotificationListener(listener, filter, handback);
    }

    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        broadcasterSupport.removeNotificationListener(listener);
    }

    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException {
        broadcasterSupport.removeNotificationListener(listener, filter, handback);
    }

}
