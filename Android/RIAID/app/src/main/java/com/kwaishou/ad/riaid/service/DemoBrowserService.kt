package com.kwaishou.ad.riaid.service

import android.content.Context
import com.kuaishou.riaid.adbrowser.service.ADBrowserService
import com.kuaishou.riaid.render.service.base.IDataBindingService
import com.kuaishou.riaid.render.service.base.ILoadImageService
import com.kuaishou.riaid.render.service.base.IMediaPlayerService
import com.kwaishou.riaid_adapter.glide.GlideImageService
import com.kwaishou.riaid_adapter.media.AndroidMediaServiceImpl

 class DemoBrowserService : ADBrowserService {
  override fun getDataBindingService(): IDataBindingService {
    return DemoDataBindingService()
  }

  override fun getLoadImageService(): ILoadImageService {
    return GlideImageService()
  }

  override fun getMediaPlayerService(context: Context): IMediaPlayerService {
    return AndroidMediaServiceImpl()
  }

}