/**
 * = Metrics
 *
 * This project implements the Vert.x Metrics Service Provider Interface (SPI) reporting metrics to the
 * https://github.com/dropwizard/metrics[Dropwizard metrics] library.
 *
 * == Features
 *
 * A fairly simple API to retrieve metrics via the {@link io.vertx.core.metrics.Measured Measured}
 * interface which is implemented by various Vert.x components like {@link io.vertx.core.http.HttpServer HttpServer},
 * {@link io.vertx.core.net.NetServer}, and even {@link io.vertx.core.Vertx Vertx} itself.
 *
 * Confiugrable JMX reporting based on Dropwizard implementation, exposing Vert.x as JMX MBeans.
 *
 * == Getting started
 *
 * To enable metrics, add the following dependency to the _dependencies_ section of your build descriptor:
 *
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>${maven.groupId}</groupId>
 *   <artifactId>${maven.artifactId}</artifactId>
 *   <version>${maven.version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * compile '${maven.groupId}:${maven.artifactId}:${maven.version}'
 * ----
 *
 * Then when you create vertx enable metrics using the {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions}:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setup()}
 * ----
 *
 * You can also enable JMX:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setupJMX()}
 * ----
 *
 * To see details about JMX see the <<jmx>> section at the bottom.
 *
 * == Command line activation
 *
 * When running Vert.x from the command line interface, metrics can be activated via JVM system properties. System
 * properties beginning with _vertx.metrics.options._ are transmitted to the metrics options.
 *
 * The _vertx.metrics.options.enabled_ is a standard Vert.x Core option for enabling the metrics implementations, this
 * options must be set to `true`:
 *
 * ----
 * java -jar your-fat-jar -Dvertx.metrics.options.enabled=true
 * ----
 *
 * The {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions#setRegistryName(java.lang.String) vertx.metrics.options.registryName}
 * configures the <<dropwizard-registry,Dropwizard Registry>> to use:
 *
 * ----
 * java -jar your-fat-jar -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.registryName=my-registry
 * ----
 *
 * The {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions#setJmxEnabled(boolean) vertx.metrics.options.jmxEnabled} and
 * {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions#setJmxDomain(java.lang.String) vertx.metrics.options.jmxDomain}
 * configures the <<jmx,JMX>> registration:
 *
 * ----
 * java -jar your-fat-jar -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.jmxEnabled=true ...
 * ----
 *
 * The {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions#setConfigPath(java.lang.String) vertx.metrics.options.configPath}
 * option allows to reconfigure the metrics from a property file.
 *
 * == Metrics service
 *
 * While Vert.x core defines an SPI for reporting metrics (implemented for instance in this project), it does not define
 * an API for retrieving metrics (because some metrics collectors just do reporting and nothing more).
 *
 * The {@link io.vertx.ext.dropwizard.MetricsService} provides an API in front of the Dropwizard Registry to get
 * metrics data snapshots.
 *
 * === Naming
 *
 * Each measured component listed below (except for Vertx) will have a base name associated with it. Each metric
 * can be retrieved by providing the fully qualified name <fqn> `baseName` + `.` + `metricName` from Vertx:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#naming1}
 * ----
 *
 * or from the measured component itself using just the metric name:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#naming2}
 * ----
 *
 * See more examples below on how to retrieve/use metrics for a specific component.
 *
 * Metrics names can also be listed:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#naming3}
 * ----
 *
 * === Retrieving metrics
 *
 * Once enabled, the {@link io.vertx.ext.dropwizard.MetricsService} allows to retrieve metrics snapshots from any
 * {@link io.vertx.core.metrics.Measured Measured} object which provides a map of the metric name to the data,
 * represented by a {@link io.vertx.core.json.JsonObject}. So for example if we were to print out all metrics
 * for a particular Vert.x instance:
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#example1}
 * ----
 *
 * NOTE: For details on the actual contents of the data (the actual metric) represented by the {@link io.vertx.core.json.JsonObject}
 * consult the implementation documentation like https://github.com/vert-x3/vertx-metrics[vertx-metrics]
 *
 * Often it is desired that you only want to capture specific metrics for a particular component, like an http server
 * without having to know the details of the naming scheme of every metric (something which is left to the implementers of the SPI).
 *
 * Since {@link io.vertx.core.http.HttpServer HttpServer} implements {@link io.vertx.core.metrics.Measured}, you can easily grab all metrics
 * that are specific for that particular http server.
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#example2}
 * ----
 *
 * Metrics can also be retrieved using a base name:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#example3}
 * ----
 *
 * == Data
 *
 * Below is how each dropwizard metric is represented in JSON. Please refer to the
 * https://github.com/dropwizard/metrics[Dropwizard metrics] documentation for detailed information on each metric.
 *
 * [[gauge]]
 * === Gauge
 *
 * [source,javascript]
 * ----
 * {
 *   "type"  : "gauge",
 *   "value" : value // any json value
 * }
 * ----
 *
 * [[counter]]
 * === Counter
 *
 * [source,$lang]
 * ----
 * {
 *   "type"  : "counter",
 *   "count" : 1 // number
 * }
 * ----
 *
 * [[histogram]]
 * === Histogram
 *
 * [source,javascript]
 * ----
 * {
 *   "type"   : "histogram",
 *   "count"  : 1 // long
 *   "min"    : 1 // long
 *   "max"    : 1 // long
 *   "mean"   : 1.0 // double
 *   "stddev" : 1.0 // double
 *   "median" : 1.0 // double
 *   "75%"    : 1.0 // double
 *   "95%"    : 1.0 // double
 *   "98%"    : 1.0 // double
 *   "99%"    : 1.0 // double
 *   "99.9%"  : 1.0 // double
 * }
 * ----
 *
 * [[meter]]
 * === Meter
 *
 * [source,$lang]
 * ----
 * {
 *   "type"              : "meter",
 *   "count"             : 1 // long
 *   "meanRate"          : 1.0 // double
 *   "oneMinuteRate"     : 1.0 // double
 *   "fiveMinuteRate"    : 1.0 // double
 *   "fifteenMinuteRate" : 1.0 // double
 *   "rate"              : "events/second" // string representing rate
 * }
 * ----
 *
 * [[throughput_meter]]
 * === ThroughputMeter
 *
 * Extends a <<meter>> to provide an instant throughput.
 *
 * [source,$lang]
 * ----
 * {
 *   "type"              : "meter",
 *   "count"             : 40 // long
 *   "meanRate"          : 2.0 // double
 *   "oneSecondRate"     : 3 // long - number of occurence for the last second
 *   "oneMinuteRate"     : 1.0 // double
 *   "fiveMinuteRate"    : 1.0 // double
 *   "fifteenMinuteRate" : 1.0 // double
 *   "rate"              : "events/second" // string representing rate
 * }
 * ----
 *
 * [[timer]]
 * === Timer
 *
 * A timer is basically a combination of Histogram + Meter.
 *
 * [source,$lang]
 * ----
 * {
 *   "type": "timer",
 *
 *   // histogram data
 *   "count"  : 1 // long
 *   "min"    : 1 // long
 *   "max"    : 1 // long
 *   "mean"   : 1.0 // double
 *   "stddev" : 1.0 // double
 *   "median" : 1.0 // double
 *   "75%"    : 1.0 // double
 *   "95%"    : 1.0 // double
 *   "98%"    : 1.0 // double
 *   "99%"    : 1.0 // double
 *   "99.9%"  : 1.0 // double
 *
 *   // meter data
 *   "meanRate"          : 1.0 // double
 *   "oneMinuteRate"     : 1.0 // double
 *   "fiveMinuteRate"    : 1.0 // double
 *   "fifteenMinuteRate" : 1.0 // double
 *   "rate"              : "events/second" // string representing rate
 * }
 * ----
 *
 * [[throughput_timer]]
 * === Throughput Timer
 *
 * Extends a <<timer>> to provide an instant throughput metric.
 *
 * [source,$lang]
 * ----
 * {
 *   "type": "timer",
 *
 *   // histogram data
 *   "count"      : 1 // long
 *   "min"        : 1 // long
 *   "max"        : 1 // long
 *   "mean"       : 1.0 // double
 *   "stddev"     : 1.0 // double
 *   "median"     : 1.0 // double
 *   "75%"        : 1.0 // double
 *   "95%"        : 1.0 // double
 *   "98%"        : 1.0 // double
 *   "99%"        : 1.0 // double
 *   "99.9%"      : 1.0 // double
 *
 *   // meter data
 *   "meanRate"          : 1.0 // double
 *   "oneSecondRate"     : 3 // long - number of occurence for the last second
 *   "oneMinuteRate"     : 1.0 // double
 *   "fiveMinuteRate"    : 1.0 // double
 *   "fifteenMinuteRate" : 1.0 // double
 *   "rate"              : "events/second" // string representing rate
 * }
 * ----
 *
 * == The metrics
 *
 * The following metrics are currently provided.
 *
 * === Vert.x metrics
 *
 * The following metrics are provided:
 *
 * * `vertx.event-loop-size` - A <<gauge>> of the number of threads in the event loop pool
 * * `vertx.worker-pool-size` - A <<gauge>> of the number of threads in the worker pool
 * * `vertx.cluster-host` - A <<gauge>> of the cluster-host setting
 * * `vertx.cluster-port` - A <<gauge>> of the cluster-port setting
 * * `vertx.verticles` - A <<counter>> of the number of verticles currently deployed
 * * `vertx.verticles.<verticle-name>` - A <<counter>> of the number of deployment of a particular verticle
 *
 * === Event bus metrics
 *
 * Base name: `vertx.eventbus`
 *
 * * `handlers` - A <<counter>> of the number of event bus handlers
 * * `handlers.myaddress` - A <<timer>> representing the rate of which messages are being received for the _myaddress_ handler
 * * `messages.bytes-read` - A <<meter>> of the number of bytes read when receiving remote messages
 * * `messages.bytes-written` - A <<meter>> of the number of bytes written when sending remote messages
 * * `messages.pending` - A <<counter>> of the number of messages received but not yet processed by an handler
 * * `messages.pending-local` - A <<counter>> of the number of messages locally received but not yet processed by an handler
 * * `messages.pending-remote` - A <<counter>> of the number of messages remotely received but not yet processed by an handler
 * * `messages.received` - A <<throughput_meter>> representing the rate of which messages are being received
 * * `messages.received-local` - A <<throughput_meter>> representing the rate of which local messages are being received
 * * `messages.received-remote` - A <<throughput_meter>> representing the rate of which remote messages are being received
 * * `messages.delivered` - A <<throughpu_metert>> representing the rate of which messages are being delivered to an handler
 * * `messages.delivered-local` - A <<throughput_meter>> representing the rate of which local messages are being delivered to an handler
 * * `messages.delivered-remote` - A <<throughput_meter>> representing the rate of which remote messages are being delivered to an handler
 * * `messages.sent` - A <<throughput_metert>> representing the rate of which messages are being sent
 * * `messages.sent-local` - A <<throughput_meter>> representing the rate of which messages are being sent locally
 * * `messages.sent-remote` - A <<throughput_meter>> representing the rate of which messages are being sent remotely
 * * `messages.published` - A <<throughput_meter>> representing the rate of which messages are being published
 * * `messages.published-local` - A <<throughput_meter>> representing the rate of which messages are being published locally
 * * `messages.published-remote` - A <<throughput_meter>> representing the rate of which messages are being published remotely
 * * `messages.reply-failures` - A <<meter>> representing the rate of reply failures
 *
 * The monitored event bus handlers is configurable via a match performed on the handler registration address.
 * Vert.x can have potentially a huge amount of registered event bus, therefore the only good default for this
 * setting is to monitor zero handlers.
 *
 * The monitored handlers can be configured in the {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions} via
 * a specific address match or a regex match:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setupMonitoredHandlers()}
 * ----
 *
 * WARNING: if you use regex match, a wrong regex can potentially match a lot of handlers.
 *
 * [[http-server-metrics]]
 * === Http server metrics
 *
 * Base name: `vertx.http.servers.<host>:<port>`
 *
 * Http server includes all the metrics of a <<net-server-metrics,Net Server>> plus the following:
 *
 * * `requests` - A <<throughput_timer>> of a request and the rate of it's occurrence
 * * `<http-method>-requests` - A <<throughput_timer>> of a specific http method request and the rate of it's occurrence
 * ** Examples: `get-requests`, `post-requests`
 * * `<http-method>-requests./<uri>` - A <<throughput_timer>> of a specific http method & URI request and the rate of it's occurrence
 * ** Examples: `get-requests./some/uri`, `post-requests./some/uri?foo=bar`
 * * `responses-1xx` - A <<throughput_meter>> of the 1xx response code
 * * `responses-2xx` - A <<throughput_meter>> of the 2xx response code
 * * `responses-3xx` - A <<throughput_meter>> of the 3xx response code
 * * `responses-4xx` - A <<throughput_meter>> of the 4xx response code
 * * `responses-5xx` - A <<throughput_meter>> of the 5xx response code
 * * `open-websockets` - A <<counter>> of the number of open web socket connections
 * * `open-websockets.<remote-host>` - A <<counter>> of the number of open web socket connections for a particular remote host
 *
 * Http URI metrics must be explicitely configured in the options either by exact match or regex match:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setupMonitoredUris()}
 * ----
 *
 * *For `bytes-read` and `bytes-written` the bytes represent the body of the request/response, so headers, etc are ignored.*
 *
 * === Http client metrics
 *
 * Base name: `vertx.http.clients` (by default) or `vertx.http.clients.<id>` where `<id>` is a non empty string
 * configured by {@link io.vertx.core.http.HttpClientOptions#setMetricsName}.
 *
 * Http client includes all the metrics of a <<http-server-metrics,Http Server>> plus the following:
 *
 * * `connections.max-pool-size` - A <<gauge>> of the max connection pool size
 * * `connections.pool-ratio` - A ratio <<gauge>> of the open connections / max connection pool size
 * * `responses-1xx` - A <<meter>> of the 1xx response code
 * * `responses-2xx` - A <<meter>> of the 2xx response code
 * * `responses-3xx` - A <<meter>> of the 3xx response code
 * * `responses-4xx` - A <<meter>> of the 4xx response code
 * * `responses-5xx` - A <<meter>> of the 5xx response code
 *
 * The http client manages a pool of connection for each remote endpoint with a queue of pending requests
 *
 * Endpoint metrics are available too:
 *
 * * `endpoint.<host:port>.queue-delay` - A <<timer>> of the wait time of a pending request in the queue
 * * `endpoint.<host:port>.queue-size` - A <<counter>> of the actual queue size
 * * `endpoint.<host:port>.open-netsockets` - A <<counter>> of the actual number of open sockets to the endpoint
 * * `endpoint.<host:port>.usage` - A <<timer>> of the delay between the request starts and the response ends
 * * `endpoint.<host:port>.in-use` - A <<counter>> of the actual number of request/response
 * * `endpoint.<host:port>.ttfb` - A <<timer>> of the wait time between the request ended and its response begins
 *
 * where <host> is the endpoint host name possibly unresolved and <port> the TCP port.
 *
 * The monitored endpoints are configurable via a match performed on the server `$host:$port`.
 * The default for this setting is to monitor no endpoints.
 *
 * The monitored endpoints can be configured in the {@link io.vertx.ext.dropwizard.DropwizardMetricsOptions} via
 * a specific hostname match or a regex match:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setupMonitoredEndpoints()}
 * ----
 *
 * [[net-server-metrics]]
 * === Net server metrics
 *
 * Base name: `vertx.net.servers.<host>:<port>`
 *
 * * `open-netsockets` - A <<counter>> of the number of open net socket connections
 * * `open-netsockets.<remote-host>` - A <<counter>> of the number of open net socket connections for a particular remote host
 * * `connections` - A <<timer>> of a connection and the rate of it's occurrence
 * * `exceptions` - A <<counter>> of the number of exceptions
 * * `bytes-read` - A <<histogram>> of the number of bytes read.
 * * `bytes-written` - A <<histogram>> of the number of bytes written.
 *
 * === Net client metrics
 *
 * Base name: `vertx.net.clients` (by default) or `vertx.net.clients.<id>` where `<id>` is a non empty string
 * configured by {@link io.vertx.core.net.NetClientOptions#setMetricsName}.
 *
 * Net client includes all the metrics of a <<net-server-metrics,Net Server>>
 *
 * === Datagram socket metrics
 *
 * Base name: `vertx.datagram`
 *
 * * `sockets` - A <<counter>> of the number of datagram sockets
 * * `exceptions` - A <<counter>> of the number of exceptions
 * * `bytes-written` - A <<histogram>> of the number of bytes written.
 * * `<host>:<port>.bytes-read` - A <<histogram>> of the number of bytes read.
 * ** This metric will only be available if the datagram socket is listening
 *
 * === Pool metrics
 *
 * Base name: `vertx.pool.<type>.<name>` where `type` is the type of the pool (e.g _worker_, _datasource_) and
 * `name` is the name of the pool (e.g `vert.x-worker-thread`).
 *
 * Pools of type _worker_ are blocking worker pools. Vert.x exposes its worker as _vert.x-worker-thread_ and
 * _vert.x-internal-blocking_. Named worker executor created with {@link io.vertx.core.WorkerExecutor} are exposed.
 *
 * Datasource created with Vert.x JDBC clients are exposed as _datasource_.
 *
 * * `queue-delay` - A <<timer>> measuring the duration of the delay to obtain the resource, i.e the wait time in the queue
 * * `queue-size` - A <<counter>> of the actual number of waiters in the queue
 * * `usage` - A <<timer>> measuring the duration of the usage of the resource
 * * `in-use` - A <<count>> of the actual number of resources used
 * * `pool-ratio` - A ratio <<gauge>> of the in use resource / pool size
 * * `max-pool-size` - A <<gauge>> of the max pool size
 *
 * The `pool-ratio` and the `max_pool_size` won't be present when the measured pool's max pool size could not
 * be determined.
 *
 * [[jmx]]
 * == JMX
 *
 * JMX is disabled by default.
 *
 * If you want JMX, then you need to enabled that:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setupJMX()}
 * ----
 *
 * If running Vert.x from the command line you can enable metrics and JMX by uncommented the JMX_OPTS line in the
 * `vertx` or `vertx.bat` script:
 *
 * ----
 * JMX_OPTS="-Dcom.sun.management.jmxremote -Dvertx.options.jmxEnabled=true"
 * ----
 *
 * You can configure the domain under which the MBeans will be created:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setupJMXWithDomain()}
 * ----
 *
 * == Enabling remote JMX
 *
 * If you want the metrics to be exposed remotely over JMX, then you need to set, at minimum the following system property:
 *
 * `com.sun.management.jmxremote`
 *
 * If running from the command line this can be done by editing the `vertx` or `vertx.bat` and uncommenting the
 * `JMX_OPTS` line.
 *
 * Please see the http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html[Oracle JMX documentation] for more information on configuring JMX
 *
 * *If running Vert.x on a public server please be careful about exposing remote JMX access*
 *
 * [[dropwizard-registry]]
 * == Accessing Dropwizard Registry
 *
 * When configuring the metrics service, an optional registry name can be specified for registering the underlying
 * https://dropwizard.github.io/metrics/3.1.0/getting-started/#the-registry[Dropwizard Registry] in the
 * the https://dropwizard.github.io/metrics/3.1.0/apidocs/com/codahale/metrics/SharedMetricRegistries.html[Dropwizard Shared Registry]
 * so you can retrieve this registry and use according to your needs.
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#getRegistry()}}
 * ----
 *
 * == Using Jolokia and Hawtio
 *
 * https://jolokia.org/[Jolokia] is a JMX-HTTP bridge giving an alternative to JSR-160 connectors. It is an agent based
 * approach with support for many platforms. In addition to basic JMX operations it enhances JMX remoting with features
 * like bulk requests.
 *
 * http://hawt.io/[Hawtio] is a modular web console consuming the data exposed by Jolokia. It lets you create dashboards
 * and retrieve data from JMX such as memory, cpu, or any vert.x metrics.
 *
 * This section explains how to configure your vert.x application to retrieve the metrics in Hawtio.
 *
 * First, you need to configure your vert.x instance with the following options:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#example4()}
 * ----
 *
 * You can change the domain to whatever you want. The same configuration can be used for clustered Vert.x instances.
 * This configuration instructs vertx-dropwizard-metrics to expose the metrics in the local MBean server, so
 * Jolokia can retrieve them.
 *
 * Then you need, to _plug_ jolokia to expose the data. There are several ways to _plug_ jolokia. See
 * https://jolokia.org/reference/html/architecture.html[for further details]. Here, we explain how to use the
 * Jolokia agent with the default configuration. Refer to the https://jolokia.org/reference/html/[the jolokia
 * documentation] to configure it.
 *
 * The agent can either be attached when you start the application or attached on a running JVM (you would need
 * special permission to access the process). In the first case, launch you application using:
 *
 * [source]
 * ----
 * java -javaagent:/.../agents/jolokia-jvm.jar=port=7777,host=localhost -jar ...
 * ----
 *
 * The `-javaagent` specifies the path to the jolokia agent jar file. You can configure the port and host from the
 * command line. Here it registers the REST endpoint on `http://localhost:7777`.
 *
 * You can also attach the agent on a running JVM with:
 *
 * [source]
 * ----
 * java -jar jolokia-jvm.jar start PID
 * ----
 *
 * Replace `PID` with the process id of the JVM.
 *
 * Once Jolokia is configured and launched, you can consume the data from Hawtio.
 *
 * On Hawtio, enter the connection details as follows:
 *
 * image::../../images/hawtio-connect.png[]
 *
 * Then, you can go to the _JMX_ tab and you should find a _directory_ with the name you entered as JMX domain
 * in the Vert.x configuration:
 *
 * image::../../images/hawtio-jmx.png[]
 *
 * From this, you can configure your dashboard and retrieve any metric exposed by vert.x.
 *
 */
@ModuleGen(name = "vertx-dropwizard", groupPackage = "io.vertx")
@Document(fileName = "index.adoc")
package io.vertx.ext.dropwizard;

import io.vertx.codegen.annotations.ModuleGen;
import io.vertx.docgen.Document;
