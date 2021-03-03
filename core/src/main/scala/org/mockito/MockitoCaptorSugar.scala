package org.mockito

import org.mockito.captor.{ArgCaptor, Captor}

/**
  * Copyright (C) 03.03.21 - REstore NV
  */
trait MockitoCaptorSugar {
  case class capture[O](mock: O) {

    def all[T1: Captor](fun: O => T1 => _): Seq[T1] = {
      val captorA = ArgCaptor[T1]
      fun(Mockito.verify(mock))(captorA)
      captorA.values
    }

    def all[T1: Captor, T2: Captor](fun: O => (T1, T2) => _): Seq[(T1, T2)] = {
      val captorA = ArgCaptor[T1]
      val captorB = ArgCaptor[T2]
      fun(Mockito.verify(mock))(captorA, captorB)
      (captorA.values.zip(captorB.values))
    }

    def last[T1: Captor](fun: O => T1 => _): T1 = {
      val captorA = ArgCaptor[T1]
      fun(Mockito.verify(mock))(captorA.capture)
      captorA.value
    }

    def last[T1: Captor, T2: Captor](fun: O => (T1, T2) => _): (T1, T2) = {
      val captorA = ArgCaptor[T1]
      val captorB = ArgCaptor[T2]
      fun(Mockito.verify(mock))(captorA.capture(), captorB.capture())
      (captorA.value -> captorB.value)
    }
    //i don't mind writing it up to 22 if we go for it.
  }
}

object MockitoCaptorSugar extends MockitoCaptorSugar
