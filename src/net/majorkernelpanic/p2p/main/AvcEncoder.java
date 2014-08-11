package net.majorkernelpanic.p2p.main;

import java.nio.ByteBuffer;

import net.majorkernelpanic.streaming.hw.NV21Convertor;
import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

public class AvcEncoder {

	public MediaCodec mediaCodec;
	private int mWidth;
	private int mHeight;
	private byte[]mInfo= null;

	private byte[] yuv420 = null;

	@SuppressLint("NewApi")
	public AvcEncoder(int width, int height, int framerate, int bitrate) {

		mWidth = width;
		mHeight = height;
		yuv420 = new byte[width * height * 3 / 2];

		mediaCodec = MediaCodec.createEncoderByType("video/avc");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc",
				width, height);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
				MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); // 关键帧间隔 单位s

		mediaCodec.configure(mediaFormat, null, null,
				MediaCodec.CONFIGURE_FLAG_ENCODE);
		mediaCodec.start();
	}

	@SuppressLint("NewApi")
	public void close() {
		try {
			mediaCodec.stop();
			mediaCodec.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public int offerEncoder(byte[] input, byte[] output) {
		int pos = 0;
		NV21Convertor convertor = new NV21Convertor();
//		swapYV12toI420(input, yuv420, m_width, m_height);
		convertor.setSize(mWidth, mHeight);
		convertor.setSliceHeigth(mHeight);
		convertor.setStride(mWidth);
		convertor.setYPadding(0);
		convertor.setEncoderColorFormat(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
		yuv420 = convertor.convert(input);

		try {
			ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
			ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
			int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
			if (inputBufferIndex >= 0) {
				ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
				inputBuffer.clear();
				inputBuffer.put(yuv420);
				mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv420.length,
						0, 0);
			}

			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,
					0);

			while (outputBufferIndex >= 0) {
				ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
				byte[] outData = new byte[bufferInfo.size];
				outputBuffer.get(outData);

				if (mInfo != null) {
					System.arraycopy(outData, 0, output, pos, outData.length);
					pos += outData.length;
//					Log.e("OUT DATA LENGTH:", outData.length +"");
				}

				else // 在第一个帧里面保存pps sps 
				{
					ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
					if (spsPpsBuffer.getInt() == 0x00000001) {
						mInfo = new byte[outData.length];
						System.arraycopy(outData, 0, mInfo, 0, outData.length);
					} else {
						return -1;
					}
					
					for(int i=0; i<mInfo.length; i++) {
						Log.e("mInfo ========>", mInfo[i]+"");	
					}
					
				}
				

				mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
				outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,
						0);
			}

			if (output[4] == 0x65) // key frame MTK编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
			{
				System.arraycopy(output, 0, yuv420, 0, pos);
				System.arraycopy(mInfo, 0, output, 0, mInfo.length);
				System.arraycopy(yuv420, 0, output, mInfo.length, pos);
				pos += mInfo.length;
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return pos;
	}

	// yv12 转 yuv420p yvu -> yuv
	private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width,
			int height) {
		System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
		System.arraycopy(yv12bytes, width * height + width * height / 4,
				i420bytes, width * height, width * height / 4);
		System.arraycopy(yv12bytes, width * height, i420bytes, width * height
				+ width * height / 4, width * height / 4);
	}

}