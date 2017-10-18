package com.github.wkigen.gaea;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by vicky on 2017/10/18.
 */
public class BundleLoader {

    static final String TAG = BundleLoader.class.getSimpleName();

    public static void installBundleDex(ClassLoader loader,File dexDir, List<File> files) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException,InstantiationException, InvocationTargetException, NoSuchMethodException, IOException {
        if (files != null){
            if (Build.VERSION.SDK_INT >= 23) {
                V23.install(loader, files, dexDir);
            }else if (Build.VERSION.SDK_INT >= 19) {
                V19.install(loader, files, dexDir);
            } else if (Build.VERSION.SDK_INT >= 14) {
                V14.install(loader, files, dexDir);
            }
        }
    }

    private static final class V23{
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {

            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            Field dexElement = findField(dexPathList, "dexElements");
            Class<?> elementType = dexElement.getType().getComponentType();
            Method loadDex = findMethod(dexPathList, "loadDexFile", File.class, File.class);
            Object dex = loadDex.invoke(dexPathList, additionalClassPathEntries.get(0), optimizedDirectory);
            Constructor<?> constructor = elementType.getConstructor(File.class, boolean.class, File.class, DexFile.class);
            Object element = constructor.newInstance(new File(""), false, additionalClassPathEntries.get(0), dex);
            Object[] newEles=new Object[1];
            newEles[0]=element;
            expandFieldArray(dexPathList, "dexElements", newEles);
        }
    }

    private static final class V19{
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {

            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);

                }
                throw suppressedExceptions.get(0);
            }
        }
    }

    private static final class V14{
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {

            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory));

        }
    }


    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {

                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters " +
                Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }

    private static Object[] makeDexElements(
            Object dexPathList, ArrayList<File> files, File optimizedDirectory)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Method makeDexElements =
                findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class);

        return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
    }

    private static Object[] makeDexElements(
            Object dexPathList, ArrayList<File> files, File optimizedDirectory,
            ArrayList<IOException> suppressedExceptions)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Method makeDexElements =
                findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class,
                        ArrayList.class);

        return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory,
                suppressedExceptions);
    }

    private static void expandFieldArray(Object instance, String fieldName,
                                        Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {

        Field jlrField = findField(instance, fieldName);
        Object[] original = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(
                original.getClass().getComponentType(), original.length + extraElements.length);

        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(extraElements, 0, combined, original.length, extraElements.length);

        jlrField.set(instance, combined);
    }


}
