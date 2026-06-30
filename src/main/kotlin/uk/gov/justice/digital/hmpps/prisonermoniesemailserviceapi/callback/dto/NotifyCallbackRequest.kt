package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto

import java.util.UUID

sealed class NotifyCallbackRequest {
  abstract val id: UUID
}
