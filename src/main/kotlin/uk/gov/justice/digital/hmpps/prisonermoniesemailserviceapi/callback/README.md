# Notify callbacks

This service exposes a single callback endpoint for <External>GOV.UK Notify</External> delivery receipts and received text messages.

## What the endpoint does

`POST /notify-callbacks`

The endpoint accepts one of two callback payload shapes:

- delivery receipts for email notifications
- received text messages

If the payload is valid, the endpoint returns `204 No Content` and passes the callback to `NotifyAppInsightsLogger`, which logs the callback in the same way as the legacy Django service.

## How the request is processed

The request flow is:

1. `CallbackController` receives the `POST /notify-callbacks` request.
2. Spring uses Jackson to bind the request body to `NotifyCallbackRequest`.
3. A custom Jackson module registers `NotifyCallbackDeserializer` for `NotifyCallbackRequest`.
4. `NotifyCallbackDeserializer` inspects the incoming JSON and decides which concrete request type to create:
    - `NotifyEmailCallbackRequest`
    - `NotifyTextCallbackRequest`
5. The controller passes the typed callback object to `NotifyAppInsightsLogger.emit(...)`.
6. The endpoint returns `204 No Content`.

## DTOs

`NotifyCallbackRequest` is the sealed parent type for callback requests.

There are two concrete request DTOs:

- `NotifyEmailCallbackRequest`
- `NotifyTextCallbackRequest`

These DTOs are strongly typed and use Jackson snake case mapping so that Notify JSON fields such as `created_at` and `date_received` map cleanly onto Kotlin properties.

## Deserialisation

`NotifyCallbackDeserializer` contains the callback shape detection logic.

It checks for the distinguishing fields used by the legacy Django implementation:

- email delivery receipt fields
- received text message fields

If the payload is:

- not a JSON object, it throws `InvalidJsonPayloadException`
- a JSON object but not a recognised callback shape, it throws `UnknownCallbackTypeException`
- a recognised callback shape with invalid typed data, Jackson throws its normal format exceptions during DTO binding

The deserialiser itself is registered in `JacksonConfig` using a `SimpleModule`, rather than annotating the sealed parent type directly. This avoids recursive subtype deserialisation issues.

## Logging

`NotifyAppInsightsLogger` is responsible for emitting the callback data.

For parity with the Django service, it logs:

- delivery receipts as `GOV.UK Notify delivery receipt <id> status=<status> ref=<reference>`
- received text messages as `GOV.UK Notify received text message <id> source_number=<source_number> message=<message>`

The logger name is `mtp`, matching the legacy application.

## Error handling

There are two layers of exception handling:

- `PrisonerMoniesCallbackExceptionHandler` handles callback-specific request failures and returns plain text responses to match the legacy Django behaviour
- `PrisonerMoniesEmailServiceApiExceptionHandler` handles the rest of the application using the standard HMPPS JSON error response format

For callback failures we currently return:

- `400 Bad Request` with `Invalid request: Invalid content type`
- `400 Bad Request` with `Invalid request: Invalid JSON payload`
- `400 Bad Request` with `Invalid request: JSON payload is not a known callback type`
- `405 Method Not Allowed` for unsupported methods

This keeps the callback endpoint behaviour aligned with the legacy implementation while leaving the wider service on the template standard.

## Tests

The callback implementation is covered at two levels.

### Integration tests

The integration tests use `WebTestClient` and prove the black-box HTTP behaviour:

- valid delivery receipt returns `204`
- valid text message returns `204`
- malformed or invalid callback payloads return `400`
- wrong content type returns `400`
- unsupported methods return `405`
- requests without the required role return `403`

These tests also verify that valid callbacks reach `NotifyAppInsightsLogger` and that invalid callbacks do not.

### Unit tests

The unit tests focus on the callback payload discrimination logic and callback-specific exception handling without booting the full Spring application context.

## Design notes

This implementation is intentionally conservative.

The goal of this phase is lift-and-shift parity with the existing Django service, not redesign. The main differences are internal only:

- Kotlin uses typed DTOs rather than passing raw dictionaries around
- Spring and Jackson handle request binding rather than manual JSON parsing in the view
- callback-specific exception handling is separated from the global HMPPS template exception handling

Externally, the endpoint is intended to behave as a black box replacement for the legacy service.
