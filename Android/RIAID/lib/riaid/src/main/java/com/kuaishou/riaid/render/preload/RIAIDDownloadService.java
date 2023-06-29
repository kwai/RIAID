package com.kuaishou.riaid.render.preload;

import static com.kuaishou.riaid.render.util.ToolHelper.toMd5Key;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.Riaid;
import com.kuaishou.riaid.render.disklrucache.DiskLruCache;
import com.kuaishou.riaid.render.disklrucache.DiskLruCacheHelper;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.service.PublicServiceIoCManager;
import com.kuaishou.riaid.render.service.RiaidExecutorService;
import com.kuaishou.riaid.render.service.base.IDownloadService;

/**
 * RIAID的下载服务，可用于资源的预加载，目前仅提供下载和获取下载文件的接口
 */
class RIAIDDownloadService {
  private final static RIAIDDownloadService instance = new RIAIDDownloadService();

  @Nullable
  private volatile DiskLruCache diskLruCache;

  private RIAIDDownloadService() {}

  public static RIAIDDownloadService getInstance() {
    return instance;
  }

  private static final String TAG = RIAIDPreloadResourceOperator.PRELOAD_TAG;

  @Nullable
  private DiskLruCache getDiskLruCache() {
    if (diskLruCache == null) {
      // 如果为空, 保证线程安全
      synchronized (this) {
        if (diskLruCache == null) {
          // 构建DiskLruCache valueCount的含义是一个key有几个文件，这里一个就够了
          try {
            diskLruCache = DiskLruCache.open(getDownloadDirFile(), 1,
                1,
                DiskLruCacheHelper.MAX_CACHE_SIZE);
          } catch (IOException e) {
            RiaidLogger.e(RIAIDPreloadResourceOperator.PRELOAD_TAG,
                "getDiskLruCache 失败", e);
          }
        }
        return diskLruCache;
      }
    } else {
      return diskLruCache;
    }
  }


  /**
   * 下载指定链接的资源
   */
  public void download(String url) {
    if (TextUtils.isEmpty(url)) {
      return;
    }
    IDownloadService downloadService =
        PublicServiceIoCManager.getInstance().getService(IDownloadService.class);
    if (downloadService == null) {
      return;
    }
    downloadService.download(url, new IDownloadService.DownloadListener() {
      @Override
      public void onSuccess(String destinationDir, String targetFilePath, String targetFileName) {
        RiaidExecutorService.getExecutor().submit(() -> {
          if (!TextUtils.isEmpty(targetFilePath)
              && !TextUtils.isEmpty(targetFileName)) {
            // 因为下载库本身没有缓存清理的能力，所以需要将文件放到缓存管理，将原文件删除
            if (DiskLruCacheHelper
                .put(getDiskLruCache(), toMd5Key(url), new File(targetFilePath))) {
              RiaidLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG, "下载成功 url:" + url);
              //noinspection ResultOfMethodCallIgnored
              new File(destinationDir, targetFileName).delete();
            }

            downloadService.cancelDownloadTask(url);
          }
        });
      }

      @Override
      public void onFailed(String url, @NonNull Exception exception) {

      }
    });
  }

  private String getDownloadDir() {
    if (Riaid.getInstance().getApplication() == null) {
      return "";
    }
    return Riaid.getInstance().getApplication().getCacheDir() + File.separator + "riaid_cache/";
  }

  @Nullable
  private File getDownloadDirFile() {
    if (TextUtils.isEmpty(getDownloadDir())) {
      return null;
    }
    return new File(getDownloadDir());
  }

  /**
   * 从缓存中取出url对应的文件
   *
   * @return 如果不存在可能为空
   */
  @Nullable
  public File getCache(String url) {
    if (TextUtils.isEmpty(url)) {
      RiaidLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG, "getCache url为空");
      return null;
    }
    if (getDiskLruCache() == null) {
      RiaidLogger.i(RIAIDPreloadResourceOperator.PRELOAD_TAG, "getCache getDiskLruCache为空");
      return null;
    }
    InputStream inputStream = null;
    try {
      DiskLruCache.Snapshot snapshot = getDiskLruCache().get(toMd5Key(url));
      if (snapshot != null) {
        File file = snapshot.getFile(0);
        inputStream = snapshot.getInputStream(0);
        if (file.exists()) {
          RiaidLogger
              .d(TAG, "getDownloadFile 文件存在 url: " + url + " file:" + file.getAbsolutePath());
          return file;
        }
      }
    } catch (IOException ioException) {
      RiaidLogger.e(TAG, "RIAIDDownloadService getCache ioException", ioException);
    } finally {
      DiskLruCacheHelper.closeQuietly(inputStream);
    }
    RiaidLogger
        .i(TAG, "getDownloadFile 文件没找到 url: " + url);
    return null;
  }
}
