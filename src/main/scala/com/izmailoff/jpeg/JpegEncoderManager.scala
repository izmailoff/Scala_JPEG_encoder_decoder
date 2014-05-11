package com.izmailoff.jpeg

import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import java.util.UUID
import com.izmailoff.jpeg.JpegMessages._
import com.izmailoff.util.CollectionUtils._
import com.izmailoff.jpeg.utils.{ImageChannelCombiner, ChannelBlockCombiner, ChannelBlockSplitter, ImageChannelSplitter}

/**
 * An actor that receives initial JPEG processing request and splits work between workers.
 * It supervises workers, collects their responses and returns result back to client/caller/sender.
 *
 * This actor is designed to be used once per processing request. Thus, it should/will be terminated when
 * request processing is complete and result is returned back to the client.
 *
 * Many instances of this class can be created as long as each actor gets a unique name.
 * See [[JpegApi.createJpegEncoderManager]] factory method for an example of how to achieve that. System threads will
 * be shared between all actors. Thus, logical threads of execution will amount to total number of manager actors
 * times number of workers each manager started. This can be controlled externally to achieve greater throughput or
 * latency.
 *
 * @param maxWorkers maximum number of workers that will be started to process a single request.
 *                   Number of workers will not exceed number of image blocks (8x8 pixels) since those workers would
 *                   never get a task to run.
 *                   It's recommended to set this number to number of available threads in the system to achieve
 *                   minimum processing latency per request.
 */
class JpegEncoderManager(maxWorkers: Int) extends Actor with ActorLogging {

  def createWorkers(numWorkers: Int) =
    1 to numWorkers map { num =>
      val actorName = s"WORKER_${num}_${UUID.randomUUID}"
      context.system.actorOf(Props[JpegWorker], actorName)
    }

  //val origWidth = imageData.length
  //val origHeight = imageData(0).length
  var processedData: Array[Array[Array[Int]]] = null
  var blocksRemaining = -1
  var resultCallback: ActorRef = null
  var channelCount = -1
  var quality = -1

  override def receive = {
    case r@JobRequest(data, channelNum, qualityPercent) => // use become() so we can never enter this state again ? - will not throw anyway or will it???
      resultCallback = sender
      channelCount = channelNum
      quality = qualityPercent
      val channels = ImageChannelSplitter.split(data) //splitImageToChannels(imageData)
      val horizBlocks = ChannelBlockSplitter.countHorizontalBlocks(data)
      val vertBlocks = ChannelBlockSplitter.countVerticalBlocks(data)
      val totalBlocks = horizBlocks * vertBlocks * channelCount
      blocksRemaining = totalBlocks
      log.debug(s"MANAGER: STARTED PROCESSING REQUEST: blocks: $totalBlocks, quality: $quality." )
      val maxWorkersNeeded = math.min(maxWorkers, totalBlocks)
      val workers = createWorkers(maxWorkersNeeded)
      log.debug(s"MANAGER: CREATED $maxWorkersNeeded WORKERS.")
      processedData = Array.ofDim[Int](channelCount, horizBlocks * ChannelBlockSplitter.BLOCK_DIM,
        vertBlocks * ChannelBlockSplitter.BLOCK_DIM)
      for {
        (channel, channelNum) <- channels.zipWithIndex
        blocks = ChannelBlockSplitter.split(channel)
        ((block, worker), blockNum) <- zipRepeat(blocks, workers).zipWithIndex
        _ = worker ! WorkerRequest(block, blockNum, channelNum)
      } ()
      log.debug("MANAGER: ALL JOBS ARE SENT TO WORKERS.")

    case a@WorkerResponse(block, blockNum, channelNum) =>
      log.debug(s"MANAGER: RECEIVED RESPONSE: channelNum: $channelNum, blockNum: $blockNum.")
      if (blocksRemaining > 0) {
        blocksRemaining -= 1
        println(s"MANAGER: TOTAL BLOCKS REMAINING: $blocksRemaining.")
        ChannelBlockCombiner.combine(block, blockNum, processedData(channelNum))
      }
      if (blocksRemaining == 0) {
        log.debug("MANAGER: ALL JOBS COMPLETE!")
        context.children foreach (context.stop(_))
        val result = ImageChannelCombiner.combine(processedData) // TODO: other final steps?
        resultCallback ! JobRequest(result, channelCount, quality)
        //context.stop(self)
      }

    case _@Progress =>
      log.debug(s"MANAGER: JOBS REMAINING $blocksRemaining.")
      sender ! blocksRemaining
  }

}

