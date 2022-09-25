package lamby

import cats.{Functor, Semigroup}

// From https://underscore.io/blog/posts/2016/12/05/type-lambdas.html
object TypeLambdaWorkout {
  def main(args: Array[String]): Unit = {
    // see
    // https://blog.rockthejvm.com/scala-3-type-lambdas/
    // https://medium.com/scala-3/scala-3-type-lambdas-polymorphic-function-types-and-dependent-function-types-2a6eabef896d

    def showResult[T](r: T): String = s":\n  => $r"

    println("Type Lambdas!")
    println("=============")

    println("\n(1) Basics\n")

    // list is a type constructor, L is a type alias
    type L = List[Option[(Int, Double)]]

    // T is a type alias that takes a parameter
    type T[A] = Option[Map[Int, A]]

    val t: T[String] = Some(Map(1 -> "abc", 2 -> "xyz"))

    println(s"Option[Map[Int, A]] instantiated via T[String]${showResult(t)}")

    // trait Functor[F[_]] ... {
    //  def map[A, B](fa: F[A])(f: A => B): F[B]
    // }

    // Map from Option[A] to Option[B]
    type F1 = Functor[Option] // OK, as Option[T]

    def optionFunctor: F1 = new F1 {
      override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
    }

    val maybeString = Some("333")
    println(s"Functor[Option] mapping $maybeString to length${showResult(optionFunctor.map(maybeString)(_.length))}")

    // Map from List[A] to List[B]
    type F2 = Functor[List] // OK, as List[T]

    def listFunctor: F2 = new F2 {
      override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
    }

    val ascendingList = List(1, 2, 3)
    println(s"Functor[List] triple each element of $ascendingList${showResult(listFunctor.map(ascendingList)(_ * 3))}")

    // Map from Map[A] to Map[B] ???
    // type F3 = Functor[Map]    // Not OK, as Map[K,V]; error is "Map takes 2 type parameters, expected: 1"

    // Type aliases are often used to ‘partially apply’ a type constructor,
    // and so to ‘adapt’ the kind of the type to be used

    type IntKeyMap[A] = Map[Int, A]

    // IntKeyMap now takes a single type parameter, and the compiler is happy with that.

    // Map from IntKeyMap[A] to IntKeyMap[B]
    // i.e. from Map[Int, A] to Map[Int, B]
    type F3 = Functor[IntKeyMap] // ok

    def mapWithIntKeyFunctor: F3 = new F3 {
      override def map[A, B](fa: IntKeyMap[A])(f: A => B): IntKeyMap[B] = fa.map {
        case (k, v) => (k + 1, f(v))
      }
    }

    val farmMap = Map(1 -> "how", 2 -> "now", 3 -> "brown", 4 -> "cow")
    val reverseMapValues = mapWithIntKeyFunctor.map(farmMap)(_.reverse)
    println(s"Functor[Map[Int, A]] via type alias, reversing values of $farmMap${showResult(reverseMapValues)}")

    println("\n(2) Type Lambdas!\n")

    // Can we achieve the same goal without having to declare an alias?

    // Yes, using type lambdas which are directly supported in scala 3 (https://blog.rockthejvm.com/scala-3-type-lambdas/)

    type MapIntV = [V] =>> Map[Int, V]

    // We can declare a local variable using the lambda syntax
    val aMap: MapIntV[String] = Map(1 -> "1", 2 -> "2")

    // And use it to express parameter type
    def showMap(m: MapIntV[String]): String =
      showResult(m)

    println(s"aMap (showMap)${showMap(aMap)}")

    // Don't need direct version in parameter type as this is the equivalent
    def showMap2[V](m: Map[Int, V]): String =
      showResult(m)

    println(s"aMap (showMap2)${showMap2(aMap)}")

    // Another possibility
    def showMap3[V](m: MapIntV[V]): String =
    showResult(m)

    println(s"aMap (showMap3)${showMap3(aMap)}")

    type F4 = Functor[MapIntV]

    // The above is the equivalent of;

    // type T[V] = Map[Int, V]
    // type F4 = Functor[T]

    println(s"Local variable declared using the lambda syntax${showResult(aMap)}")

    def mapWithIntKeyFunctor2: F4 = new F4 {
      override def map[A, B](fa: Map[Int, A])(f: A => B): Map[Int, B] = fa.map {
        case (k, v) => (k + 2, f(v))
      }
    }

    val anotherMap = Map(1 -> 2.0, 2 -> 3.0, 3 -> 4.0, 4 -> 5.0)
    println(
      s"Functor[[V] =>> Map[Int, V]] via type lambda, incrementing key by 2 and cubing values of $anotherMap" +
        showResult(mapWithIntKeyFunctor2.map(anotherMap)(d => d * d * d))
    )

    // Direct definition
    def mapWithIntKeyFunctor3 = new Functor[[V] =>> Map[String, V]] {
      override def map[A, B](fa: Map[String, A])(f: A => B): Map[String, B] = fa.map {
        case (k, v) => (k.reverse, f(v))
      }
    }

    val abcMap = Map("abc" -> 8, "xyz" -> 12)

    println(
      s"Functor[[V] =>> Map[String, V]] via type lambda, reversing key and squaring values of $abcMap" +
        showResult(mapWithIntKeyFunctor3.map(abcMap)(d => d * d))
    )

    // Consider the following rather ABSTRACT EXAMPLE:

    def foo[A[_, _], B](functor: Functor[[C] =>> A[B, C]]): Functor[[C] =>> A[B, C]] = functor

    // To construct such a lambda we need a function where A[_, _] and B are already in scope, so we'll rename the
    // types of the map method from [A, B] to [S, T] to avoid type shadowing. We get:
    def aFunctor[A[_, _], B]: Functor[[C] =>> A[B, C]] = new Functor[[C] =>> A[B, C]] {
      // The above type lambda represents a Functor that maps from TT[S] to TT[T], where TT[C] = A[B, C].
      // Substituting S and T into TT in turn, we see that the lambda maps from A[B, S] to A[B, T].

      // It's difficult to implement this method without knowing more about the types
      override def map[S, T](fa: A[B, S])(f: S => T): A[B, T] = ???
    }

    // Can we use aFunctor method and pass the functor it returns as an argument to the foo method?
    val mapWithStringKeyFunctor: Functor[[C] =>> Map[String, C]] = aFunctor[Map, String]
    val fooMapWithStringKeyFunctor: Functor[[C] =>> Map[String, C]] = foo(mapWithStringKeyFunctor) // Compiles OK


    // val f = aFunctor
    // val fooFFunctor = foo(f) // Doesn't compile, we need to line up the types :)

    def lineUpTheTypes[A[_, _], B]: Functor[[C] =>> A[B, C]] = {
      foo(aFunctor[A, B]) // Compiles OK
    }

    // This time let's reimplement aFunctor, but give a bit more typing help so we can write a
    // sensible implementation of the map method

    // Note that two type hints use different techniques;
    // A uses upper type bound (https://docs.scala-lang.org/tour/upper-type-bounds.html)
    // B uses context bound (https://stackoverflow.com/questions/2982276/what-is-a-context-bound-in-scala)
    def anotherFunctor[A[K, V] <: Map[K, V], B: Semigroup]: Functor[[C] =>> A[B, C]] =
      new Functor[[C] =>> A[B, C]] {
        override def map[S, T](fa: A[B, S])(f: S => T): A[B, T] = fa.map {
          case (k, v) => (Semigroup[B].combine(k, k), f(v))
        }.asInstanceOf[A[B, T]]
      }

    // Can we use anotherFunctor method and pass the functor it returns as an argument to the foo method?
    val mapWithStringKeyAnotherFunctor: Functor[[C] =>> Map[String, C]] = anotherFunctor[Map, String]
    val fooMapWithStringKeyAnotherFunctor: Functor[[C] =>> Map[String, C]] = foo(mapWithStringKeyAnotherFunctor) // Compiles OK

    val alphabetMap = Map("1" -> "abc", "2" -> "xyz")
    println(
      s"Functor[[C] =>> Map[String, C]] via type lambda, combining key with itself and reversing values of $alphabetMap" +
        showResult(fooMapWithStringKeyAnotherFunctor.map(alphabetMap)(_.reverse))
    )

//    val f = anotherFunctor    // Doesn't compile, ambiguous given instances
//    val fooFAnotherFunctor = foo(f)

    def lineUpTheTypes2[A[K, V] <: Map[K, V], B: Semigroup]: Functor[[C] =>> A[B, C]] = {
      foo(anotherFunctor[A, B]) // Compiles OK
    }

    val oneTwoMap = Map(1 -> 1, 2 -> 2)
    println(
      s"Functor[[C] =>> Map[Int, C]] via type lambda, combining key with itself and adding 4 to values of $oneTwoMap" +
        showResult(lineUpTheTypes2[Map, Int].map(oneTwoMap)(_ + 4))
    )

    println("\n(3) Splitting foo definition\n")

    // If we prefer not to use type lambdas we can split the definition of foo in two:

    class Foo[A[_, _], B] {
      type AB[C] = A[B, C]

      def apply(functor: Functor[AB]): Functor[AB] = functor // Apply in a class :)
    }

    def aFoo[A[_, _], B] = new Foo[A, B]

    // Let's kick aFoo's tyres
    val aFooMapWithStringKeyFunctor: Functor[[C] =>> Map[String, C]] = aFoo[Map, String](mapWithStringKeyFunctor)

    // val f = aFunctor
    // val aFooFAFunctor = aFoo(f) // Doesn't compile, we need to line up the types :)

    def lineUpTheTypes3[A[_, _], B]: Functor[[C] =>> A[B, C]] = {
      aFoo[A, B](aFunctor[A, B]) // Compiles OK
    }

    def lineUpTheTypes4[A[K, V] <: Map[K, V], B: Semigroup]: Functor[[C] =>> A[B, C]] = {
      aFoo[A, B](anotherFunctor[A, B]) // Compiles OK
    }

    val roloMap = Map("ro" -> 1, "lo" -> 2)
    println(
      s"Functor[[C] =>> Map[String, C]] via Foo, combining key with itself and dividing values by 1 of $roloMap" +
        showResult(lineUpTheTypes4[Map, String].map(roloMap)(_ / 1))
    )

    val aFooMapWithStringKeyFunctor2 = aFoo[Map, String](mapWithStringKeyAnotherFunctor)
    println(
      s"Functor[[C] ==> Map[String, C]] via Foo, combining key with itself and reversing values of $alphabetMap" +
        showResult(aFooMapWithStringKeyFunctor2.map(alphabetMap)(_.reverse))
    )

    println("\n(4) Kind projector plugin\n")

    // As per readme, Tuple2[*, Double] is equivalent to: type R[A] = Tuple2[A, Double]

    type R[A] = Tuple2[A, Double]

    def swap(t: R[_]): (Double, _) = t.swap

    val tuple = ("egg", 1.0)
    println(s"swap $tuple${showResult(swap(tuple))}")

    type Tuple2SomethingDouble = [T] =>> (T, Double)

    def swap1[T](t: Tuple2SomethingDouble[T]): (Double, _) = t.swap

    println(s"swap1 $tuple${showResult(swap1(tuple))}")

    def aTupleFunctor: Functor[Tuple2SomethingDouble] = new Functor[Tuple2SomethingDouble] {
      override def map[A, B](fa: (A, Double))(f: A => B): (B, Double) = (f(fa._1), fa._2)
    }

    val tuple2 = (1, 1.0)
    println(s"Functor[Tuple2SomethingDouble] map $tuple2 Int key to double${showResult(aTupleFunctor.map(tuple2)(_.toDouble))}")

    val tuple3 = ("Hello", 1.0)
    println(s"Functor[Tuple2SomethingDouble] map $tuple3 String key to reverse${showResult(aTupleFunctor.map(tuple3)(_.reverse))}")
  }
}