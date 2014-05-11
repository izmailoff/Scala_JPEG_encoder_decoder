package com.izmailoff.jpeg.utils

object ImageChannelCombiner {

  def combine(channelData: Array[Array[Array[Int]]]): Array[Array[Int]] = {
    val width = channelData(0).length
    val height = channelData(0)(0).length
    val result = Array.ofDim[Int](width, height)

    def R = channelData(0)
    def G = channelData(1)
    def B = channelData(2)

    for {
      i <- 0 until width
      j <- 0 until height
    } {
      result(i)(j) = ((0xff << 24) |
        ((R(i)(j) << 16) & 0x00ff0000) |
        ((G(i)(j) << 8) & 0x0000ff00) |
        (B(i)(j) & 0x000000ff))
    }

    result
  }
}
