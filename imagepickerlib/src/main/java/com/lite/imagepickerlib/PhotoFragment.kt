package com.lite.imagepickerlib

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.photo_fragment.view.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class PhotoFragment : Fragment() {

    private lateinit var fragmentView: View
    private var imagePickActivity: GalleryActivity? = null
    fun setImagePickActivity(imagePickActivity: GalleryActivity) { this.imagePickActivity = imagePickActivity }

    private var mCapturingPicture: Boolean = false
    private var mCapturingVideo: Boolean = false

    // To show stuff in the callback
    private var mCaptureNativeSize: Size? = null
    private var mCaptureTime: Long = 0

    private var countDownTimer: CountDownTimer? = null
    private val maxDuration = 60 * 1000   // 60 seconds

    private var photo: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.photo_fragment, container, false)
        CameraLogger.setLogLevel(CameraLogger.LEVEL_INFO)

        fragmentView.camera.setLifecycleOwner(this)
        fragmentView.camera.addCameraListener(object : CameraListener() {
            override fun onCameraOpened(options: CameraOptions?) {}
            override fun onPictureTaken(jpeg: ByteArray?) {
                onPicture(jpeg!!)
            }

            override fun onVideoTaken(video: File?) {
                super.onVideoTaken(video)
                onVideo(video!!)
            }
        })

        fragmentView.switch_camera_btn.setOnClickListener { toggleCamera() }
        fragmentView.take_picture_btn.setOnClickListener { capturePhoto() }
        fragmentView.record_video_btn.setOnClickListener {
            if (!mCapturingVideo) {
                captureVideo()
                fragmentView.record_video_btn.setImageResource(R.drawable.ic_stop_button)
            } else {
                countDownTimer?.cancel()
                fragmentView.camera.stopCapturingVideo()
                fragmentView.record_video_btn.setImageResource(R.drawable.ic_play_button)
            }
        }

        return fragmentView
    }

    fun setPhotoFlag(photo: Boolean) {
        this.photo = photo
    }

    fun getPhotoFlag() : Boolean {
        return photo
    }

    fun changeView() {
        if (photo) {
            fragmentView.camera.sessionType = SessionType.PICTURE
            fragmentView.take_picture_btn.visibility = View.VISIBLE
            fragmentView.record_video_btn.visibility = View.GONE
            fragmentView.progress_bar.visibility = View.GONE
            fragmentView.video_time.visibility = View.GONE
        } else {
            fragmentView.camera.sessionType = SessionType.VIDEO
            fragmentView.take_picture_btn.visibility = View.GONE
            fragmentView.record_video_btn.visibility = View.VISIBLE
            fragmentView.progress_bar.visibility = View.VISIBLE
            fragmentView.video_time.visibility = View.VISIBLE
        }
    }

    private fun onPicture(fileBytes: ByteArray) {
        mCapturingPicture = false
        val callbackTime = System.currentTimeMillis()
        if (mCapturingVideo) {
            //Toast.makeText(activity!!, "Captured while taking video", Toast.LENGTH_SHORT).show()
            return
        }
        // This can happen if picture was taken with a gesture.
        if (mCaptureTime == 0L) {
            mCaptureTime = callbackTime - 300
        }
        if (mCaptureNativeSize == null)
            mCaptureNativeSize = fragmentView.camera.pictureSize

        mCaptureTime = 0
        mCaptureNativeSize = null
        val fileUrl = savePicture(fileBytes)
        Log.e("TAG", "onPicture captured  ${fileBytes.size}")
        val intent = Intent()
        val arrayList = ArrayList<String>()
        arrayList.add(fileUrl)
        intent.putExtra("images", arrayList)
        imagePickActivity?.onFinishScreen(Activity.RESULT_OK, intent)
    }

    private fun onVideo(video: File) {
        mCapturingVideo = false
        Log.e("TAG", "onVideo captured $video")
        val intent = Intent()
        val arrayList = ArrayList<String>()
        arrayList.add(video.absolutePath)
        intent.putExtra("images", arrayList)
        imagePickActivity?.onFinishScreen(Activity.RESULT_OK, intent)
    }

    private fun capturePhoto() {
        if (mCapturingPicture) return
        mCapturingPicture = true
        mCaptureTime = System.currentTimeMillis()
        mCaptureNativeSize = fragmentView.camera.pictureSize
        //Toast.makeText(activity!!, "Capturing picture...", Toast.LENGTH_SHORT).show()
        fragmentView.camera.capturePicture()
    }

    private fun captureVideo() {
        if (fragmentView.camera.sessionType !== SessionType.VIDEO) {
            Toast.makeText(activity!!, "Can't record video while session type is 'picture'.", Toast.LENGTH_SHORT).show()
            return
        }
        if (mCapturingPicture || mCapturingVideo) return
        mCapturingVideo = true

        val anim = ProgressBarAnimation(fragmentView.progress_bar, 0F, 100F)
        anim.duration = maxDuration + 4L
        fragmentView.progress_bar.startAnimation(anim)

        fragmentView.camera.videoMaxDuration = maxDuration

        var fileUrl = Environment.getExternalStorageDirectory().absolutePath + "/TempVideos"
        if (!File(fileUrl).exists())
            File(fileUrl).mkdirs()
        fileUrl = "$fileUrl/myVideo.mp4"

        countDownTimer = Timer()
        countDownTimer!!.start()
        fragmentView.camera.startCapturingVideo(File(fileUrl))
    }

    private fun toggleCamera() {
        if (mCapturingPicture) return
        fragmentView.camera.toggleFacing()
    }

    private fun savePicture(fileBytes: ByteArray): String {
        var fileUrl = Environment.getExternalStorageDirectory().absolutePath + "/TempImages"
        if (!File(fileUrl).exists())
            File(fileUrl).mkdirs()
        fileUrl = "$fileUrl/${System.currentTimeMillis()}.jpg"
        try {
            val bos = BufferedOutputStream(FileOutputStream(File(fileUrl)))
            bos.write(fileBytes)
            bos.flush()
            bos.close()
        } catch (e: Exception) { Log.e("TAG", "Exception : ${e.message}") }

        return  fileUrl
    }

    private inner class Timer: CountDownTimer(maxDuration.toLong(), 1000) {
        override fun onFinish() {}
        override fun onTick(timeLeft: Long) {
            val milliSec = maxDuration - timeLeft
            Log.e("TAG", "timeLeft : ${milliSec / 1000}")
            fragmentView.video_time.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(milliSec) -
                            TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSec)),
                    TimeUnit.MILLISECONDS.toSeconds(milliSec) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSec)))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

}