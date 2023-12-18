package org.yandrut.api;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import static io.restassured.RestAssured.given;

public class ApiTest {
    private static final String URL = "https://reqres.in/";

    @Test
    public void successfulRegistrationTest() {
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(200));

        Registration user = new Registration("eve.holt@reqres.in","pistol");

        SuccessfulRegistration registration = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessfulRegistration.class);

        Integer expectedId = 4;
        Integer actualId = registration.getId();
        String expectedToken = "QpwL5tke4Pnpja7X4";
        String actualToken = registration.getToken();

        assertEquals(expectedId, actualId);
        assertEquals(expectedToken, actualToken);
    }

    @Test
    public void unsuccessfulRegistrationTest() {
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(400));

        Registration user = new Registration("sydney@fife", "");

        UnsuccessfulRegistration registration = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnsuccessfulRegistration.class);

        String expected = "Missing password";
        String actual = registration.getError();

        assertEquals(expected, actual);
    }

    @Test
    public void avatarNameContainsUserId() {
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(200));

        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        assertNotNull(users);
        users.forEach(user -> assertTrue(user.getAvatar().contains(user.getId().toString())));
    }

    @Test
    public void userEmailMatchesFormat() {
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(200));

        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        assertNotNull(users);
        assertTrue(users.stream().allMatch(user -> user.getEmail().endsWith("reqres.in")));
    }

    @Test
    public void isResourcesListSortedByYear() {
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(200));

        List<ResourceData> resources = given()
                .when()
                .get("api/unknown")
                .then().log().all()
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
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(204));

        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void currentAndResponseTimeMatches() {
        Specifications.submitSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        UserTime user = new UserTime("morpheus", "zion resident");

        UserTimeResponse response = given()
                .when()
                .body(user)
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);


        String regex = "(.{5})$";
        String regexForResponse = "(.{6})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        String responseTime = response.getUpdatedAt().replaceAll(regexForResponse, "");

        assertEquals(currentTime, responseTime);
    }
}