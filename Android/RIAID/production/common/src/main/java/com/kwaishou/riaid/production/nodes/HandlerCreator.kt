package com.kwaishou.riaid.production.nodes

import com.kuaishou.riaid.proto.Handler
import com.kuaishou.riaid.proto.LottieHandler
import com.kuaishou.riaid.proto.Responder
import com.kuaishou.riaid.proto.VideoHandler

/**
 * 创建Handler的口子
 */
object HandlerCreator {

    /**
     * 根据triggerKey创建responder
     */
    private fun createResponder(triggerKeyList: List<Int>): Responder {
        return Responder.newBuilder()
                .addAllTriggerKeys(triggerKeyList)
                .build()
    }

    /**
     * 根据手势的handler
     */
    fun createHandler(click: List<Int>? = null, doubleClick: List<Int>? = null, longPress: List<Int>? = null): Handler {
        val builder = Handler.newBuilder()
        if (!click.isNullOrEmpty()) builder.click = createResponder(click)
        if (!doubleClick.isNullOrEmpty()) builder.doubleClick = createResponder(doubleClick)
        if (!longPress.isNullOrEmpty()) builder.longPress = createResponder(longPress)
        return builder.build()
    }

    /**
     * 创建视频的handler
     */
    fun createVideoHandler(impression: List<Int>? = null, start: List<Int>? = null, finish: List<Int>? = null,
                           pause: List<Int>? = null, resume: List<Int>? = null): VideoHandler {
        val builder = VideoHandler.newBuilder()
        if (!impression.isNullOrEmpty()) builder.impression = createResponder(impression)
        if (!start.isNullOrEmpty()) builder.start = createResponder(start)
        if (!finish.isNullOrEmpty()) builder.finish = createResponder(finish)
        if (!pause.isNullOrEmpty()) builder.pause = createResponder(pause)
        if (!resume.isNullOrEmpty()) builder.resume = createResponder(resume)
        return builder.build()
    }

    fun createLottieHandler(start: List<Int>? = null, end: List<Int>? = null, replaceImageSuccess: List<Int>? = null, replaceImageFalse: List<Int>? = null
    ): LottieHandler {
        val builder = LottieHandler.newBuilder()
        if (!start.isNullOrEmpty()) builder.start = createResponder(start)
        if (!end.isNullOrEmpty()) builder.end = createResponder(end)
        if (!replaceImageSuccess.isNullOrEmpty()) builder.replaceImageSuccess = createResponder(replaceImageSuccess)
        if (!replaceImageFalse.isNullOrEmpty()) builder.replaceImageFalse = createResponder(replaceImageFalse)
        return builder.build()
    }

}