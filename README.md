# EventLogger
### A simple multi-process event logger

This project is meant to be used as a library in other projects where the developer wants to record events
Its main difference with respect to a logging library is that events have time duration and data (text) associated.
And it's possible to have the difference between the data at the start of the event and the data at the end as
a similar output as `diff`.

Another difference is that this library allows to have different processes to log events on a same timeline.
This is done by using RMI (Remote Method Invocation).

## Usage


Either run this class with the `--server` argument, or just use a client (EventLoggerClient) which will start a new server if one is not found
Queries that a client can make to the server are as follows:

 * EVENT_START_STOP: Start or stop an event with or without associated data
 * EVENT_QUERY: Query the server the information of an event with a given name (it should be checked beforehand if an event with that name exists)
 * EVENTS_QUERY: Query the server the information of all current events
 * EVENT_NAME_CHECK: Query the server if an event with a specific name exists
 * EVENT_NAMES: Query the server about the names of all current events
 * STOP_SERVER: Stops the server, this must be done only once, no more queries can be sent to the server after this
 * ERROR: An error occurred on a particular query

## Properties and configuration

This library uses `-D<key>=<value>` arguments to obtain custom configuration values, available configurations are:

 * __event_logger.naming_convention__ Naming convention used for event names. Possible values are [AS_IS, LOWERCASE, UPPERCASE]. Default value is AS_IS
 * __event_logger.properties.use_default_on_invalid_value__ If a property's value is not valid, the default value will be used instead. Default value is true
 * __event_logger.rmi.client.connection.retries__ How many retries will be made to connect to the server, default value is 5
 * __event_logger.rmi.client.connection.retry_delay__ Delay in seconds to retry a connection to the server, default value is 2s
 * __event_logger.rmi.port__ Default port number used for RMI exported remote objects, default is 0 (a 0 value means that a random port will be used)
 * __event_logger.rmi.url__ The URL to the server, default value is 127.0.0.1
 * __event_logger.rmi_registry.port__ Default port number used for the RMI registry, default is 1099