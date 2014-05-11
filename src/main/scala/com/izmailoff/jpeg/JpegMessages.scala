package com.izmailoff.jpeg

/**
 * TODO
 */
object JpegMessages {

   trait JpegJobMessage

   case class JobRequest(data: Array[Array[Int]], channelCount: Int, quality: Int) extends JpegJobMessage {
     require(quality >= 0 && quality <= 100, "Quality must be between 0 and 100.")
     require(channelCount == 1 || channelCount == 3, "Number of channels can be either 1 - grayscale, or 3 - RGB.")
   }

   case class WorkerRequest(data: Array[Array[Int]], blockNum: Int, channelNum: Int) extends JpegJobMessage

   case class WorkerResponse(data: Array[Array[Int]], blockNum: Int, channelNum: Int) extends JpegJobMessage

   case object Progress extends JpegJobMessage

 }
