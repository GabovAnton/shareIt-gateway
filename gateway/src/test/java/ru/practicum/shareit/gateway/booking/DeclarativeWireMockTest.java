package ru.practicum.shareit.gateway.booking;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.user.UserClientFeign;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.hamcrest.MatcherAssert.assertThat;

//@TestPropertySource(properties = {"SHAREIT_SERVER_URL=http://localhost:8089"})
@WireMockTest(httpPort = 8089)
@SpringBootTest(properties = "SHAREIT_SERVER_URL=http://localhost:8089")

@ExtendWith(SpringExtension.class)
public class DeclarativeWireMockTest {

    RestTemplate restTemplate;

    ResponseEntity response;



  /*  @Autowired
    private BookingFeignClient bookingFeignClient;*/

    @Autowired
    private UserClientFeign userClientFeign;

    @Before
    public void setup() throws Exception {

        restTemplate = new RestTemplate();
        response = null;
    }

    @BeforeEach
    void setUp() throws IOException {

        stubFor(get("/users").willReturn(WireMock
                .aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json")
                .withBodyFile("users-response.json")));
    }

    @Test
    public void whenGetBooks_thenBooksShouldBeReturned() {

        RestTemplate restTemplate = new RestTemplate();

        response = restTemplate.getForEntity("http://localhost:8089/users", String.class);
        Object body = response.getBody();
        // assertThat("Verify Response Body", response.getBody().contains("mappings"));
        assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));

        /* assertEquals(1L, (long) bookingFeignClient.getBookingById(7L, 1L).getId());*/
    }

    @Test
    void test_something_with_wiremock() {
        // The static DSL will be automatically configured for you
        //   stubFor(get("/static-dsl").willReturn(aResponse().withStatus(200)));

        //   List<BookingDto> bookingDto = bookingService.getItemsByStateAndOwner(1L,0,10, BookingState.ALL);
        // List<BookingDto> bookingDto = bookingFeignClient.getItemsByStateAndOwner("0", "10", "ALL", 1);
        //        //org.hamcrest.MatcherAssert.assertThat(bookingDto, Matchers.is(1L));

        List<UserDto> users = userClientFeign.getUsers();
        // List<UserDto> users = userClientFeign.getUsers();

        System.out.println(users);

        assertThat(users, Matchers.is(Matchers.notNullValue()));
        // Do some testing...
    }



}
