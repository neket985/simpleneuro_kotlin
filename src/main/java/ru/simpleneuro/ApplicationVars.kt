package ru.simpleneuro

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.ScheduledReporter
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import metrics_influxdb.InfluxdbReporter
import metrics_influxdb.api.protocols.HttpInfluxdbProtocol
import java.util.concurrent.TimeUnit

object ApplicationVars {
    val metrics = MetricRegistry()
    val reporter = buildInfluxReporter(metrics, ConfigFactory.load().getConfig("influx"))
//    val reporter = ConsoleReporter.forRegistry(metrics).build()

    init {
        reporter.start(5, TimeUnit.SECONDS)
    }

    private fun buildInfluxReporter(registry: MetricRegistry, cfg: Config): ScheduledReporter {
        val protocol = HttpInfluxdbProtocol(
                cfg.getString("host"),
                cfg.getInt("port"),
//                cfg.getString("user"),
//                cfg.getString("password"),
                cfg.getString("database")
        )

        return InfluxdbReporter.forRegistry(registry)
                .protocol(protocol)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .skipIdleMetrics(false)
                .build()
    }
}