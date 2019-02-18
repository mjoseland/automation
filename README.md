A single-user, general-purpose application with a focus on automating the day-to-day burden of seeking and filtering
information. Built in a microservices architecture with docker image builds available for all services.

Feature-specific readme files with API documentation and build/deploy instructions will be written once a basic level
of functionality has been achieved.

In this repository, Git branching is used to isolate the resources for individual services.

# Scope
#### Features (Complete)
* 

#### Features (Planned)
* Highly configurable multi-service events
* API call scheduling for internal services
* Data source checks with tests and conditional handlers
* Notifications with configurable transmission method (email only for MVP)
* Calendar API interaction (long term)
* Front end for all configuration operations (long term)

#### Use Cases
* Notifications for online store pricing updates or sales
* Machine uptime monitoring
* Blog post notifications
* Upcoming digital media
* Weather alerts
* Centralised Anki (long term)

# Services
#### Scheduler
**Primary Branch:** DataApi-SchedulerConfig<br>
**Singleton:** Y<br>
**Stateful:** Y

When a scheduled request is triggered:
1. Config for an internal (to the application) JSON-body HTTP request is retrieved from the request repository.
2. The request is assembled and sent.
3. The response so step 2 is stored as part of the scheduled request's short term history.

Scheduled request item config can be subject to CRUD operations at any time through a data API. Any changes immediately 
start, stop, or modify the scheduled request that corresponds to the config. Short term scheduled request history is 
available as GET call to the associated config.

TODO:
* Scheduled request manager for high-level implementation of steps 1-3
* Scheduled request start, stop, modify automatic triggering on config CRUD using the manager
* Interval scheduling as a number of milliseconds
* Document API on branch readme

#### Request Repository
**Primary Branch:** DataApi-RequestRepository<br>
**Singleton:** N<br>
**Stateful:** Y

* Internal (to the application) request config data API. An internal request is associated with a HTTP method, service 
  ID, resource ID, and JSON body (optional).
* External request config data API. An external request stores a HTTP method, URI, and a String body (optional).
* Document API on branch readme

#### Text Source Checker
**Primary Branch:** N/A<br>
**Singleton:** N<br>
**Stateful:** Y

1. Retrieves an internal or external HTTP request from the request repository
2. Assembles and sends the request
3. Performs a configurable test and optional associated translation to a JSON object on the response
4. If the test passes, sends the JSON reduction to a configurable internal service

TODO:
* Everything

#### Notifier
**Primary Branch:** N/A<br>
**Singleton:** N<br>
**Stateful:** Y

Notifications with configurable transmission method. Available transmission methods will be limited to email only in 
the medium term. 

**Notifier Type:** Email<br>
* Configurable fields are email address, subject prefix

TODO:
* Everything
