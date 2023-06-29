package com.kuaishou.riaid.render.service.base;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 下载资源的服务
 */
public interface IDownloadService {
  /**
   * 下载接口
   *
   * @param url              下载的url地址
   * @param downloadListener 下载回调
   */
  void download(String url, @Nullable DownloadListener downloadListener);

  /**
   * 取消下载任务
   *
   * @param url 下载的url地址
   */
  void cancelDownloadTask(String url);

  /**
   * 下载监听
   */
  interface DownloadListener {
    /**
     * 下载成功了
     *
     * @param destinationDir 下载目录
     * @param targetFilePath 目标文件路径
     * @param targetFileName 目标文件名字
     */
    void onSuccess(String destinationDir, String targetFilePath, String targetFileName);

    void onFailed(String url, @NonNull Exception exception);
  }
}
