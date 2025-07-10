package org.eatclub.challenge;

import org.eatclub.challenge.assertions.DealAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eatclub.challenge.assertions.DealAssertions.assertThat;

@Testcontainers
public class ActiveDealsIT {

    @Container
    private static final GenericContainer<?> container;

    static {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path jarPath = projectRoot.resolve("target/app.jar");

        ImageFromDockerfile image = new ImageFromDockerfile()
                .withFileFromPath("Dockerfile", projectRoot.resolve("Dockerfile"))
                .withFileFromPath("target/app.jar", jarPath);

        container = new GenericContainer<>(image)
                .withExposedPorts(8080)
                .waitingFor(Wait.forListeningPort());
    }

    @Test
    void timeOfDay3PM() throws Exception {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=15:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .allDealsHaveOpenRestaurantAtTimeOfDay("15:00");
    }
}
