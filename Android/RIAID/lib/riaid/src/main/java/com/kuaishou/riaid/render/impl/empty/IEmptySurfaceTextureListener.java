package com.kuaishou.riaid.render.impl.empty;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
 * TextureView.SurfaceTextureListener的空实现
 */
public interface IEmptySurfaceTextureListener extends TextureView.SurfaceTextureListener {

  default void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {}

  /**
   * Invoked when the {@link SurfaceTexture}'s buffers size changed.
   *
   * @param surface The surface returned by
   *                {@link TextureView#getSurfaceTexture()}
   * @param width   The new width of the surface
   * @param height  The new height of the surface
   */
  default void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

  /**
   * Invoked when the specified {@link SurfaceTexture} is about to be destroyed.
   * If returns true, no rendering should happen inside the surface texture after this method
   * is invoked. If returns false, the client needs to call {@link SurfaceTexture#release()}.
   * Most applications should return true.
   *
   * @param surface The surface about to be destroyed
   */
  default boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  /**
   * Invoked when the specified {@link SurfaceTexture} is updated through
   * {@link SurfaceTexture#updateTexImage()}.
   *
   * @param surface The surface just updated
   */
  default void onSurfaceTextureUpdated(SurfaceTexture surface) {}

}
