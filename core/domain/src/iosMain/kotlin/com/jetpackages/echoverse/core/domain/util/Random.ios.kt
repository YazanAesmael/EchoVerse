package com.jetpackages.echoverse.core.domain.util

import kotlin.random.Random

/**
 * The actual implementation for iOS, using Kotlin's standard random generator.
 */
public actual fun randomFloat(): Float {
    return Random.nextFloat()
}