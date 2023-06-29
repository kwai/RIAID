package com.kuaishou.riaid.render.service.base;

import androidx.annotation.NonNull;

/**
 * 这个是用来匹配服务端下发的key用的，举个栗子
 * 服务端下发image_url = ${imageUrl}
 * 这个时候，我需要获取imageUrl这个key，从map中查找value
 */
public interface IDataBindingService {

  /**
   * 不要给我null，可以给我一个""，如果实在匹配不上的话
   *
   * @param dataHolder 占位符key
   * @return 返回解析好的value
   */
  @NonNull
  String parseDataHolder(@NonNull String dataHolder);
}
