package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.module.SimpleModule
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyCallbackDeserializer
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto.NotifyCallbackRequest

@Configuration
class JacksonConfig {

  @Bean
  fun notifyCallbackModule(): SimpleModule = SimpleModule().apply {
    addDeserializer(NotifyCallbackRequest::class.java, NotifyCallbackDeserializer())
  }
}
