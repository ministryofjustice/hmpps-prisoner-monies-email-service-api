package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.integration.callback

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.mockito.kotlin.verifyNoInteractions
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.integration.IntegrationTestBase

import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.NotifyAppInsightsLogger
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyCallbackRequest
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyEmailCallbackRequest
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyTextCallbackRequest


class CallbackControllerTests : IntegrationTestBase() {

  @MockitoBean
  private lateinit var notifyAppInsightsLogger: NotifyAppInsightsLogger

  @Test
  fun `Callback endpoint returns 204 No Content for valid delivery receipts`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .bodyValue(validDeliveryReceiptPayload)
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    val captor = argumentCaptor<NotifyCallbackRequest>()
    verify(notifyAppInsightsLogger).emit(captor.capture())

    val callback = assertInstanceOf<NotifyEmailCallbackRequest>(captor.firstValue)
    assertEquals("transaction-111112", callback.reference)
    assertEquals("delivered", callback.status)
    assertEquals("user@outside.local", callback.to)
  }

  @Test
  fun `Callback endpoint returns 204 No Content for valid text messages`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .bodyValue(validReceivedTextMessagePayload)
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    val captor = argumentCaptor<NotifyCallbackRequest>()
    verify(notifyAppInsightsLogger).emit(captor.capture())

    val callback = assertInstanceOf<NotifyTextCallbackRequest>(captor.firstValue)
    assertEquals("07000000000", callback.sourceNumber)
    assertEquals("07000000001", callback.destinationNumber)
    assertEquals("How do I sign in?", callback.message)
  }

  @Test
  fun `invalid json payload is rejected`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .bodyValue(""""id=11111-22222"""")
      .exchange()
      .expectStatus().isBadRequest

    verifyNoInteractions(notifyAppInsightsLogger)
  }

  @Test
  fun `unknown callback payload is rejected`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .bodyValue(
        """
          {
            "id": "11111111-1111-1111-1111-111111111111"
          }
        """.trimIndent(),
      )
      .exchange()
      .expectStatus().isBadRequest

    verifyNoInteractions(notifyAppInsightsLogger)
  }

  @Test
  fun `Callback with invalid typed field is rejected`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .bodyValue(
        """
          {
            "id": "11111111-1111-1111-1111-111111111111",
            "created_at": "2021-09-03T12:15:30.000000Z",
            "sent_at": "2021-09-03T12:15:31.000000Z",
            "completed_at": "2021-09-03T12:15:32.000000Z",
            "notification_type": "email",
            "template_id": "not-a-uuid",
            "template_version": 1,
            "reference": "transaction-111112",
            "to": "user@outside.local",
            "status": "delivered"
          }
        """.trimIndent(),
      )
      .exchange()
      .expectStatus().isBadRequest

    verifyNoInteractions(notifyAppInsightsLogger)
  }

  @Test
  fun `invalid content type is rejected`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.TEXT_PLAIN)
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .bodyValue(validDeliveryReceiptPayload)
      .exchange()
      .expectStatus().isBadRequest

    verifyNoInteractions(notifyAppInsightsLogger)
  }

  @Test
  fun `get is not allowed`() {
    webTestClient.get()
      .uri("/notify-callbacks")
      .headers(setAuthorisation(roles = listOf("ROLE_NOTIFY")))
      .exchange()
      .expectStatus().isEqualTo(405)

    verifyNoInteractions(notifyAppInsightsLogger)
  }

  @Test
  fun `request without notify role is forbidden`() {
    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .bodyValue(validDeliveryReceiptPayload)
      .exchange()
      .expectStatus().isForbidden

    verifyNoInteractions(notifyAppInsightsLogger)
  }



  @LocalServerPort
  private var port: Int = 0

  @Test
  fun `request over http is rejected`() {
    val webClient = WebTestClient.bindToServer()
      .baseUrl("http://localhost:$port")
      .build()

    webClient.post()
      .uri("/notify-callbacks")
      .headers ( setAuthorisation(roles = listOf("ROLE_NOTIFY")) )
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(validReceivedTextMessagePayload)
      .exchange()
      .expectStatus().isNoContent
  }

  val validReceivedTextMessagePayload =
      // representative payload as at 2021-09-07
      """
          {
      "id": "11111111-1111-1111-1111-111111111112",
      "date_received": "2021-09-03T12:15:30.000000Z",
      "source_number": "07000000000",
      "destination_number": "07000000001",
      "message": "How do I sign in?"
          }
        """.trimIndent()

    val validDeliveryReceiptPayload =
        """
          {
            "id": "11111111-1111-1111-1111-111111111111",
            "created_at": "2021-09-03T12:15:30.000000Z",
            "sent_at": "2021-09-03T12:15:31.000000Z",
            "completed_at": "2021-09-03T12:15:32.000000Z",
            "notification_type": "email",
            "template_id": "22222222-2222-2222-2222-222222222222",
            "template_version": 1,
            "reference": "transaction-111112",
            "to": "user@outside.local",
            "status": "delivered"
          }
        """.trimIndent()

}
