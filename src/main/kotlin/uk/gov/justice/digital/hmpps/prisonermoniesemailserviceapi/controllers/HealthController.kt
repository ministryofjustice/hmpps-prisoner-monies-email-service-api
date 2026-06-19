package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.controllers

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

  @PreAuthorize("hasRole('ROLE_PRISONER_MONIES_EMAIL_SERVICE_API__READ')")
  @GetMapping("/health")
  fun health() = mapOf("status" to "UP")
}
