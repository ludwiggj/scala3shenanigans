package com.ludwig.contextfns

type Console = Console.type

type Print[A] = Console ?=> A

extension[A](print: Print[A]) {
  def prefix(first: Print[Unit]): Print[A] =
    Print {
      first
      print
    }

  def red: Print[A] =
    Print {
      Print.print(Console.RED)
      val result = print
      Print.print(Console.BLACK)
      result
    }
}

object Print {
  def print(msg: Any)(using c: Console): Unit =
    c.print(msg)

  def println(msg: Any)(using c: Console): Unit =
    c.println(msg)
    
  def run[A](print: Print[A]): A = {
    given c: Console = Console
    print
  }
    
  def apply[A](body: Console ?=> A): Print[A] =
    body
}

@main def go(): Unit = {
  val message: Print[Unit] =
//  val message = // won't compiloe
    Print.println("Hello from direct-style land!")

  val redMessage: Print[Unit] =
    Print.println("Amazing!").prefix(Print.print("> ").red)

  Print.run(message)
  Print.run(redMessage)
}
