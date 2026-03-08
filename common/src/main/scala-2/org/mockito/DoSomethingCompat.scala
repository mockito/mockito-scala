package org.mockito

/**
 * Scala 2 compatibility hook for DoSomething.
 *
 * In Scala 2, unlike Scala 3, <code>doAnswer(() => expr)</code> is dispatched to the by-name overload <code>doAnswer(l: => R)</code>, so no extra overload is needed here. This
 * trait stays empty and only mirrors the Scala 3 structure.
 */
private[mockito] trait DoSomethingCompat
