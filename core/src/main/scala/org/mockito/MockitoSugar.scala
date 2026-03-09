package org.mockito

trait MockitoSugar extends Rest with ScalacticSerialisableHack

/**
 * Simple object to allow the usage of the trait without mixing it in
 */
object MockitoSugar extends MockitoSugar
