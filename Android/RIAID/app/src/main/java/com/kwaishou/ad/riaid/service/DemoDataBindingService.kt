package com.kwaishou.ad.riaid.service

import android.text.TextUtils
import com.kuaishou.riaid.proto.nano.RIAIDConstants
import com.kuaishou.riaid.render.service.base.IDataBindingService
import com.kuaishou.riaid.render.util.ToolHelper.isDataBindingEqual

/**
 * 文本宏替换的Service
 */
class DemoDataBindingService : IDataBindingService {

  override fun parseDataHolder(dataHolder: String): String {
    return when {
      isDataBindingEqual(RIAIDConstants.DataBinding.TITLE, dataHolder) -> {
        return "title title title"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.DESCRIPTION, dataHolder) -> {
        return "Descrição do produto em inglês depois do português Origem: CN (origem) Número " +
            "modelo"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.CTA, dataHolder) -> {
        return "Let's Go"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.REWARD_FINISH_CONTINUE_WATCH, dataHolder) -> {
        return "Keep watching, more prize!"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.AD_TAG, dataHolder) -> {
        return "Ad"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.MERCHANT_DISCOUNT, dataHolder) -> {
        return "$1600.9"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.MERCHANT_PRICE, dataHolder) -> {
        return "$1900.99"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.MERCHANT_DISCOUNT_RATE, dataHolder) -> {
        return "-50%"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.MERCHANT_TITLE, dataHolder) -> {
        return "here is never going up"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.PLAYABLE_BG_URL, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.SUB_TITLE_ICON, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.MERCHANT_ICON, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.ICON_URL, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.MULTI_PIC_ICON_01, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.MULTI_PIC_ICON_02, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.MULTI_PIC_ICON_03, dataHolder) -> {
        return "http://s16.kwai.net/bs2/ad-i18n-dsp/af6b795e4f3243c7988cc00b6e5733f1.png"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.MULTI_PIC_TAG_01, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.MULTI_PIC_TAG_02, dataHolder) ||
          isDataBindingEqual(RIAIDConstants.DataBinding.MULTI_PIC_TAG_03, dataHolder) -> {
        return "hello world"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.SUB_TITLE_APP_TITLE, dataHolder) -> {
        return "come to challenge"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.PLAYABLE_FREE_TRIAL, dataHolder) -> {
        return "Free trial"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.PLAYABLE_DOWNLOAD, dataHolder) -> {
        return "Download"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.CANCEL, dataHolder) -> {
        return "Cancel"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.REPLAY, dataHolder) -> {
        return "replay"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.LARGE_PIC_ICON, dataHolder) -> {
        return "https://bs3-sgp.corp.kuaishou.com/ad-i18n-dsp/icon_large.png"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.SPLASH_SHAKE_UNLOCK_SURPRISE, dataHolder) -> {
        return "twist unlock surprise"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.FEED_EASTER_EGG_VIDEO_URL, dataHolder) -> {
        return "http://s16.kwai.net/bs2/ad-i18n-dsp/riaid/media/video_splash_easter_egg.mp4"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.SPLASH_SHAKE_ANIM, dataHolder) -> {
        return "http://s16.kwai.net/bs2/ad-i18n-dsp/riaid/lottie/splash_shake_default_lottie.zip"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.TWIST, dataHolder) -> {
        return "twist"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.BLIND_BOX_BOX_URL, dataHolder) -> {
        return "https://bs3-sgp.corp.kuaishou.com/ad-i18n-dsp/icon_large.png"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.BLIND_BOX_CARD_PERSON_URL, dataHolder) -> {
        return "http://s16.kwai.net/bs2/ad-i18n-dsp/af6b795e4f3243c7988cc00b6e5733f1.png"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.CTA_BTN_BG_COLOR, dataHolder) -> {
        return "#FFF44336"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.CTA_BTN_BG_PRESS_COLOR, dataHolder) -> {
        return "#99F44336"
      }
      TextUtils.equals("reward_count", dataHolder) -> {
        return "40"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.REWARD_WAIT_TITLE, dataHolder) -> {
        return "继续看视频领更多金币"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.REWARD_WAIT_DES, dataHolder) -> {
        return "更多金币"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.REWARD_WAIT_EARN_MORE, dataHolder) -> {
        return "赚更多"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.QUIT, dataHolder) -> {
        return "退出"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.COUPON_CARD_SPEC_TIP, dataHolder) -> {
        return "special tip coupon"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.COUPON_END_VALID_TIP, dataHolder) -> {
        return "expire time"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.COUPON_OFF_NUM, dataHolder) -> {
        return "80"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.COUPON_CODE, dataHolder) -> {
        return "12"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.DEEP_APP_ALERT_BTN, dataHolder) -> {
        return "continue"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.DEEP_APP_DISCLAIMER, dataHolder) -> {
        return "免责声明"
      }
      isDataBindingEqual(RIAIDConstants.DataBinding.DEEP_APP_ALERT_TITLE, dataHolder) -> {
        return "After ${400010} s will jump 哈哈"
      }
      else -> "dataHolder"
    }
  }
}