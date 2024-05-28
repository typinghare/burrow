package me.jameschan.burrow.server;

import jakarta.annotation.PreDestroy;
import me.jameschan.burrow.kernel.ChamberShepherd;
import me.jameschan.burrow.kernel.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"me.jameschan.burrow.kernel"})
@RestController
public class BurrowServer {
  private static final Logger logger = LoggerFactory.getLogger(BurrowServer.class);

  private final ChamberShepherd chamberShepherd;

  @Autowired
  public BurrowServer(final ChamberShepherd chamberShepherd) {
    this.chamberShepherd = chamberShepherd;
  }

  public static void main(final String[] args) {
    SpringApplication.run(BurrowServer.class, args);
  }

  @EventListener(ContextRefreshedEvent.class)
  public void onStart() {
    try {
      chamberShepherd.initiate(Constants.DEFAULT_CHAMBER);
    } catch (final Throwable ex) {
      logger.error("Fail to initialize the default chamber.", ex);
      System.exit(ExitCode.ERROR);
    }
  }

  @PostMapping("/")
  public BurrowResponse receiveRequest(@RequestBody final BurrowRequest request) {
    return chamberShepherd.processRequest(request);
  }

  @PreDestroy
  public void onShutdown() {
    logger.info("Shutting down Burrow ...");

    // Terminate all chambers
    chamberShepherd.terminateAll();
    logger.info("All chambers have been terminated.");

    logger.info("Successfully shut down Burrow.");
  }
}
