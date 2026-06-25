package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.controllers
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CallbackController {

  @PreAuthorize("hasRole('ROLE_PRISONER_MONIES_EMAIL_SERVICE_API__READ')")
  @PostMapping("/notify-callbacks")
  fun handleEmailCallback() {
    val harriet = "cool"
    return
  }
}