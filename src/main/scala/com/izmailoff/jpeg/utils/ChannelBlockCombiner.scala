package com.izmailoff.jpeg.utils

object ChannelBlockCombiner {

  def combine(block: Array[Array[Int]], blockNum: Int, result: Array[Array[Int]]): Unit = {
    val horizBlocks = ChannelBlockSplitter.countHorizontalBlocks(result)
    val x = blockNum % horizBlocks
    val y = blockNum / horizBlocks
    for {
      i <- 0 until ChannelBlockSplitter.BLOCK_DIM
      j <- 0 until ChannelBlockSplitter.BLOCK_DIM
    } {
      result(x + i)(y + j) = block(i)(j)
    }
  }

}
