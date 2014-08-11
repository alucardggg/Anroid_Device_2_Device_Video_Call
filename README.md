Anroid_Device_2_Device_Video_Call
=================================

project in GitHub and implements the video call between the android devices, add the MTK solution device support
( libstreaming does not support MTK) and fix some bugs


- The main Activity locates on the net.majorkernelpanic.p2p.main.MainActivity

- The project removes the libstreaming's MediaRecorder implementation and use the MediaCodec only! ( Low-latency stream)

- Remove the encoder-decoder detection to make it is compatible with MTK

- Add SPS and PPS support before each IDC.

- Fix some bugs

Two ways to start the Video Call

A.

1. After main activity is started, type the target host IP and start videocall.
2. Copy the SDP description that print in the Logcat and save it to .sdp file.
3. use the VLC player to open the file and stream it.

B.
1. The project integrated the NanoHttp and use the NanoHttp to deliver the SDP file.
2. Type the http://[IP]:8080/v.sdp to receive the stream.


note:
Audio.AAC use the MediaCodec to encode(Low-latency stream)
Audio.AMRNB use the MediaRecoder to encode(High-latency stream)
