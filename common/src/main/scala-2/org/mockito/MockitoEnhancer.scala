package org.mockito

/**
 * Scala 2 specific trait that provides object mocking functionality. Extends MockCreator (Scala 2 version with WeakTypeTag) and MockitoEnhancerRuntime (shared utilities including
 * withObject methods).
 *
 * This is a thin compatibility layer - all actual functionality is in MockCreator and MockitoEnhancerRuntime.
 */
private[mockito] trait MockitoEnhancer extends MockCreator with MockitoEnhancerRuntime
