package com.example.rn.kotlintest

import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class KotlinTestBasic {
  
  companion object {
    private const val TAG = "KotlinTest"
    private fun l(log: String) = print("$TAG: $log")
  }
  
  @Test fun testString() {
    var a = 1
    val s1 = "a is $a"
    a = 2
    val s2 = "${s1.replace("is", "was")}, but now is $a"
    l(s2)
  }
  
  fun testStream(){
    val stream = Files.newInputStream(Paths.get("text"))
    stream.buffered().reader().use { reader ->
      println(reader.readText())
    }
    
  }
}