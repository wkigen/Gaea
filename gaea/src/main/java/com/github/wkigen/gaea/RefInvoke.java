package com.github.wkigen.gaea;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vicky on 2017/10/18.
 */
public class RefInvoke {

    public static  Object invokeStaticMethod(String className, String methodName, Class[] types, Object[] vaules)
            throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException {
        try {
            Class obj_class = Class.forName(className);
            Method method = obj_class.getMethod(methodName,types);
            return method.invoke(null, vaules);
        }
        catch (Exception e) {
            throw new NoSuchMethodException("StaticMethod " + methodName + " not found in " + className);
        }
    }

}
