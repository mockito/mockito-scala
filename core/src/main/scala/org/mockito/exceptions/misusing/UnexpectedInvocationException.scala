package org.mockito.exceptions.misusing

import org.mockito.exceptions.base.MockitoException

class UnexpectedInvocationException(message: String, cause: Throwable = None.orNull)
    extends MockitoException(message, cause)
