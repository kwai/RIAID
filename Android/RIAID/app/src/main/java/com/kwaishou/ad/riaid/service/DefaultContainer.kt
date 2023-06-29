package com.kwaishou.ad.riaid.service

import android.annotation.SuppressLint
import android.content.Context
import com.kuaishou.riaid.render.service.base.IDataBindingService
import com.kuaishou.riaid.render.service.base.ILoadImageService
import com.kuaishou.riaid.render.service.base.IMediaPlayerService
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService
import com.kuaishou.riaid.render.service.base.IResumeActionService
import com.kuaishou.riaid.render.service.base.render.IRenderService
import com.kwaishou.riaid_adapter.glide.GlideImageService
import com.kwaishou.riaid_adapter.media.AndroidMediaServiceImpl

class DefaultContainer(var context: Context) : IRenderService {
  override fun getResumeActionService(): IResumeActionService {
    return IResumeActionService { _, _, _ -> }
  }

  override fun getLoadImageService(): ILoadImageService {
    return GlideImageService()
  }

  override fun getRIAIDLogReportService(): IRIAIDLogReportService {
    return IRIAIDLogReportService { key, value -> }
  }

  @SuppressLint("UseRequireInsteadOfGet")
  override fun getMediaService(): IMediaPlayerService {
    return AndroidMediaServiceImpl()
  }

  override fun getDataBindingService(): IDataBindingService {
    return DemoDataBindingService()
  
  }
}