package com.vv.personal.twm.calc;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.vv.personal.twm.calc.constants.Constants.LOCALHOST;
import static com.vv.personal.twm.calc.constants.Constants.SWAGGER_UI_URL;

/**
 * @author Vivek
 * @since 03/02/21 (migrated to Quarkus)
 */
@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleBean.class);

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "twm-calc-service")
    String applicationName;

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    String port;

    void onStart(@Observes StartupEvent ev) {
        String host = LOCALHOST;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("Failed to obtain ip address. ", e);
        }
        LOGGER.info("'{}' activation is complete! Exact url: {}", applicationName.toUpperCase(),
                String.format(SWAGGER_UI_URL, host, port));
    }
}
