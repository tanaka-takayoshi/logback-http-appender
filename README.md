# logback-http-appender



## How to use

```xml

<appender name="NewRelicLogs" class="jp.co.newrelikk.labs.HttpAppender">
    <contentType>application/json</contentType>
    <url>https://example.com/logs/v1</url>
    <headers>{"X-Insert-Key":"YourKeyHere"}</headers>
    <reconnectDelay>10</reconnectDelay>

    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <mdc/> <!-- MDC variables on the Thread will be written as JSON fields-->
            <context/> <!--Outputs entries from logback's context -->
            <version/> <!-- Logstash json format version, the @version field in the output-->
            <logLevel/>
            <loggerName/>
            <pattern>
                <pattern> <!-- we can add some custom fields to be sent with all the log entries make filtering easier in Logstash   -->
                    {
                    "appName": "upp-quality-control-framework-ws"    <!--or searching with Kibana-->
                    }
                </pattern>
            </pattern>

            <threadName/>
            <message/>

            <logstashMarkers/> <!-- Useful so we can add extra information for specific log lines as Markers-->
            <arguments/> <!--or through StructuredArguments-->

            <stackTrace/>
        </providers>
    </encoder>
</appender>
```

### Use with New Relic Logs in Context

Add New Relic Logs for logback.

```xml
<dependency>
    <groupId>com.newrelic.logging</groupId>
    <artifactId>logback</artifactId>
    <version>2.2</version>
</dependency>
```

Configure as the following.

```xml
<appender name="NewRelicLogs" class="jp.co.newrelikk.labs.HttpAppender">
    <contentType>application/json</contentType>
    <url>https://log-api.newrelic.com/logs/v1</url>
    <headers>{"X-License-Key":"YourNewRelicLicenseKey"}</headers>
    <reconnectDelay>10</reconnectDelay>
    <encoder class="com.newrelic.logging.logback.NewRelicEncoder"/>
</appender>
```

## References

* [Chapter 4: Appenders](http://logback.qos.ch/manual/appenders.html)
* [logback-redis-appender](https://github.com/kmtong/logback-redis-appender)
