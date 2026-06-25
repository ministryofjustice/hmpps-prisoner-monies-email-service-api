package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.unit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.integration.IntegrationTestBase


class CallbackControllerTests : IntegrationTestBase() {

@Test
    fun `Callback endpoint returns 204 No Content`() {


      val objectMapper = jacksonObjectMapper()
        // representative payload as at 2021-09-07

      val validDeliveryReceiptPayload = objectMapper.readTree(
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
      )

//      valid_received_text_message_payload = {
//        // unconfirmed payload, based on GOV.UK Notify documentation
//        'id': '11111111-1111-1111-1111-111111111112',
//        'date_received': '2021-09-03T12:15:30.000000Z',
//        'source_number': '07000000000', 'destination_number': '07000000001',
//        'message': 'How do I sign in?',
//    }

    webTestClient.post()
      .uri("/notify-callbacks")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_PRISONER_MONIES_EMAIL_SERVICE_API__READ" )))
      .bodyValue(validDeliveryReceiptPayload)
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    }
}










