package com.kuaishou.riaid.render.preload;


import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.kuaishou.riaid.proto.nano.ADActionModel;
import com.kuaishou.riaid.proto.nano.ADBeepActionModel;
import com.kuaishou.riaid.proto.nano.ADSceneModel;
import com.kuaishou.riaid.proto.nano.ADTransitionModel;
import com.kuaishou.riaid.proto.nano.ADTriggerModel;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.proto.nano.ImageAttributes;
import com.kuaishou.riaid.proto.nano.LottieAttributes;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kuaishou.riaid.proto.nano.TextAttributes;
import com.kuaishou.riaid.proto.nano.VideoAttributes;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.service.RiaidExecutorService;

/**
 * 负责RIAID的资源的预加载管理。相关的操作在子线程中执行，包括：
 * - 遍历Node找到对应的资源
 * - 下载资源存到本地中
 * - 下载库文档：https://docs.corp.kuaishou.com/d/home/fcAD4mEkLt1sH4Nt4jsClkMRn
 * 非单例
 */
public class RIAIDPreloadResourceOperator {
  public static final String PRELOAD_TAG = "PRELOAD_TAG";
  private static final String VIDEO = "video";
  private static final String LOTTIE = "lottie";
  private static final String AUDIO = "audio";
  private static final String IMAGE = "image";

  private static final String HTTP = "http";
  private static final String HTTPS = "https";

  /**
   * 用来存放当前在下载的url，防止重复下载
   */
  private final Set<String> mPreloadingUrlSet = new HashSet<>();
  /**
   * 是否打开的预下载
   */
  private static boolean isOpenPreload;

  public RIAIDPreloadResourceOperator() {
  }

  public static void init(boolean isOpenPreload) {
    RIAIDPreloadResourceOperator.isOpenPreload = isOpenPreload;
    RiaidLogger.i(PRELOAD_TAG, " isOpenPreload:" + isOpenPreload);
  }

  /**
   * @param url 要预下载的资源地址
   * @return 下载好的文件，如果为空则是没下载完成，如果返回则一定是存在的文件
   */
  @Nullable
  public static File getPreloadExistsFile(String url) {
    if (!isOpenPreload) {
      return null;
    }
    if (TextUtils.isEmpty(url)) {
      return null;
    }
    return RIAIDDownloadService.getInstance().getCache(url);
  }

  /**
   * @param url 图片地址
   * @return 图片地址或本地预加载好的图片路径
   */
  public static String getRealUrl(String url) {
    if (!isOpenPreload) {
      return url;
    }
    String realUrl = url;
    File preloadExistsFile = RIAIDPreloadResourceOperator.getPreloadExistsFile(url);
    if (preloadExistsFile != null) {
      Uri uri = Uri.fromFile(preloadExistsFile);
      if (uri == null) {
        return realUrl;
      }
      realUrl = uri.toString();
      ADRenderLogger.i(
          RIAIDPreloadResourceOperator.PRELOAD_TAG + "图片本地加载路径：" + realUrl +
              " url: " + url);
    } else {
      ADRenderLogger.i(
          RIAIDPreloadResourceOperator.PRELOAD_TAG + "网络加载路径：" + realUrl +
              " url: " + url);
    }
    return realUrl;
  }

  /**
   * 预加载一个RIAID数据中所有资源，在子线程执行。
   *
   * @param riaidModel RIAID数据，包含了场景触发器等
   */
  @WorkerThread
  public void preload(@Nullable RiaidModel riaidModel) {
    if (!isOpenPreload) {
      ADRenderLogger.w(RIAIDPreloadResourceOperator.PRELOAD_TAG + "没有打开预加载");
      return;
    }
    RiaidExecutorService.getExecutor()
        .submit(() -> {
          if (riaidModel == null) {
            return;
          }
          // 遍历所有的场景中的Node
          if (riaidModel.scenes != null) {
            for (ADSceneModel scene : riaidModel.scenes) {
              if (scene == null || scene.render == null) {
                continue;
              }
              preloadNodeRes(scene.render.renderData);
            }
          }
          // 遍历所有trigger，找到对应可能的资源
          if (riaidModel.triggers != null) {
            for (ADTriggerModel trigger : riaidModel.triggers) {
              if (trigger.timeout != null) {
                preloadActionRes(trigger.timeout.actions);
              }
            }
          }
        });
  }

  /**
   * 遍历节点中所有的子节点，找到所有最小子节点，并且预加载其中的资源
   *
   * @param node 根节点
   */
  private void preloadNodeRes(@Nullable Node node) {
    if (null == node) {
      return;
    }
    if (node.children != null && node.children.length > 0) {
      LinkedList<Node> linkedList = new LinkedList<>();
      linkedList.add(node);
      // 不断地遍历子节点不为空的Node，直到为空
      while (!linkedList.isEmpty()) {
        //removeFirst()删除第一个元素，并返回该元素
        Node current = linkedList.removeFirst();
        if (current == null) {
          continue;
        }
        //遍历linkedList中第一个Node中的子node
        for (int i = 0; i < current.children.length; i++) {
          Node child = current.children[i];
          if (child == null) {
            continue;
          }
          if (child.children != null && child.children.length > 0) {
            linkedList.addLast(child);
          } else {
            // 最小子节点
            preloadAttr(child.attributes);
          }
        }
      }
    } else {
      // 最小子节点
      preloadAttr(node.attributes);
    }
  }

  /**
   * 目前涉及到资源的Action：
   * {@link ADBeepActionModel}
   * {@link com.kuaishou.riaid.proto.nano.ADRenderContentTransitionModel}
   */
  private void preloadActionRes(@Nullable ADActionModel[] adActionModels) {
    if (adActionModels == null) {
      return;
    }
    for (ADActionModel adActionModel : adActionModels) {
      if (adActionModel.beep != null) {
        preloadDownload(adActionModel.beep.url, AUDIO);
      }
      if (adActionModel.transition != null && adActionModel.transition.transitions != null) {
        for (ADTransitionModel transition : adActionModel.transition.transitions) {
          if (transition.renderContent != null &&
              transition.renderContent.renderAttributes != null) {
            // 找到更新render的transition，并尝试加载相应有资源的Node属性
            preloadAttr(transition.renderContent.renderAttributes);
          }
        }
      }
    }
  }

  /**
   * {@link Attributes}包含了所有的属性，这里作为统一的入口
   *
   * @param attributes 可为空
   */
  private void preloadAttr(@Nullable Attributes attributes) {
    if (attributes == null) {
      return;
    }
    preloadLottieAttr(attributes.lottie);
    preloadImageAttr(attributes.image);
    preloadVideoAttr(attributes.video);
    // 按钮有子view，可能支持图片等
    preloadButtonAttr(attributes.button);
    // 文本有富文本，可能支持图片等
    preloadTextAttr(attributes.text);
  }

  /**
   * 预加载富文本中的一些可下载的资源
   *
   * @param attributes 文本的属性
   */
  private void preloadTextAttr(@Nullable TextAttributes attributes) {
    if (attributes == null) {
      return;
    }
    if (attributes.richList != null) {
      for (TextAttributes.RichText richText : attributes.richList) {
        if (richText == null) {
          continue;
        }
        preloadNodeRes(richText.content);
      }
    }
  }

  /**
   * 预加载按钮中的一些可下载的资源
   *
   * @param attributes 按钮的属性
   */
  private void preloadButtonAttr(@Nullable ButtonAttributes attributes) {
    if (attributes == null) {
      return;
    }
    preloadNodeRes(attributes.content);
    if (attributes.highlightStateList != null) {
      for (ButtonAttributes.HighlightState highlightState : attributes.highlightStateList) {
        if (highlightState == null) {
          continue;
        }
        preloadAttr(highlightState.attributes);
      }
    }
  }

  /**
   * 预加载Lottie中的一些可下载的资源
   *
   * @param attributes 按钮的属性
   */
  private void preloadLottieAttr(@Nullable LottieAttributes attributes) {
    if (attributes == null) {
      return;
    }
    preloadDownload(attributes.url, LOTTIE);
    if (attributes.replaceImageList != null) {
      for (LottieAttributes.ReplaceImage replaceImage : attributes.replaceImageList) {
        preloadDownload(replaceImage.imageAddress, IMAGE);
      }
    }
  }

  /**
   * 预加载图片中的一些可下载的资源
   *
   * @param attributes 按钮的属性
   */
  private void preloadImageAttr(@Nullable ImageAttributes attributes) {
    if (attributes == null) {
      return;
    }
    preloadDownload(attributes.url, IMAGE);
    preloadDownload(attributes.highlightUrl, IMAGE);
    preloadDownload(attributes.rtlUrl, IMAGE);
    preloadDownload(attributes.rtlHighlightUrl, IMAGE);
  }

  /**
   * 预加载视频中的一些可下载的资源
   *
   * @param attributes 按钮的属性
   */
  private void preloadVideoAttr(@Nullable VideoAttributes attributes) {
    if (attributes == null) {
      return;
    }
    preloadDownload(attributes.coverUrl, IMAGE);
    preloadDownload(attributes.url, VIDEO);
  }

  /**
   * 异步下载资源
   */
  public void preloadDownloadAsync(String url, @NonNull String type) {
    RiaidExecutorService.getExecutor()
        .submit(() -> preloadDownload(url, type));
  }

  /**
   * 通过下载文件的形式来预加载资源
   *
   * @param type 下载的资源类型，没有逻辑，仅仅适用于日志打印
   */
  private void preloadDownload(String url, @NonNull String type) {
    if (TextUtils.isEmpty(url)) {
      return;
    }

    Uri uri = Uri.parse(url);
    String scheme = uri.getScheme();
    if (scheme == null) {
      RiaidLogger.e(PRELOAD_TAG, "uri的scheme为空 url:" + url);
      return;
    }
    if (!scheme.equalsIgnoreCase(HTTP) && !scheme.equalsIgnoreCase(HTTPS)) {
      RiaidLogger.i(PRELOAD_TAG, "不是网络资源 type: " + type + " url：" + url);
      return;
    }

    if (mPreloadingUrlSet.contains(url)) {
      RiaidLogger.i(PRELOAD_TAG, "type: " + type + "preload  urlSet存在了：" + url);
      return;
    }

    if (getPreloadExistsFile(url) != null) {
      RiaidLogger.i(PRELOAD_TAG, "type: " + type + "preload 本地文件已经存在了 url：" + url);
      return;
    }

    try {
      ADRenderLogger.i(PRELOAD_TAG + "开始预加载 type:" + type + " url: " + url);
      RIAIDDownloadService.getInstance().download(url);
      mPreloadingUrlSet.add(url);
    } catch (Exception e) {
      RiaidLogger.e(PRELOAD_TAG, "type: " + type + " preload err", e);
    }
  }
}
