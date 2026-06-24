package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CallbackController {

  @PostMapping("/notify-callbacks")
  fun handleEmailCallback() {
    return
  }

}