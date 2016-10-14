package com.huxin.common.utils;

import android.databinding.BaseObservable;
import android.text.TextUtils;
import android.util.Log;

import com.huxin.common.entity.IEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 反射赋值
 */
public class EntityUtils {

    public static ConcurrentHashMap<String, ConcurrentHashMap<String, Method>[]> classGetAndSetMethodMap = new ConcurrentHashMap<>();

    /**
     * 调用Class的get方法取出数据，并调用接收数据对象的set方法赋值
     *
     * @param object
     * @param resultObject
     * @param <T>
     */
    public static <T extends IEntity> void resolveAllFieldsSet(final T object, T resultObject) {
        if (null == object || null == resultObject) {
            return;
        }
        Class cls = resultObject.getClass();
        ConcurrentHashMap<String, Method>[] concurrentHashMapArray = searchGetAndSetMethods(cls);
        Iterator<Map.Entry<String, Method>> iterator = concurrentHashMapArray[0].entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Method> entry = iterator.next();
            String fieldName = entry.getKey();
            Method getMethod = entry.getValue();
            Method setMethod = concurrentHashMapArray[1].get(fieldName);
            if (null == setMethod || null == getMethod) {
                continue;
            }

            try {
                Object fieldVal = getMethod.invoke(resultObject, new Object[]{});
                setMethod.invoke(object, fieldVal);
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * 遍历class的get&set方便，并存入数组中缓存
     *
     * @param cls
     * @return
     */
    private static ConcurrentHashMap<String, Method>[] searchGetAndSetMethods(Class<?> cls) {
        String className = cls.getName();
        ConcurrentHashMap<String, Method>[] getSetMethodArray = null;
        getSetMethodArray = classGetAndSetMethodMap.get(className);
        if (null == getSetMethodArray) {
            ConcurrentHashMap<String, Method> getMethodsMap = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, Method> setMethodsMap = new ConcurrentHashMap<>();

            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                try {
                    method.setAccessible(true);
                    String methodName = method.getName();

                    if (isGetMethod(methodName)) {
                        String fieldName = getMethodField(methodName);
                        getMethodsMap.put(fieldName, method);
                        continue;
                    }

                    if (isSetMethod(methodName)) {
                        String fieldName = getMethodField(methodName);
                        setMethodsMap.put(fieldName, method);
                        continue;
                    }

                } catch (Exception e) {
                    continue;
                }
            }
            getSetMethodArray = new ConcurrentHashMap[2];
            getSetMethodArray[0] = getMethodsMap;
            getSetMethodArray[1] = setMethodsMap;
            classGetAndSetMethodMap.put(className, getSetMethodArray);
        }
        return getSetMethodArray;
    }

    public static <T extends IEntity> Object resolveAllFields(Class cls, T object, T resultObject) {
        if (cls != null && !cls.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            if (fields != null) {
                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName();
                    if (!name.startsWith("this$")) {
                        fields[i].setAccessible(true);
                        try {
                            Object v = fields[i].get(resultObject);
                            fields[i].set(object, v);
                        } catch (IllegalArgumentException ee) {
                            Log.e("EntityHellper", ee.getMessage());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (object instanceof BaseObservable) {
            ((BaseObservable) object).notifyChange();
        }
        return object;
    }

    public static Object resolveAllFields(Class<IEntity> cls, IEntity object, Class<IEntity> resultcls, IEntity resultObject) throws IllegalAccessException {
        if (cls != null && !cls.equals(Object.class) && null != resultcls && !resultObject.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            Field[] resultFields = resultcls.getDeclaredFields();

            if (fields != null && resultFields != null) {
                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName();
                    if (!name.startsWith("this$")) {
                        for (Field resultField : resultFields) {
                            fields[i].setAccessible(true);
                            String resultName = resultField.getName();
                            if (name.equals(resultName)) {
                                Object v = resultField.get(resultObject);
                                fields[i].set(object, v);
                            }
                        }

                    }
                }
            }
        }
        if (object instanceof BaseObservable) {
            ((BaseObservable) object).notifyChange();
        }
        return object;
    }

    /**
     * 取Bean的属性和值对应关系的MAP
     *
     * @param bean
     * @return Map
     */
    public static Map<String, Object> getFieldValueMap(IEntity bean) {
        Class<?> cls = bean.getClass();
        Map<String, Object> valueMap = new HashMap<String, Object>();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldType = field.getType().getSimpleName();
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                valueMap.put(field.getName(), fieldVal);
            } catch (Exception e) {
                continue;
            }
        }
        return valueMap;
    }

    /**
     * set属性的值到Bean
     *
     * @param bean
     * @param valMap
     */
    private static void setFieldValue(Object bean, Map<String, Object> valMap) {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            try {
                String fieldSetName = parSetName(field.getName());
//                if (!checkSetMet(methods, fieldSetName)) {
//                    continue;
//                }
                Method fieldSetMet = cls.getMethod(fieldSetName,
                        field.getType());
//              String fieldKeyName = parKeyName(field.getName());
                String fieldKeyName = field.getName();
                Object value = valMap.get(fieldKeyName);
                fieldSetMet.invoke(bean, value);
                //get方法比对赋值,用不上
//                String fieldGetName = parGetName(field.getName());
//                Method fieldGetMet = cls.getMethod(fieldGetName, null);
//                Object oldValue = fieldGetMet.invoke(bean, null);
//                if (oldValue!=value){
//                    fieldSetMet.invoke(bean, value);
//                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * 格式化string为Date
     *
     * @param datestr
     * @return date
     */
    public static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return (Date) sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMethodField(String getMethodName) {
        String fieldName = getMethodName.substring(3, getMethodName.length());
        return fieldName;
    }

    public static boolean isGetMethod(String methodName) {
        int index = methodName.indexOf("get");
        if (index == 0) {
            return true;
        }
        return false;
    }

    public static boolean isSetMethod(String methodName) {
        int index = methodName.indexOf("set");
        if (index == 0) {
            return true;
        }
        return false;
    }

    /**
     * 日期转化为String
     *
     * @param date
     * @return date string
     */
    public static String fmtDate(Date date) {
        if (null == date) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.US);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否存在某属性的 set方法
     *
     * @param methods
     * @param fieldSetMet
     * @return boolean
     */
    public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
        for (Method met : methods) {
            if (fieldSetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "get"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 获取存储的键名称（调用parGetName）
     *
     * @param fieldName
     * @return 去掉开头的get
     */
    public static String parKeyName(String fieldName) {
        String fieldGetName = parGetName(fieldName);
        if (!TextUtils.isEmpty(fieldGetName) && TextUtils.isEmpty(fieldGetName.trim())
                && fieldGetName.length() > 3) {
            return fieldGetName.substring(3);
        }
        return fieldGetName;
    }

}
