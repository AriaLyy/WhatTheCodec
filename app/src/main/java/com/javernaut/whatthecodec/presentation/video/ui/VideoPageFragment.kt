package com.javernaut.whatthecodec.presentation.video.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.Observer
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.root.viewmodel.model.BasicVideoInfo
import com.javernaut.whatthecodec.presentation.stream.BasePageFragment
import com.javernaut.whatthecodec.presentation.stream.model.Stream
import com.javernaut.whatthecodec.presentation.stream.model.StreamFeature
import com.javernaut.whatthecodec.presentation.stream.model.makeStream
import kotlinx.android.synthetic.main.fragment_video_page.*

class VideoPageFragment : BasePageFragment(R.layout.fragment_video_page) {

    private var progressDialog: Dialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaFileViewModel.basicVideoInfoLiveData.observe(this, Observer {
            if (it != null) {
                displayStreams(listOf(convertToStream(it)))
            }
        })

        mediaFileViewModel.modalProgressLiveData.observe(this, Observer {
            progressDialog?.dismiss()
            progressDialog = if (it) {
                ProgressDialog.show(requireContext(), null, getString(R.string.message_progress))
            } else {
                null
            }
        })

        mediaFileViewModel.framesLiveData.observe(this, Observer {
            frameDisplayingView.setFrames(it)
        })

        mediaFileViewModel.framesBackgroundLiveData.observe(this, Observer { newColor ->
            val currentColor = (frameBackground.background as? ColorDrawable)?.color
                    ?: Color.TRANSPARENT
            ObjectAnimator.ofObject(frameBackground,
                    "backgroundColor",
                    ArgbEvaluator(),
                    currentColor,
                    newColor
            )
                    .setDuration(300)
                    .start()
        })
    }

    private fun convertToStream(basicVideoInfo: BasicVideoInfo): Stream {
        val videoStream = basicVideoInfo.videoStream

        return makeStream(videoStream.basicInfo, resources) {
            add(StreamFeature(R.string.info_file_format, basicVideoInfo.fileFormat))
            add(StreamFeature(R.string.page_video_codec_name, videoStream.basicInfo.codecName))

            add(StreamFeature(R.string.page_video_frame_width, videoStream.frameWidth.toString()))
            add(StreamFeature(R.string.page_video_frame_height, videoStream.frameHeight.toString()))

            add(StreamFeature(R.string.info_protocol_title, getString(
                    if (videoStream.fullFeatured) {
                        R.string.info_protocol_file
                    } else {
                        R.string.info_protocol_pipe
                    })))
        }
    }

    override fun onDestroy() {
        progressDialog?.dismiss()
        progressDialog = null
        super.onDestroy()
    }
}