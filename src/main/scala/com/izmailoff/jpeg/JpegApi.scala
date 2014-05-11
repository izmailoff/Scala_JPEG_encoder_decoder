package com.izmailoff.jpeg

import akka.actor.{ActorSystem, Props, ActorRef, ActorLogging}
import java.util.UUID
import com.izmailoff.jpeg.JpegMessages.JobRequest
import java.util.UUID
import com.izmailoff.jpeg.JpegMessages._
import akka.actor.ActorDSL._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.util.Timeout

/**
 * Low level JPEG encoding API. Applications might implement another level of indirection on top of these functions
 * to provide convenience methods to call this API.
 */
object JpegApi {

  /**
   * Creates a manager actor [[JpegEncoderManager]] that is ready to receive [[JobRequest]] or [[Progress]] messages.
   * Each request should create it's own manager actor by calling this function. Manager actor is
   * fairly cheap to create and it will be destroyed once processing is complete.
   *
   * @param numWorkers
   * @param system
   * @return
   */
  def createJpegEncoderManager(numWorkers: Int)(implicit system: ActorSystem): ActorRef = {
    val name = "manager_" + UUID.randomUUID
    system.log.debug(s"CREATED MANAGER ACTOR: [$name].")
    system.actorOf(Props(new JpegEncoderManager(numWorkers)), name)
  }

  /**
   * Creates a temporary actor that will communicate with [[JpegEncoderManager]]. Once request is complete
   * a callback will be called and both this temporary actor and manager will be shut down.
   * This is a non-blocking call and caller is free to continue working on other things while it's executing.
   *
   * @param image
   * @param quality
   * @param numWorkers
   * @param callback use callback to save results to a file, pipe them to another actor, etc.
   * @param system
   * @return
   */
  def asyncJpegEncode(image: Array[Array[Int]], quality: Int, numWorkers: Int, callback: JobRequest => Unit)
                     (implicit system: ActorSystem): Unit =
    actor(new Act with ActorLogging {
      system.log.debug("TEMP JPEG ACTOR: STARTED")
      val manager = createJpegEncoderManager(numWorkers)

      become {
        case result@JobRequest(data, channels, quality) =>
          log.debug("CLIENT: JOB FINISHED!!!")
          system.stop(manager)
          callback(result)
          system.stop(self)
      }

      manager ! JobRequest(image, 3, quality)
      system.log.debug("TEMP JPEG ACTOR: sent job request to MANAGER.")
    })

  /**
   * Starts processing by creating a manager actor [[JpegEncoderManager]] and immediately returns a Future which
   * will be satisfied once processing is complete.
   *
   * @param image
   * @param quality
   * @param numWorkers
   * @param system
   * @param timeout if manager actor does not respond with result within specified timeout returned Future will
   *                contain an error.
   * @return
   */
  def jpegEncode(image: Array[Array[Int]], quality: Int, numWorkers: Int)
                (implicit system: ActorSystem, timeout: Timeout = 10 seconds): Future[JobRequest] = {
    import akka.pattern.ask
    val manager = createJpegEncoderManager(numWorkers)(system)
    val futureResult = manager ? JobRequest(image, 3, quality)
    futureResult.mapTo[JobRequest]
  }

  /**
   * This is a BLOCKING call that starts processing by sending [[JobRequest]] to [[JpegEncoderManager]]. This call
   * will BLOCK and await for result to become available, i.e. for Future to become complete.
   * It involves 2 kinds of timeouts: how long should we wait for Future to become complete, and how long should we wait
   * for the Future to be returned from the manager actor (should be almost immediate).
   *
   * @param image
   * @param quality
   * @param numWorkers
   * @param system
   * @param timeout
   * @return
   */
  def blockingJpegEncode(image: Array[Array[Int]], quality: Int, numWorkers: Int)
                        (implicit system: ActorSystem, timeout: Duration = 10 seconds): JobRequest =
    Await.result(jpegEncode(image, quality, numWorkers), timeout)

}
