# Kotlin 笔记

记录容易忘记的点 和 语言独有的特性

## 字符串拼接

使用 `$` 符号和 `{}` 组合可直接在字符串内添加表达式返回值。

```kotlin
var a = 1
val s1 = "a is $a"
a = 2
val s2 = "${s1.replace("is", "was")}, but now is $a"
```

## 数字

数字可用下划线连接

```kotlin
val a = 1000_000
```

## java to kotlin

```
instanceOf -> is
for each -> for in
```

## 对象

- 数据类

```kotlin
data class User(val name: String, val email: String)
```

- 单例类

```kotlin
object Utils {
  fun util() : String = "hello"
}
```

## if not null

```kotlin
// if not null and else
val files = File("Test").listFiles()
println(files?.size ?: "empty")

// is not null exec
var value: String? = null
value?.let{ /* exec if not null */ }
```

## 针对一个对象一次调用多个方法

```kotlin
class Turtle {
    fun penDown()
    fun penUp()
    fun turn(degrees: Double)
    fun forward(pixels: Double)
}

with(Turtle()) { // 画⼀个 100 像素的正⽅形
    penDown()
    for(i in 1..4) {
    forward(100.0)
    turn(90.0)
    }
    penUp()
}
```

## 使用 as 重定义导入类型的名字

```kotlin
import foo.Bar
import bar.Bar as bBar
```