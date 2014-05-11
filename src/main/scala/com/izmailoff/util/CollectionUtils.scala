package com.izmailoff.util

object CollectionUtils {


  def zipRepeat[A, B](xs: Seq[A], repeatedElems: Seq[B]): Seq[(A, B)] = {
    val lSize = xs.size
    val sSize = repeatedElems.size
    if (lSize <= sSize) {
      xs zip repeatedElems
    }
    else {
      val (cur, next) = xs splitAt (sSize)
      cur.zip(repeatedElems) ++ zipRepeat(next, repeatedElems)
    }
  }

}
