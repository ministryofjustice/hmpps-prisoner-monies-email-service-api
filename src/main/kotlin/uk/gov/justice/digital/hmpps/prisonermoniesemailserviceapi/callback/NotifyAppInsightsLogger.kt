package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyCallbackRequest
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyEmailCallbackRequest
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyTextCallbackRequest

@Service
class NotifyAppInsightsLogger {

  private val log = LoggerFactory.getLogger("mtp")

  fun emit(callbackRequest: NotifyCallbackRequest) {
    when (callbackRequest) {
      is NotifyEmailCallbackRequest -> {
        log.info(
          "GOV.UK Notify delivery receipt {} status={} ref={}",
          callbackRequest.id,
          callbackRequest.status,
          callbackRequest.reference,
        )
      }
      is NotifyTextCallbackRequest -> {
        log.info(
          "GOV.UK Notify received text message {} source_number={} message={}",
          callbackRequest.id,
          callbackRequest.sourceNumber,
          callbackRequest.message,
        )
      }
    }
  }
}