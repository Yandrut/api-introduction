package org.yandrut.api;

import org.yandrut.data.*;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import static org.testng.Assert.*;
import static io.restassured.RestAssured.given;
import static org.yandrut.data.Specifications.*;

public class ApiTest {
    private static final Logger log = LogManager.getLogger(ApiTest.class);
    private static final String URL = "https://reqres.in/";
    private static final String REGISTER_ENDPOINT = "api/register";

    @Test
    public void successfulRegistrationTest() {
        submitSpecifications(requestSpec(URL), responseSpec(200));

        Registration registrationData = new Registration("eve.holt@reqres.in","pistol");

        SuccessfulRegistration successfulRegistration = given()
                .body(registrationData)
                .when()
                .post(REGISTER_ENDPOINT)
                .then().log().body()
                .extract().as(SuccessfulRegistration.class);

        Integer expectedId = 4;
        Integer actualId = successfulRegistration.getId();
        String expectedToken = "QpwL5tke4Pnpja7X4";
        String actualToken = successfulRegistration.getToken();

        assertEquals(expectedId, actualId);
        assertEquals(expectedToken, actualToken);
    }

    @Test
    public void unsuccessfulRegistrationTest() {
        submitSpecifications(requestSpec(URL), responseSpec(400));

        Registration user = new Registration("sydney@fife", "");

        UnsuccessfulRegistration registration = given()
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT)
                .then().log().body()
                .extract().as(UnsuccessfulRegistration.class);

        String expected = "Missing password";
        String actual = registration.getError();

        assertEquals(expected, actual);
    }

    @Test
    public void avatarNameContainsUserId() {
        submitSpecifications(requestSpec(URL), responseSpec(200));

        List<UserData> users = given()
                .queryParam("page", 2)
                .when()
                .get("api/users")
                .then().log().body()
                .extract().body().jsonPath().getList("data", UserData.class);

        assertNotNull(users);
        users.forEach(user -> assertTrue(user.getAvatar().contains(user.getId().toString())));
    }

    @Test
    public void userEmailMatchesFormat() {
        submitSpecifications(requestSpec(URL), responseSpec(200));

        List<UserData> users = given()
                .queryParam("page", 2)
                .when()
                .get("api/users")
                .then().log().body()
                .extract().body().jsonPath().getList("data", UserData.class);

        assertNotNull(users);
        assertTrue(users.stream().allMatch(user -> user.getEmail().endsWith("reqres.in")));
    }

    @Test
    public void isResourcesListSortedByYear() {
        submitSpecifications(requestSpec(URL), responseSpec(200));

        List<ResourceData> resources = given()
                .when()
                .get("api/unknown")
                .then().log().body()
                .extract().body().jsonPath().getList("data", ResourceData.class);

        List<Integer> yearsActual = resources
                .stream()
                .map(ResourceData::getYear)
                .collect(Collectors.toList());

        List<Integer> yearsSorted = yearsActual
                .stream()
                .sorted()
                .collect(Collectors.toList());

        assertEquals(yearsSorted, yearsActual);
    }

    @Test
    public void allowsToDeleteUser() {
        submitSpecifications(requestSpec(URL), responseSpec(204));

        given()
                .pathParam("userId", 2)
                .when()
                .delete("api/users/{userId}")
                .then().log().body();
    }

    @Test
    public void currentAndResponseTimeMatches() {
        submitSpecifications(requestSpec(URL), responseSpec(200));
        UserTime userTime = new UserTime("morpheus", "zion resident");

        UserTimeResponse response = given()
                .when()
                .pathParam("userId", 2)
                .body(userTime)
                .put("api/users/{userId}")
                .then().log().body()
                .extract().as(UserTimeResponse.class);

        String currentTime = Clock.systemUTC().instant().toString();
        String responseTime = response.getUpdatedAt();

        log.info(currentTime + " | " + responseTime);
        assertEquals(currentTime, responseTime);
    }
}