package uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.dto

import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.deser.std.StdDeserializer
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.exception.InvalidJsonPayloadException
import uk.gov.justice.digital.hmpps.prisonermoniesemailserviceapi.callback.exception.UnknownCallbackTypeException

class NotifyCallbackDeserializer : StdDeserializer<NotifyCallbackRequest>(NotifyCallbackRequest::class.java) {

  override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): NotifyCallbackRequest {
    val node = ctxt.readTree(parser) as JsonNode

    if (!node.isObject) {
      throw InvalidJsonPayloadException()
    }

    return when {
      hasAll(node, RECEIVED_TEXT_MESSAGE_FIELDS) ->
        ctxt.readTreeAsValue(node, NotifyTextCallbackRequest::class.java)

      hasAll(node, DELIVERY_RECEIPT_FIELDS) ->
        ctxt.readTreeAsValue(node, NotifyEmailCallbackRequest::class.java)

      else -> throw UnknownCallbackTypeException()
    }
  }

  private fun hasAll(node: JsonNode, fields: Set<String>): Boolean =
    fields.all(node::has)

  private companion object {
    val DELIVERY_RECEIPT_FIELDS = setOf(
      "id",
      "reference",
      "to",
      "status",
      "notification_type",
      "sent_at",
      "template_id",
    )

    val RECEIVED_TEXT_MESSAGE_FIELDS = setOf(
      "id",
      "source_number",
      "destination_number",
      "message",
      "date_received",
    )
  }
}
