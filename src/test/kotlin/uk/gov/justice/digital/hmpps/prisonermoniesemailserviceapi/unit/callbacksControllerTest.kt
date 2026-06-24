import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(CallbackController::class)
class CallbackControllerTests(@Autowired val mockMvc: MockMvc) {    

@Test
 
    fun `Callback endpoint returns 204 No Content`() {


    valid_delivery_receipt_payload = {
        # representative payload as at 2021-09-07
        'id': '11111111-1111-1111-1111-111111111111',
        'created_at': '2021-09-03T12:15:30.000000Z',
        'sent_at': '2021-09-03T12:15:31.000000Z',
        'completed_at': '2021-09-03T12:15:32.000000Z',
        'notification_type': 'email',
        'template_id': '22222222-2222-2222-2222-222222222222', 'template_version': 1,
        'reference': 'transaction-111112',
        'to': 'user@outside.local',
        'status': 'delivered',
    }
    valid_received_text_message_payload = {
        # unconfirmed payload, based on GOV.UK Notify documentation
        'id': '11111111-1111-1111-1111-111111111112',
        'date_received': '2021-09-03T12:15:30.000000Z',
        'source_number': '07000000000', 'destination_number': '07000000001',
        'message': 'How do I sign in?',
    }

    mockMvc.perform(post("/notify-callbacks/").accept(MediaType.APPLICATION_JSON).content("""
            {
                "url": "",
                "payload": ${valid_delivery_receipt_payload}
            }"""))
            .andExpect(status().isNoContent)
            .andExpect(content().string(""))        

    }
}










