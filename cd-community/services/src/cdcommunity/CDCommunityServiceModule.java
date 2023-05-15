import io.harness.cvng.core.services.api.CDCommunityTelemetrySentStatusService; // check import
import io.harness.cvng.core.services.impl.CDCommunityTelemetrySentStatusServiceImpl; // this is not present
import io.harness.telemetry.AbstractTelemetryModule;
import io.harness.telemetry.TelemetryConfiguration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


install(new AbstractTelemetryModule() {
@Override
public TelemetryConfiguration telemetryConfiguration() {
        return verificationConfiguration.getSegmentConfiguration();
  }
});

bind(ScheduledExecutorService.class)
        .annotatedWith(Names.named("CDCommunityTelemetryPublisherExecutor"))
        .toInstance(new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder()
        .setNameFormat("Cd-community-telemetry-publisher-Thread-%d")
        .setPriority(Thread.NORM_PRIORITY)
        .build()));

bind(CDCommunityTelemetrySentStatusService.class).to(CDCommunityTelemetrySentStatusServiceImpl.class);