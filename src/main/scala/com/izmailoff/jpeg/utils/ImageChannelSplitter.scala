package com.izmailoff.jpeg.utils

object ImageChannelSplitter {

  def split(image: Array[Array[Int]]): Array[Array[Array[Int]]] =
    Array(
      getMaskedArray(image, 0x00ff0000, 16), // R
      getMaskedArray(image, 0x0000ff00, 8), // G
      getMaskedArray(image, 0x000000ff, 0) //B
    )

  private def getMaskedArray(image: Array[Array[Int]], mask: Int, offset: Int): Array[Array[Int]] = {
    val width = image.length
    val height = image(0).length
    val result = Array.ofDim[Int](width, height)

    for {
      y <- 0 until height
      x <- 0 until width
      value = (image(x)(y) & mask) >> offset
      _ = result(x)(y) = value
    } ()

    result
  }

}
