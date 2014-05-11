package com.izmailoff.jpeg

import akka.actor.{ActorLogging, Actor}
import com.izmailoff.jpeg.JpegMessages._

/**
 * A worker actor that receives [[WorkerRequest]] requests from [[JpegEncoderManager]], performs encoding steps
 * and returns results back.
 *
 * This worker will be started and shut down by manager actor.
 */
class JpegWorker extends Actor with ActorLogging {
  var blocksProcessed = 0

  override def receive = {
    case WorkerRequest(data, blockNum, channelNum) =>
      blocksProcessed += 1
      log.debug(s"WORKER: channel [$channelNum], block [$blockNum], name [${self.path.name}].")
      // TODO: perform all JPEG steps like DCT, quantization, etc that are done on 8x8 blocks
      sender ! WorkerResponse(data, blockNum, channelNum)
  }

  override def postStop(): Unit =
    println(s"WORKER: name [${self.path.name}] processed [$blocksProcessed].") // log.debug can't be used - log actor is killed

}
