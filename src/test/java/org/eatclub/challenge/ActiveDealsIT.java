package org.eatclub.challenge;

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
    void timeOfDay3PM() {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=15:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .allDealsHaveOpenRestaurantAtTimeOfDay("15:00")
                .hasSize(7)
                .containsDealIds(
                        "DEA567C5-0000-3C03-FF00-E3B24909BE00", // (Masala Kitchen)
                        "DEA567C5-1111-3C03-FF00-E3B24909BE00", // (Masala Kitchen)
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", // (ABC Chicken)
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", // (ABC Chicken)
                        "B5713CD0-0000-40C7-AFC3-7D46D26B00BF", // (Kekou)
                        "B5913CD0-0550-40C7-AFC3-7D46D26B01BF", // (OzzyThai)
                        "B5713CD0-1361-40C7-AFC3-7D46D26B00BF" // (OzzyThai)
                );
    }

    @Test
    void excludedDealsAt3PM() {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=15:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .doesNotContainDealIds(
                        "CDB2B42A-0000-EE20-FF45-8D0A8057E200", // Vrindavan (starts at 6pm)
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF", // Kekou (starts at 5pm)
                        "B5913CD0-0000-40C7-AFC3-7D46D26B01BF", // Gyoza Gyoza (restaurant opens at 4pm)
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF"  // Gyoza Gyoza
                );
    }

    @Test
    void timeOfDay6PM() {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=18:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .allDealsHaveOpenRestaurantAtTimeOfDay("18:00")
                .hasSize(9)
                .containsDealIds(
                        "DEA567C5-0000-3C03-FF00-E3B24909BE00", // (Masala Kitchen)
                        "DEA567C5-1111-3C03-FF00-E3B24909BE00", // (Masala Kitchen)
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", // (ABC Chicken)
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", // (ABC Chicken)
                        "CDB2B42A-0000-EE20-FF45-8D0A8057E200", // (Vrindavan)
                        "B5713CD0-0000-40C7-AFC3-7D46D26B00BF", // (Kekou)
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF", // (Kekou)
                        "B5913CD0-0000-40C7-AFC3-7D46D26B01BF", // (Gyoza Gyoza)
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF"  // (Gyoza Gyoza)
                );
    }

    @Test
    void excludedDealsAt6PM() {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=18:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .doesNotContainDealIds(
                        "B5913CD0-0550-40C7-AFC3-7D46D26B01BF", // OzzyThai (restaurant closes at 3pm)
                        "B5713CD0-1361-40C7-AFC3-7D46D26B00BF"  // OzzyThai
                );
    }

    @Test
    void timeOfDay9PM() {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=21:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .allDealsHaveOpenRestaurantAtTimeOfDay("21:00")
                .hasSize(9)
                .containsDealIds(
                        "DEA567C5-0000-3C03-FF00-E3B24909BE00", //  (Masala Kitchen)
                        "DEA567C5-1111-3C03-FF00-E3B24909BE00", //  (Masala Kitchen)
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", //  (ABC Chicken)
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", //  (ABC Chicken)
                        "CDB2B42A-0000-EE20-FF45-8D0A8057E200", //  (Vrindavan)
                        "B5713CD0-0000-40C7-AFC3-7D46D26B00BF", //  (Kekou)
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF", //  (Kekou)
                        "B5913CD0-0000-40C7-AFC3-7D46D26B01BF", // (Gyoza Gyoza)
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF" // (Gyoza Gyoza)
                );
    }

    @Test
    void excludedDealsAt9PM() {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080) + "/deal/active?timeOfDay=21:00";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ActiveDealsResponseDTO> response = restTemplate.getForEntity(url, ActiveDealsResponseDTO.class);

        assertThat(response)
                .hasStatusCode(200)
                .hasNonNullResponseBody()
                .doesNotContainDealIds(
                        "B5913CD0-0550-40C7-AFC3-7D46D26B01BF", // OzzyThai (closes at 3pm)
                        "B5713CD0-1361-40C7-AFC3-7D46D26B00BF"  // OzzyThai
                );
    }
}
