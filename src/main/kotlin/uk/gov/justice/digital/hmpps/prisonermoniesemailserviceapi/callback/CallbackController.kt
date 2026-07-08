package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyCallbackRequest

@RestController
class CallbackController(private val transmitter: NotifyAppInsightsLogger) {

  @PostMapping("/notify-callbacks")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('NOTIFY')")
  fun handleCallback(@RequestBody callback: NotifyCallbackRequest) {
    transmitter.emit(callback)
  }
}

@GetMapping("/alexTest")
fun alexTest() {
  val alex = "cool"
  val i_hope = "this is caught in review"
  ResponseEntity.ok("ok")
}
