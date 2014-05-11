package com.izmailoff.jpeg

import akka.actor.ActorSystem
import scala.concurrent.duration._

/**
 * TODO:
 * ENCODING STEPS for each 8x8 block:

    Shift the block
    Perform a DCT on the block
    Quantize the block
    Subtract the last DC coefficient from the current DC coefficient
    Zigzag the block
    Zero run length encode the block
    Break down the non-zero coefficients into variable-length binary numbers & their lengths
    Entropy encode the run lengths & binary number lengths
    Write the entropy encoded information & binary numbers to the output
 */


/**
 * An example of a client application that will consume JpegApi service.
 */
object JpegEncoderExample extends App {

  implicit val system = ActorSystem("jpeg-encoding-service")
  implicit val requestTimeout = 5 seconds

  val filename = "images/small.png" //val filename = "images/big.png"
  val outFilename = "images/out.jpeg"
  val numWorkers = 4
  val quality = 5
  val imageReader = new ImageInputFile(filename)
  val image = imageReader.readFile() // not the best way to load images!!!  //val image = Toolkit.getDefaultToolkit.getImage(filename)


  // OPTION 1 - BLOCKING WITH TIMEOUT:
  val result = JpegApi.blockingJpegEncode(image, quality, numWorkers)
  println(result)

  // OPTION 2 - NON-BLOCKING WILL CALLBACK:
  JpegApi.asyncJpegEncode(image, quality, numWorkers, r => println("SAVING FILE SOMEWHERE: " + r))


  // The reason I put sleep here is to wait for processing to finish before I shut down actor system.
  // In real application Actor System should be shut down when module or application shuts down.
  Thread.sleep((requestTimeout toMillis) * 2)
  system.shutdown()


}
