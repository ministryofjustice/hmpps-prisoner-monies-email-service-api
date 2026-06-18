package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrisonerMoniesEmailServiceApi

fun main(args: Array<String>) {
  runApplication<PrisonerMoniesEmailServiceApi>(*args)
}
