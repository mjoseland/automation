A collection of interoperable services with a focus on highly configurable automated information retrieval and
processing.
Service per branch with docker image builds available.

# Scope

#### Planned Features
* Task scheduling
* Remotely configurable API call control flows
* Notifications and commands (long term) via signal
* Web GUI (long term)

#### Example Use Cases
* Web resource history with change notifications
* Machine or resource availability monitoring
* Conditional weather alerts

## Web Services

#### Eureka Service Registry
**Primary Branch:** EurekaServiceRegistry<br>
* Service registration and discovery
* Discovery typically only utilised by the [request repository](#request-repository)

#### Groovy Script Repository
**Primary Branch:** N/A<br>
* Groovy script CRUD
* Supports remote calls to groovy scripts 
* Restricted to input and output as JSON

#### Regex Repository
**Primary Branch:** N/A<br>
* Regex CRUD
* API supports matching for supplied input
* Match results as 2D array (row per match, column per group)

<a name="request-repository"></a>
#### Request Repository
**Primary Branch:** DataApi-RequestRepository<br>

* HTTP request CRUD
* An internal (to the application) request is stores an HTTP method, service ID, resource path, array of header
  fields, and JSON body (nullable).
* An external request stores an HTTP method, URL, array of header fields, and a text body (nullable).
* Both internal and external requests available in "assembled" form, which can easily be used by clients to construct
  an HTTP request

#### Scheduler
**Primary Branch:** DataApi-SchedulerConfig<br>

When a scheduled request is triggered:
1. Config for an HTTP request is retrieved from the request repository
2. The request is assembled and sent
3. The response to step 2 is ignored and discarded, excepting logging

Scheduled request item config can be subject to CRUD operations at any time through a data API.
All immediately update the currently running schedule.

#### Sequencer
**Primary Branch:** N/A<br>

* API call control flows CRUD and execution
* Branching/chaining as chosen [request repository](#request-repository) item for next call
* Branching dependent on response code to previous request
* Can send as body one of:
  * the (nullable) body stored against the request in the request repository
  * the body of the response to the previous request

<a name="signal-message-sender"></a>
#### Signal Message Sender
**Primary Branch:** N/A<br>

* Account per service instance
* Handles requests to send messages with supplied username and string

#### String History Repository
**Primary Branch:** N/A<br>

* Stores string version history
* Handles requests containing a current value of a string
* Logs the entry and the string's value, if it doesn't match the prior entry
* Optional [signal notifications](#signal-message-sender) when updates occur

#### Template Repository
**Primary Branch:** N/A<br>

* Text template CRUD
* Supports generating text from a template for supplied JSON input
