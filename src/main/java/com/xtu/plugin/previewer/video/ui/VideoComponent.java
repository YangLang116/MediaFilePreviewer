package com.xtu.plugin.previewer.video.ui;


import com.intellij.openapi.application.ApplicationManager;
import com.xuggle.xuggler.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VideoComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    private volatile IContainer videoContainer;
    private volatile IStreamCoder audioCoder;
    private volatile IStreamCoder videoCoder;

    private volatile Image frameImage;
    private volatile Dimension frameSize;

    private volatile SourceDataLine audioLine; //系统声卡

    private volatile long playStartTime;
    private volatile long systemStartTime;

    private volatile boolean isRelease;
    private final ExecutorService executorService;

    private Dimension maxVideoSize;
    private OnFrameChangeListener frameChangeListener;

    public VideoComponent() {
        super();
        this.setLayout(new BorderLayout());
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void setOnFrameChangeListener(OnFrameChangeListener listener) {
        this.frameChangeListener = listener;
    }

    public void setMaxVideoSize(Dimension maxSize) {
        this.maxVideoSize = maxSize;
    }

    public void playVideo(String url, boolean loop) {
        this.executorService.submit(() -> {
            do {
                this.play(url);
                this.reset();
            } while (loop && !isRelease);
        });
    }

    private void play(String url) {
        try {
            this.videoContainer = IContainer.make();
            int openResult = this.videoContainer.open(url, IContainer.Type.READ, null);
            if (openResult < 0) {
                setError("视频文件打开失败~");
                return;
            }
            //解析音频轨道、视频轨道
            int streamNum = this.videoContainer.getNumStreams();
            int audioStreamID = -1;
            this.audioCoder = null;
            int videoStreamID = -1;
            this.videoCoder = null;
            for (int i = 0; i < streamNum; i++) {
                IStream stream = videoContainer.getStream(i);
                IStreamCoder streamCoder = stream.getStreamCoder();
                ICodec.Type codecType = streamCoder.getCodecType();
                if (audioStreamID == -1 && codecType == ICodec.Type.CODEC_TYPE_AUDIO) {
                    audioStreamID = i;
                    this.audioCoder = streamCoder;
                }
                if (videoStreamID == -1 && codecType == ICodec.Type.CODEC_TYPE_VIDEO) {
                    videoStreamID = i;
                    this.videoCoder = streamCoder;
                }
            }
            if (audioStreamID == -1 && videoStreamID == -1) {
                setError("无法解析视频数据~");
                return;
            }
            //处理音频轨道
            if (this.audioCoder != null) {
                int audioOpenResult = this.audioCoder.open();
                if (audioOpenResult < 0) {
                    setError("打开音频轨道失败~");
                    return;
                }
                boolean success = openSystemAudioStream(this.audioCoder);
                if (!success) {
                    setError("播放音频失败~");
                    return;
                }
            }
            //处理视频轨道
            IVideoResampler videoSampler = null;
            if (this.videoCoder != null) {
                int videoOpenResult = this.videoCoder.open();
                if (videoOpenResult < 0) {
                    setError("打开视频轨道失败~");
                    return;
                }
                if (this.videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                    videoSampler = IVideoResampler.make(
                            this.videoCoder.getWidth(),
                            this.videoCoder.getHeight(),
                            IPixelFormat.Type.BGR24,
                            this.videoCoder.getWidth(),
                            this.videoCoder.getHeight(),
                            this.videoCoder.getPixelType());
                    if (videoSampler == null) {
                        setError("视频播放失败~");
                        return;
                    }
                }
            }
            //读取数据
            IPacket packet = IPacket.make();
            this.playStartTime = Global.NO_PTS;
            this.systemStartTime = 0;
            while (this.videoContainer.readNextPacket(packet) >= 0) {
                if (this.isRelease) return;
                int streamIndex = packet.getStreamIndex();
                //播放视频
                if (streamIndex == videoStreamID) {
                    IVideoPicture picture = IVideoPicture.make(
                            this.videoCoder.getPixelType(),
                            this.videoCoder.getWidth(),
                            this.videoCoder.getHeight());
                    int byteDecoded = this.videoCoder.decodeVideo(picture, packet, 0);
                    if (byteDecoded < 0) {
                        setError("视频播放失败~");
                        return;
                    }
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        if (videoSampler != null) {
                            newPic = IVideoPicture.make(
                                    videoSampler.getOutputPixelFormat(),
                                    picture.getWidth(),
                                    picture.getHeight());
                            if (videoSampler.resample(newPic, picture) < 0) {
                                setError("视频转码失败~");
                                return;
                            }
                        }
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            setError("视频转码失败~");
                            return;
                        }
                        long delay = millisecondsUntilTimeToDisplay(newPic);
                        if (delay > 0) {
                            Thread.sleep(delay);
                        }
                        Image bufferedImage = createImage(newPic);
                        ApplicationManager.getApplication().invokeLater(new ImageRunnable(bufferedImage));
                    }
                }
                //播放音频
                else if (streamIndex == audioStreamID) {
                    IAudioSamples samples = IAudioSamples.make(1024, this.audioCoder.getChannels());
                    int offset = 0;
                    while (offset < packet.getSize()) {
                        int bytesDecoded = this.audioCoder.decodeAudio(samples, packet, offset);
                        if (bytesDecoded < 0) {
                            setError("音频播放失败~");
                            return;
                        }
                        offset += bytesDecoded;
                        if (samples.isComplete()) {
                            byte[] rawBytes = samples.getData().getByteArray(0, samples.getSize());
                            this.audioLine.write(rawBytes, 0, samples.getSize());
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ignore
        }
    }

    private Image createImage(IVideoPicture newPic) {
        int width = newPic.getWidth();
        int height = newPic.getHeight();
        BufferedImage image = Utils.videoPictureToImage(newPic);
//        if (width > maxVideoSize.width || height > maxVideoSize.height) {
//            double widthRadio = width * 1.0f / maxVideoSize.width;
//            double heightRadio = height * 1.0f / maxVideoSize.height;
//            double radio = Math.max(widthRadio, heightRadio);
//            int finalWidth = (int) (width / radio);
//            int finalHeight = (int) (height / radio);
//            Image scaledImage = image.getScaledInstance(finalWidth, finalHeight, Image.SCALE_DEFAULT);
//            image.flush();
//            return scaledImage;
//        } else {
            return image;
//        }
    }

    private long millisecondsUntilTimeToDisplay(IVideoPicture picture) {
        long millisecondsToSleep = 0;
        if (this.playStartTime == Global.NO_PTS) {
            // This is our first time through
            this.playStartTime = picture.getTimeStamp();
            // get the starting clock time so we can hold up frames
            // until the right time.
            this.systemStartTime = System.currentTimeMillis();
        } else {
            long systemClockCurrentTime = System.currentTimeMillis();
            long millisecondsClockTimeSinceStartVideo = systemClockCurrentTime - this.systemStartTime;
            // compute how long for this frame since the first frame in the stream.
            // remember that IVideoPicture and IAudioSamples timestamps are always in MICROSECONDS,
            // so we divide by 1000 to get milliseconds.
            long millisecondsStreamTimeSinceStartOfVideo = (picture.getTimeStamp() - this.playStartTime) / 1000;
            millisecondsToSleep = millisecondsStreamTimeSinceStartOfVideo - millisecondsClockTimeSinceStartVideo;
        }
        return millisecondsToSleep;
    }

    public void reset() {
        if (this.audioCoder != null) {
            this.audioCoder.close();
        }
        if (this.videoCoder != null) {
            this.videoCoder.close();
        }
        if (this.videoContainer != null) {
            this.videoContainer.close();
        }
        if (this.audioLine != null) {
            this.audioLine.close();
        }
    }

    public void release() {
        this.isRelease = true;
        try {
            this.executorService.shutdown();
        } catch (Exception e) {
            //ignore
        }
    }

    //打开系统声卡
    private boolean openSystemAudioStream(IStreamCoder aAudioCoder) {
        try {
            AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
                    (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
                    aAudioCoder.getChannels(),
                    true,
                    false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(audioFormat);
            audioLine.start();
            return true;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置错误提示
    private void setError(String error) {
        ApplicationManager.getApplication().invokeLater(() -> {
            JLabel errorLabel = new JLabel(error);
            errorLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            errorLabel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
            add(errorLabel, BorderLayout.CENTER);
        });
    }

    //更新视频帧
    private class ImageRunnable implements Runnable {
        private final Image newImage;

        public ImageRunnable(Image newImage) {
            this.newImage = newImage;
        }

        public void run() {
            int width = newImage.getWidth(null);
            int height = newImage.getHeight(null);
            Dimension newSize = new Dimension(width, height);
            VideoComponent.this.setFrameInfo(this.newImage, newSize);
        }
    }

    private void setFrameInfo(Image newImage, Dimension newSize) {
        if (this.frameChangeListener != null && !Objects.equals(newSize, this.frameSize)) {
            this.frameSize = newSize;
            this.frameChangeListener.onSizeChanged(newSize);
        }
        this.frameImage = newImage;
        this.repaint();
    }

    @Override
    public synchronized void paint(Graphics g) {
        if (this.frameImage != null){
            g.drawImage(this.frameImage, 0, 0, this);
            this.frameImage.flush();
        }
    }

    public interface OnFrameChangeListener {

        void onSizeChanged(Dimension size);
    }
}
