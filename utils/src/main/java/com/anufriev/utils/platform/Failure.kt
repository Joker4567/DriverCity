package com.anufriev.utils.platform

/**
 * Base Class for handling errors/failures/exceptions.
 */
sealed class Failure {
    object ServerError : Failure()
    object CommonError : Failure()
    object UnknownError : Failure()
}
