package DynamicMBeans;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

/**
 * Created by Ronnie on 12/4/2016.
 * A Dynamic MBean exposes it's management information by returning the standard metadata object from methods defined by jmx.management.Dynamic interface.
 * Dynamic bean provides their actual management information at runtime that's why they are more flexible than standard beans.
 */
public interface DateTimeServicesMBean {
    Object getAttribute(String s) throws AttributeNotFoundException, MBeanException, ReflectionException;

    void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;

    AttributeList getAttributes(String[] strings);

    AttributeList setAttributes(AttributeList list);
    /*
    * invoke method can be used to call it's operation.
    * */
    Object invoke(String s, Object[] objects, String[] strings) throws MBeanException, ReflectionException;

    /*
    * getMBeanInfo function will return it's Metadata
    * */
    MBeanInfo getMBeanInfo();
}
