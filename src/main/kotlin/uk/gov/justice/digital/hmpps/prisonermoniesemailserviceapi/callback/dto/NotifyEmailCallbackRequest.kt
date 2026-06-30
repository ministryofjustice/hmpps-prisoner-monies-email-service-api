package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming
import java.time.OffsetDateTime
import java.util.UUID

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NotifyEmailCallbackRequest(
  override val id: UUID,
  val createdAt: OffsetDateTime,
  val sentAt: OffsetDateTime,
  val completedAt: OffsetDateTime,
  val notificationType: String,
  val templateId: UUID,
  val templateVersion: Int,
  val reference: String,
  val to: String,
  val status: String,
) : NotifyCallbackRequest()
