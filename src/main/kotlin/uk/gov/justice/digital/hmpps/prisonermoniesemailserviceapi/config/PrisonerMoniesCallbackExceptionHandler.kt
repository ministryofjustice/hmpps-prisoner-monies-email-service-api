
package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.config

import org.springframework.core.NestedExceptionUtils
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import tools.jackson.databind.exc.InvalidFormatException
import tools.jackson.databind.exc.MismatchedInputException
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.CallbackController
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.exception.InvalidJsonPayloadException
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.exception.UnknownCallbackTypeException

@RestControllerAdvice(assignableTypes = [CallbackController::class])
class PrisonerMoniesCallbackExceptionHandler {

  @ExceptionHandler(
    InvalidJsonPayloadException::class,
    UnknownCallbackTypeException::class,
    HttpMessageNotReadableException::class,
    MismatchedInputException::class,
    InvalidFormatException::class,
  )
  fun handleBadRequest(e: Exception): ResponseEntity<String> {
    val cause = NestedExceptionUtils.getMostSpecificCause(e)

    val message = when (cause) {
      is UnknownCallbackTypeException -> "JSON payload is not a known callback type"
      is InvalidJsonPayloadException -> "Invalid JSON payload"
      else -> "Invalid JSON payload"
    }

    return ResponseEntity
      .status(BAD_REQUEST)
      .contentType(MediaType.TEXT_PLAIN)
      .body("Invalid request: $message")
  }
}
