package com.ludwig.contextfns

import scala.collection.mutable.ArrayBuffer

// see https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html

case class Cell(elem: String)

class Row {
  private val cells = ArrayBuffer[Cell]()
  def add(c: Cell): Unit = cells += c
  override def toString: String = cells.mkString("Row(", ", ", ")")
}

class Table {
  private val rows = ArrayBuffer[Row]()
  def add(r: Row): Unit = rows += r
  override def toString: String = rows.mkString("Table(", ", ", ")")
}

def table(init: Table ?=> Unit) = {
  given t: Table = Table()
  init
  t
}

def row(init: Row ?=> Unit)(using t: Table) = {
  given r: Row = Row()
  init
  t.add(r)
}
  
def cell(str: String)(using r: Row) =
  r.add(Cell(str))

object TableWorkout {
  def main(args: Array[String]): Unit = {
    val t = table {
      row {
        cell("top left")
        cell("top right")
      }
      row {
        cell("bottom left")
        cell("bottom right")
      }
    }
    println(t)
  }
}
  

