package com.kwaishou.riaid_adapter.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.kuaishou.riaid.Riaid;
import com.kuaishou.riaid.render.service.base.ILoadImageService;

public class GlideImageService implements ILoadImageService {
  @Override
  public void preload(@NonNull String url, @Nullable ILoadListener listener) {
    RequestBuilder<Drawable> load = Glide.with(Riaid.getInstance().getApplication())
        .load(url);
    getDrawableRequestBuilder(listener, load).preload();
  }

  @Override
  public void loadImage(@NonNull String url, @NonNull ImageView view,
      @Nullable ILoadListener listener) {
    RequestBuilder<Drawable> load = Glide.with(Riaid.getInstance().getApplication())
        .load(url);
    getDrawableRequestBuilder(listener, load).into(view);
  }

  @Override
  public void loadImage(@NonNull String url, @NonNull ImageView view) {
    Glide.with(Riaid.getInstance().getApplication())
        .load(url).into(view);
  }

  @Override
  public void loadImage(@NonNull String url, @NonNull ImageView view, int width, int height) {
    RequestBuilder<Drawable> load = Glide
        .with(Riaid.getInstance().getApplication())
        .load(url);
    if (width > 0 && height > 0) {
      load = load.override(width, height);
    }
    load.into(view);
  }

  @Override
  public void loadBitmap(@NonNull String url, @Nullable Drawable placeHolder,
      @Nullable Drawable errorDrawable, @Nullable IImageListener listener) {
    if (listener != null) {
      listener.onPrepareLoad(placeHolder);
    }
    Glide
        .with(Riaid.getInstance().getApplication())
        .asBitmap()
        .load(url)
        .placeholder(placeHolder)
        .error(errorDrawable)
        .addListener(new RequestListener<Bitmap>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Bitmap> target, boolean isFirstResource) {
            if (listener != null) {
              listener.onBitmapFailed(e, errorDrawable);
            }
            return false;
          }

          @Override
          public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
              DataSource dataSource, boolean isFirstResource) {
            return false;
          }
        })
        .into(new CustomTarget<Bitmap>() {
          @Override
          public void onResourceReady(@NonNull Bitmap resource,
              @Nullable Transition<? super Bitmap> transition) {
            if (listener != null) {
              listener.onBitmapLoaded(resource);
            }
          }

          @Override
          public void onLoadCleared(@Nullable Drawable placeholder) {

          }
        });
  }


  private RequestBuilder<Drawable> getDrawableRequestBuilder(@Nullable ILoadListener listener,
      RequestBuilder<Drawable> load) {
    if (listener != null) {
      load = load.addListener(new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model,
            Target<Drawable> target, boolean isFirstResource) {
          listener.onFailure(e);
          return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
            DataSource dataSource, boolean isFirstResource) {
          listener.onSuccess();
          return false;
        }
      });
    }
    return load;
  }
}
