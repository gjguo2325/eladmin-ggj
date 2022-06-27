package com.ggj.utils;

import com.ggj.exception.GeneralRuntimeException;

import java.lang.reflect.InvocationTargetException;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author guogj
 * @date 2022/06/27
 */
public class ConvertUtil {


  /**
   * Convert the given source bean to a target bean of specified type.
   *
   * @param source      the source bean
   * @param targetClass the class of target bean
   * @param <T>         the type of target bean
   * @return the target bean of type <code>T</code>
   */
  public static <T> T convert(Object source, Class<T> targetClass) {
    // Initialize a new instance of the target type.
    T result;
    try {
      result = targetClass.getDeclaredConstructor().newInstance();
    }catch (Exception e) {
      throw new GeneralRuntimeException("fail to create instance of type" + targetClass.getCanonicalName(), e);
    }
    copyProperties(source, result);
    return result;
  }
}
