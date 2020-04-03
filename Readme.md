# HttpLogMonitor
Http log monitor is application that can analyze http traffic from log file   

## Description of modules
Project contains two modules:
1. monitor - application that can analyze http traffic
2. traffic-simulator - application that simulate http traffic writing it on a tail of the file

## Installation
Download and configure:
1. [Maven 3.6](https://maven.apache.org/download.cgi)
2. [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

Use `mvn` in the root of the project build it:

```mvn clean package```

If you don't want to build a project you can find the _httpMonitor.jar_ and _simulator.jar_ in the root of the project

### How to run monitor

To run monitor use command: 

```java -jar monitor/target/httpMonitor.jar```

Use `java -jar monitor/target/httpMonitor.jar -h` to find overridable parameters
 
### How to run traffic-simulator
To run traffic-simulator use command:

`java -jar traffic-simulator/target/simulator.jar`

Use `java -jar traffic-simulator/target/simulator.jar -h` to find overridable parameters


## Architecture of http monitor
![Alt text](diagram.png "")

The central entity in the system in `EventBus`, which is used for asynchronous communication between different parts of the system.

### Description main classes

TailFileListener - listen changes from file and publish `AccessLogLineEvent` into event queue

AccessLogParser - Fetch parameters from common log line and publish `AccessLogEvent`

TrafficCalculator - Listen and accumulate `AccessLogEvent`. Based on `ticInterval` calculate traffic parameters and publish them into event bus

AlertCalculator - Listen and accumulate `TrafficStatisticEvents`. Based on `alertInterval`, trigger event for activating or hiding an event.

DashboardCalculator - Listen `AccessLogEvent` and `TrafficStatisticEvents` for updating UI

UI - Swing based application


## What can be improved
1. I would implement own query language for alerts that will give us the opportunity to define any alert. Look how [Promql](https://github.com/prometheus/prometheus/tree/master/promql) or [Flux](https://github.com/influxdata/flux) works.  

2. I would rewrite the UI of application in more beautiful and informative way

3. Access log parse can be rewritten to sequential version which will work faster then current regex version.

4. This application is not scalable, because we store all the data in memory and event bus use one thread to process data, it is not fault tolerance, there is no partitioning by topic event. We can use Redis cluster as cache to store most requested data and historical data we can store on the disk.
We can replace current event-bus with Kafka. Also we can use microservice architecture to decouple the application into different services. For example alert service, real time service, historical data       

5. There is no efficient way to store time series data. To solve this problem we can use [Prometheus](https://github.com/prometheus/prometheus) or [InfluxDb](https://github.com/influxdata/influxdb)   