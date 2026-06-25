package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.controllers
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CallbackController {

  @PostMapping("/notify-callbacks")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('NOTIFY')")
  fun handleEmailCallback() {

  }
}