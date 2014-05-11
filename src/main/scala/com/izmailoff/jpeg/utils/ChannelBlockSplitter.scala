package com.izmailoff.jpeg.utils

object ChannelBlockSplitter {
  val BLOCK_DIM = 8

  def split(channel: Array[Array[Int]]): Array[Array[Array[Int]]] = {
    val numHorizBlocks = countHorizontalBlocks(channel)
    val numVertBlocks = countVerticalBlocks(channel)
    val blocks = Array.ofDim[Int](numHorizBlocks * numVertBlocks, BLOCK_DIM, BLOCK_DIM)
    var blockIdx = 0

    for {
      i <- 0 until numHorizBlocks
      j <- 0 until numVertBlocks
      y = i * BLOCK_DIM
      x = j * BLOCK_DIM
      block = getBlock(channel, x -> y)
    } {
      blocks(blockIdx) = block
      blockIdx += 1
    }

    def getBlock(from: Array[Array[Int]], offset: (Int, Int)): Array[Array[Int]] = {
      val (x, y) = offset
      val block = Array.ofDim[Int](BLOCK_DIM, BLOCK_DIM)
      for {
        i <- x until math.min(from.length, x + BLOCK_DIM) // padded with 0s
        j <- y until math.min(from(0).length, y + BLOCK_DIM)
        _ = block(i -x)(j - y) = from(i)(j)
      } ()
      block
    }

    blocks
  }

  def countHorizontalBlocks(image: Array[Array[Int]]): Int = {
    val imgWidth = image.length
    imgWidth / BLOCK_DIM + (if(imgWidth % BLOCK_DIM > 0) 1 else 0)
  }

  def countVerticalBlocks(image: Array[Array[Int]]): Int = {
    val imgHeight = image(0).length
    imgHeight / BLOCK_DIM + (if(imgHeight % BLOCK_DIM > 0) 1 else 0)
  }

  def countTotalBlocks(image: Array[Array[Int]]): Int =
    countHorizontalBlocks(image) * countVerticalBlocks(image)
}
