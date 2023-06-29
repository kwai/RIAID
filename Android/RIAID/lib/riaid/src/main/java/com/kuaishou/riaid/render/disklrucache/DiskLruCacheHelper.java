/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuaishou.riaid.render.disklrucache;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Junk drawer of utility methods. */
public final class DiskLruCacheHelper {
  static final Charset US_ASCII = StandardCharsets.US_ASCII;
  static final Charset UTF_8 = StandardCharsets.UTF_8;
  /**
   * The number of bytes in a kilobyte.
   */
  static final long ONE_KB = 1024;
  /**
   * The number of bytes in a megabyte.
   */
  public static final long ONE_MB = ONE_KB * ONE_KB;

  /**
   * The file copy buffer size (30 MB)
   */
  private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

  /**
   * 缓存的最大为60mb
   */
  public static final long MAX_CACHE_SIZE = ONE_MB * 60;

  private DiskLruCacheHelper() {
  }

  static String readFully(Reader reader) throws IOException {
    try {
      StringWriter writer = new StringWriter();
      char[] buffer = new char[1024];
      int count;
      while ((count = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, count);
      }
      return writer.toString();
    } finally {
      reader.close();
    }
  }

  /**
   * Deletes the contents of {@code dir}. Throws an IOException if any file
   * could not be deleted, or if {@code dir} is not a readable directory.
   */
  static void deleteContents(File dir) throws IOException {
    File[] files = dir.listFiles();
    if (files == null) {
      throw new IOException("not a readable directory: " + dir);
    }
    for (File file : files) {
      if (file.isDirectory()) {
        deleteContents(file);
      }
      if (!file.delete()) {
        throw new IOException("failed to delete file: " + file);
      }
    }
  }

  public static void closeQuietly(/*Auto*/Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (RuntimeException rethrown) {
        throw rethrown;
      } catch (Exception ignored) {
      }
    }
  }

  static String read2String(Reader reader) throws IOException {
    try (StringWriter writer = new StringWriter()) {
      char[] buffer = new char[1024];
      int count;
      while ((count = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, count);
      }
      return writer.toString();
    } finally {
      reader.close();
    }
  }

  /**
   * 存文件
   *
   * @return true 代表成功，false 出异常或者失败
   */
  public static boolean put(@Nullable DiskLruCache diskLruCache,
      @NonNull String key, @Nullable File file) {
    if (diskLruCache == null || file == null) {
      return false;
    }
    boolean success = false;
    try {
      DiskLruCache.Editor editor = diskLruCache.edit(key);
      if (editor != null) {
        if (editor.set(file)) {
          editor.commit();
          success = true;
        } else {
          editor.abort();
        }
        diskLruCache.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return success;
  }

  /**
   * 从 com.yxcorp.utility.io.FileUtils复制或来的
   */
  public static boolean renameTo(final File src, final File dest) {
    if (src.renameTo(dest)) {
      return true;
    } else {
      try {
        copyFile(src, dest);
        try {
          src.delete();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return true;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return false;
  }


  /**
   * Copies a file to a new location preserving the file date.
   * <p>
   * This method copies the contents of the specified source file to the
   * specified destination file. The directory holding the destination file is
   * created if it does not exist. If the destination file exists, then this
   * method will overwrite it.
   * <p>
   * <strong>Note:</strong> This method tries to preserve the file's last
   * modified date/times using {@link File#setLastModified(long)}, however
   * it is not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param srcFile  an existing file to copy, must not be {@code null}
   * @param destFile the new file, must not be {@code null}
   * @throws NullPointerException if source or destination is {@code null}
   * @throws IOException          if source or destination is invalid
   * @throws IOException          if an IO error occurs during copying
   */
  public static void copyFile(File srcFile, File destFile) throws IOException {
    copyFile(srcFile, destFile, true);
  }

  /**
   * Copies a file to a new location.
   * <p>
   * This method copies the contents of the specified source file
   * to the specified destination file.
   * The directory holding the destination file is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * {@code true} tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param srcFile          an existing file to copy, must not be {@code null}
   * @param destFile         the new file, must not be {@code null}
   * @param preserveFileDate true if the file date of the copy
   *                         should be the same as the original
   * @throws NullPointerException if source or destination is {@code null}
   * @throws IOException          if source or destination is invalid
   * @throws IOException          if an IO error occurs during copying
   */
  public static void copyFile(File srcFile, File destFile,
      boolean preserveFileDate) throws IOException {
    if (srcFile == null) {
      throw new NullPointerException("Source must not be null");
    }
    if (destFile == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if (!srcFile.exists()) {
      throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
    }
    if (srcFile.isDirectory()) {
      throw new IOException("Source '" + srcFile + "' exists but is a directory");
    }
    if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
      throw new IOException(
          "Source '" + srcFile + "' and destination '" + destFile + "' are the same");
    }
    File parentFile = destFile.getParentFile();
    if (parentFile != null) {
      if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
        throw new IOException("Destination '" + parentFile + "' directory cannot be created");
      }
    }
    if (destFile.exists() && destFile.canWrite() == false) {
      throw new IOException("Destination '" + destFile + "' exists but is read-only");
    }
    doCopyFile(srcFile, destFile, preserveFileDate);
  }


  /**
   * Internal copy file method.
   *
   * @param srcFile          the validated source file, must not be {@code null}
   * @param destFile         the validated destination file, must not be {@code null}
   * @param preserveFileDate whether to preserve the file date
   * @throws IOException if an error occurs
   */
  private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate)
      throws IOException {
    if (destFile.exists() && destFile.isDirectory()) {
      throw new IOException("Destination '" + destFile + "' exists but is a directory");
    }

    FileInputStream fis = null;
    FileOutputStream fos = null;
    FileChannel input = null;
    FileChannel output = null;
    try {
      fis = new FileInputStream(srcFile);
      fos = new FileOutputStream(destFile);
      input = fis.getChannel();
      output = fos.getChannel();
      long size = input.size();
      long pos = 0;
      long count = 0;
      while (pos < size) {
        count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
        pos += output.transferFrom(input, pos, count);
      }
    } finally {
      closeQuietly(output);
      closeQuietly(fos);
      closeQuietly(input);
      closeQuietly(fis);
    }

    if (srcFile.length() != destFile.length()) {
      throw new IOException("Failed to copy full contents from '" +
          srcFile + "' to '" + destFile + "'");
    }
    if (preserveFileDate) {
      destFile.setLastModified(srcFile.lastModified());
    }
  }
}