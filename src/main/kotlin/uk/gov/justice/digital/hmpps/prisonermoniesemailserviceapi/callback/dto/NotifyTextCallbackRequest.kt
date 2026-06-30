
package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming
import java.time.OffsetDateTime
import java.util.UUID

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NotifyTextCallbackRequest(
  override val id: UUID,
  val dateReceived: OffsetDateTime,
  val sourceNumber: String,
  val destinationNumber: String,
  val message: String,
) : NotifyCallbackRequest()
