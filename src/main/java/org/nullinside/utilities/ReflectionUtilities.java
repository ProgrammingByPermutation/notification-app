package org.nullinside.utilities;

import javafx.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utilities for performing common operations using reflection.
 */
public final class ReflectionUtilities {
    /**
     * Recursively finds the field in a class object.
     * <p>
     * The reason we have to do this is because the way Java reflection works
     * is not great. You have to ask for a field in the class you're currently
     * working on but the fields aren't searched for any parent classes. So if A
     * inherits from B, you cannot just get the B fields from an A object by looking
     * specifically in the A class for them. You have to find all of the parent classes
     * of A one at a time and look for B's fields.
     *
     * @param klass The class to search for the property in.
     * @param name  The name of the property.
     * @return The Field object associated with the name, null if not found.
     */
    public static Field getField(Class<?> klass, String name) {
        Field field;
        do {
            try {
                field = klass.getDeclaredField(name);
                return field;
            } catch (NoSuchFieldException e) {
                // Do nothing
            }

            klass = klass.getSuperclass();
        } while (klass != null);

        return null;
    }

    /**
     * Recursively finds the field in a class object or the child of a class object using "parent.child.field" syntax.
     * <p>
     * The reason we have to do this is because the way Java reflection works
     * is not great. You have to ask for a field in the class you're currently
     * working on but the fields aren't searched for any parent classes. So if A
     * inherits from B, you cannot just get the B fields from an A object by looking
     * specifically in the A class for them. You have to find all of the parent classes
     * of A one at a time and look for B's fields.
     *
     * @param klass The class to search for the property in.
     * @param name  The name of the property.
     * @param instance The instance of the class to search through.
     * @return The Field object associated with the name and the object it acts on, null if not found.
     */
    public static Pair<Object, Field> getFieldNested(Class<?> klass, String name, Object instance) {
        var searchFor = name;
        String next = null;
        if (name.contains(".")) {
            var index = name.indexOf('.');
            searchFor = name.substring(0, index);
            next = name.substring(index + 1);
        }

        Field field;
        do {
            try {
                field = klass.getDeclaredField(searchFor);
                if (null != next) {
                    field.setAccessible(true);
                    return getFieldNested(field.getType(), next, field.get(instance));
                }

                return new Pair<>(instance, field);
            } catch (NoSuchFieldException e) {
                // Do nothing
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                break;
            }

            klass = klass.getSuperclass();
        } while (klass != null);

        return null;
    }

    /**
     * Recursively finds the method in a class object.
     * <p>
     * The reason we have to do this is because the way Java reflection works
     * is not great. You have to ask for a method in the class you're currently
     * working on but the methods aren't searched for any parent classes. So if A
     * inherits from B, you cannot just get the B methods from an A object by looking
     * specifically in the A class for them. You have to find all of the parent classes
     * of A one at a time and look for B's methods.
     *
     * @param klass The class to search for the method in.
     * @param name  The name of the method.
     * @return The Method object associated with the name, null if not found.
     */
    public static Method getMethod(Class<?> klass, String name) {
        Method method;
        do {
            try {
                method = klass.getMethod(name);
                return method;
            } catch (NoSuchMethodException e) {
                // Do nothing
            }

            klass = klass.getSuperclass();
        } while (klass != null);

        return null;
    }

    /**
     * Recursively finds the method in a class object or the child of a class object using "parent.child.method" syntax.
     * <p>
     * The reason we have to do this is because the way Java reflection works
     * is not great. You have to ask for a method in the class you're currently
     * working on but the methods aren't searched for any parent classes. So if A
     * inherits from B, you cannot just get the B methods from an A object by looking
     * specifically in the A class for them. You have to find all of the parent classes
     * of A one at a time and look for B's methods.
     *
     * @param klass The class to search for the method in.
     * @param name  The name of the method.
     * @param instance The instance of the class to search through.
     * @return The Method object associated with the name, null if not found.
     */
    public static Pair<Object, Method> getMethodNested(Class<?> klass, String name, Object instance) {
        var searchFor = name;
        String next = null;
        if (name.contains(".")) {
            var index = name.indexOf('.');
            searchFor = name.substring(0, index);
            next = name.substring(index + 1);
        }

        Field field;
        do {
            try {
                if (null != next) {
                    field = klass.getDeclaredField(searchFor);
                    field.setAccessible(true);
                    return getMethodNested(field.getType(), next, field.get(instance));
                }

                return new Pair<>(instance, klass.getMethod(searchFor));
            } catch (NoSuchFieldException | NoSuchMethodException e) {
                // Do nothing
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                break;
            }

            klass = klass.getSuperclass();
        } while (klass != null);

        return null;
    }
}
