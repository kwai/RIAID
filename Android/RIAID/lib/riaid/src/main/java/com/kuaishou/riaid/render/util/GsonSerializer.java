package com.kuaishou.riaid.render.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kuaishou.riaid.render.logger.ADRenderLogger;

/**
 * 这是通过Gson解析的
 */
public class GsonSerializer {

  /**
   * 全局维持一个就可以了，没有必要多次new
   */
  @NonNull
  private static final Gson sGson = new Gson();

  /**
   * 把data转换成JsonObject
   *
   * @param data 数据源字符串
   * @return 返回转换好的对象JsonObject
   */
  @Nullable
  public static JsonObject parse2JsonObject(@Nullable String data) {
    if (!TextUtils.isEmpty(data)) {
      try {
        return JsonParser.parseString(data).getAsJsonObject();
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parse2JsonObject 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 把JsonElement转成JsonObject
   *
   * @param element 属性对象
   * @param key     熟悉值key
   * @return 返回属性对象key对应的value，把value转成的JsonObject返回，如果存在并且成功的话
   */
  @Nullable
  public static JsonObject parse2JsonObject(@Nullable JsonElement element, @Nullable String key) {
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        return element.getAsJsonObject().get(key).getAsJsonObject();
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parse2JsonObject 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 把data转换成JsonArray
   *
   * @param data 数据源字符串
   * @return 返回转换好的数组对象JsonArray
   */
  @Nullable
  public static JsonArray parse2JsonArray(@Nullable String data) {
    if (!TextUtils.isEmpty(data)) {
      try {
        return JsonParser.parseString(data).getAsJsonArray();
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parse2JsonArray 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 把JsonElement转换成String
   *
   * @param element      目标对象
   * @param defaultValue 默认值，如果转换失败用这个值
   * @return 返回装换好的结果
   */
  @Nullable
  public static String parseElement2JsonString(@Nullable JsonElement element,
      @Nullable String defaultValue) {
    String result = null;
    if (element != null) {
      try {
        result = sGson.toJson(element);
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parseElement2JsonString 解析异常", e);
      }
    }
    return result != null ? result : defaultValue;
  }

  /**
   * 把Object转成String
   *
   * @param object       目标对象
   * @param defaultValue 默认值，如果转换失败用这个值
   * @return 返回装换好的结果
   */
  @NonNull
  public static String parseObject2JsonString(@Nullable Object object,
      @NonNull String defaultValue) {
    String result = null;
    if (object != null) {
      try {
        result = sGson.toJson(object);
      } catch (Exception e) {
        result = defaultValue;
        e.printStackTrace();
        ADRenderLogger.e("parseObject2JsonString 解析异常", e);
      }
    }
    return result != null ? result : defaultValue;
  }

  /**
   * 把JsonObject中的key对应的value，装换成string
   *
   * @param element      目标对象
   * @param key          对象的属性key
   * @param defaultValue 装换失败，或者获取失败的默认值
   * @return 返回装换好的结果
   */
  @Nullable
  public static String parseKey2JsonString(@Nullable JsonElement element, @Nullable String key,
      @Nullable String defaultValue) {
    String result = null;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        JsonElement keyElement = element.getAsJsonObject().get(key);
        if (keyElement != null) {
          result = sGson.toJson(keyElement);
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parseKey2JsonString 解析异常", e);
      }
    }
    return result != null ? result : defaultValue;
  }

  /**
   * 获取对象的属性，并把属性对应的值转成成对象
   *
   * @param element 目标对象
   * @param key     属性key
   * @return 返回对应的属性值对象
   */
  @Nullable
  public static JsonElement getJsonElement(@Nullable JsonElement element, @Nullable String key) {
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        return element.getAsJsonObject().get(key);
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("getJsonElement 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 获取对象的属性，并把属性对应的值转成数组
   *
   * @param element 目标对象
   * @param key     属性key
   * @return 返回对应的属性值数组
   */
  @Nullable
  public static JsonArray getJsonArray(@Nullable JsonElement element, @Nullable String key) {
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        return element.getAsJsonObject().get(key).getAsJsonArray();
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("getJsonArray 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 把Json字符串转成Bean对象
   *
   * @param data  目标Json字符串
   * @param clazz 需要转成的目标类型对象的class
   * @return 返回转换好的对象
   */
  @Nullable
  public static <T> T parse2BeanObject(@Nullable String data, @NonNull Class<T> clazz) {
    if (!TextUtils.isEmpty(data)) {
      try {
        return sGson.fromJson(data, clazz);
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parse2BeanObject 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 把Json字符串转正Bean的list集合
   *
   * @param data  目标Json字符串
   * @param clazz 需要转成的目标类型对象的class
   * @return 返回转换好的对象集合
   */
  @Nullable
  public static <T> List<T> parse2BeanArray(@Nullable String data, @NonNull Class<T> clazz) {
    if (!TextUtils.isEmpty(data)) {
      try {
        return sGson.fromJson(data, new TypeToken<List<T>>() {}.getType());
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parse2BeanArray 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 把字符串转换成Map<String,String>
   *
   * @param data 目标Json字符串
   * @return 返回处理好的map
   */
  @Nullable
  public static HashMap<String, String> parse2MapString(@Nullable String data) {
    if (!TextUtils.isEmpty(data)) {
      try {
        HashMap<String, Object> map =
            sGson.fromJson(data, new TypeToken<HashMap<String, Object>>() {}.getType());
        if (ToolHelper.isMapValid(map)) {
          HashMap<String, String> resultMap = new HashMap<>();
          for (Map.Entry<String, Object> entry : map.entrySet()) {
            resultMap.put(entry.getKey(), sGson.toJson(entry.getValue()));
          }
          return resultMap;
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("parse2MapString 解析异常", e);
      }
    }
    return null;
  }

  /**
   * 获取String类型的value
   *
   * @param element      目标对象
   * @param key          需要获取目标value的key
   * @param defaultValue 默认值，如果没有获取到，就用这个值
   * @return 获取指定key的value
   */
  @Nullable
  public static String optString(@Nullable JsonElement element, @Nullable String key,
      @Nullable String defaultValue) {
    String result = null;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        JsonElement keyElement = element.getAsJsonObject().get(key);
        if (keyElement != null) {
          result = keyElement.getAsString();
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("optString 解析异常", e);
      }
    }
    return result != null ? result : defaultValue;
  }

  /**
   * 获取key的对象，并转换成string
   *
   * @param element      目标对象
   * @param key          希望获取目标value的属性key
   * @param defaultValue 默认值，如果没有获取到，就用这个值
   * @return 获取指定Key的对象，并装成string
   */
  @Nullable
  public static String optObjectString(@Nullable JsonElement element, @Nullable String key,
      @Nullable String defaultValue) {
    String result = null;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        JsonElement keyElement = element.getAsJsonObject().get(key);
        if (keyElement != null) {
          result = sGson.toJson(keyElement);
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("optObjectString 解析异常", e);
      }
    }
    return result != null ? result : defaultValue;
  }

  /**
   * 获取Integer类型的value
   *
   * @param element      目标对象
   * @param key          希望获取目标value的属性key
   * @param defaultValue 默认值，如果没有获取到，就用这个值
   * @return 获取指定Key的int值
   */
  public static int optInt(@Nullable JsonElement element, @Nullable String key, int defaultValue) {
    int result = 0;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        JsonElement keyElement = element.getAsJsonObject().get(key);
        if (keyElement != null) {
          result = keyElement.getAsInt();
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("optInt 解析异常", e);
      }
    }
    return result != 0 ? result : defaultValue;
  }

  /**
   * 获取float类型的value
   *
   * @param element      目标对象
   * @param key          希望获取目标value的属性key
   * @param defaultValue 默认值，如果没有获取到，就用这个值
   * @return 获取指定Key的float值
   */
  public static float optFloat(@Nullable JsonElement element, @Nullable String key,
      float defaultValue) {
    float result = 0;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        JsonElement keyElement = element.getAsJsonObject().get(key);
        if (keyElement != null) {
          result = keyElement.getAsFloat();
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("optFloat 解析异常", e);
      }
    }
    return result != 0 ? result : defaultValue;
  }

  /**
   * 获取bool类型的value
   *
   * @param element      目标对象
   * @param key          希望获取目标value的属性key
   * @param defaultValue 默认值，如果没有获取到，就用这个值
   * @return 获取指定Key的bool值
   */
  public static boolean optBool(@Nullable JsonElement element, @Nullable String key,
      boolean defaultValue) {
    Boolean result = null;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        JsonElement keyElement = element.getAsJsonObject().get(key);
        if (keyElement != null) {
          result = keyElement.getAsBoolean();
        }
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("optBool 解析异常", e);
      }
    }
    return result != null ? result : defaultValue;
  }

  /**
   * 判断字符串是不是json
   *
   * @param data Json数据源字符串
   * @return 返回是否是JsonObject的结果
   */
  public static boolean isJsonData(@Nullable String data) {
    boolean result = false;
    if (!TextUtils.isEmpty(data)) {
      try {
        JsonParser.parseString(data).getAsJsonObject();
        result = true;
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("isJsonData 解析异常", e);
      }
    }
    return result;
  }

  /**
   * 判断是不是包含某一个属性
   *
   * @param element 目标对象
   * @param key     属性值的可以
   * @return 返回是否包含key属性
   */
  public static boolean containKey(@Nullable JsonElement element, @Nullable String key) {
    boolean result = false;
    if (element != null && !TextUtils.isEmpty(key)) {
      try {
        result = element.getAsJsonObject().get(key) != null;
      } catch (Exception e) {
        e.printStackTrace();
        ADRenderLogger.e("containKey 解析异常", e);
      }
    }
    return result;
  }

}
