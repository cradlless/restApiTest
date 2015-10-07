package UTrack;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.Before;

public class IssueMethods {
    Cookies cookies;

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "https://goit2.myjetbrains.com/youtrack/rest/";
        Response response =
                given().
                        param("login", "cradlless@gmail.com").
                        param("password", "Qwerty+1").
                        when().
                        post("user/login");

        cookies = response.getDetailedCookies();

    }

    @Test
    public void testCreateIssue() throws Exception {
        given().
                cookies(cookies).
                param("project", "GOIT").
                param("summary", "Some summary").
                param("description", "Some description").
                when().
                param("/issue").
                then().
                statusCode(201);
    }

    private String createTestIssue() throws Exception {
        Response response =
        given().
                        cookies(cookies).
                        param("project", "GOIT").
                        param("summary", "Some summary").
                        param("description", "Some description").
                        when().
                        put("/issue");

        String location = response.getHeader("Location");
        String issueId = location.substring(location.lastIndexOf("/")+1);
        return issueId;
    }

    @Test
    public void testDeleteIssue() throws Exception {
    String issueID = createTestIssue();
    given().
            cookies(cookies).
    when().
            delete("/issue/" + issueID).
    then().
            statusCode(200);

    }

    @Test
    public void testGetIssue() throws Exception {
        String issueID = createTestIssue();
        Response response =
        given().cookies(cookies).
        when().
                get("/issue/" + issueID).
        then().
                statusCode(200).
                body("issue.@id", equalTo(issueID)).
                extract().response();
        System.out.println(response.asString());
    }

    @Test
    public void testIssueExist() throws Exception {
        String issueID = createTestIssue();
        given().
                cookies(cookies).
        when().
                get("/issue/" + issueID + "/exists").
        then().
                statusCode(200);
    }

    @Test
    public void testIssueNotExists() throws Exception {
        String issueID = "123456";
        given().
                cookies(cookies).
        when().
                get("/issue/" + issueID + "/exists").
        then().
                statusCode(404);

    }

    @Test
    public void testGetNumberIssue (){

        Response response=
                (given().
                        cookies(cookies).
                        param("callback", "fun").
                        param("filter",5).
                when().
                        get("/issue/caunt").
                then().statusCode(200).extract().response());
        System.out.println(response.asString());

        Integer issueNumber = Integer.parseInt(response.asString().replaceAll("[\\D]",""));
        System.out.println(issueNumber);
        assertThat(issueNumber, greaterThanOrEqualTo(10));

    }
}
