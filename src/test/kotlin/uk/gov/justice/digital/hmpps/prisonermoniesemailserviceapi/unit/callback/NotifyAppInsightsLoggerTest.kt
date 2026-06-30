package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.unit.callback

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.NotifyAppInsightsLogger
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyEmailCallbackRequest
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyTextCallbackRequest
import java.time.OffsetDateTime
import java.util.UUID

@ExtendWith(OutputCaptureExtension::class)
class NotifyAppInsightsLoggerTest {

  private val logger = NotifyAppInsightsLogger()

  @Test
  fun `emits delivery receipt log line`(output: CapturedOutput) {
    logger.emit(
      NotifyEmailCallbackRequest(
        id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
        createdAt = OffsetDateTime.parse("2021-09-03T12:15:30Z"),
        sentAt = OffsetDateTime.parse("2021-09-03T12:15:31Z"),
        completedAt = OffsetDateTime.parse("2021-09-03T12:15:32Z"),
        notificationType = "email",
        templateId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
        templateVersion = 1,
        reference = "transaction-111112",
        to = "user@outside.local",
        status = "delivered",
      ),
    )

    assertThat(output.out).contains(
      "GOV.UK Notify delivery receipt 11111111-1111-1111-1111-111111111111 status=delivered ref=transaction-111112",
    )
  }

  @Test
  fun `emits received text log line`(output: CapturedOutput) {
    logger.emit(
      NotifyTextCallbackRequest(
        id = UUID.fromString("11111111-1111-1111-1111-111111111112"),
        dateReceived = OffsetDateTime.parse("2021-09-03T12:15:30Z"),
        sourceNumber = "07000000000",
        destinationNumber = "07000000001",
        message = "How do I sign in?",
      ),
    )

    assertThat(output.out).contains(
      "GOV.UK Notify received text message 11111111-1111-1111-1111-111111111112 source_number=07000000000 message=How do I sign in?",
    )
  }
}