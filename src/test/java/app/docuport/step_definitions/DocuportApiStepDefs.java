package app.docuport.step_definitions;
import app.docuport.pages.LoginPage;
import app.docuport.utilities.ConfigurationReader;
import app.docuport.utilities.DocuportApiUtil;
import app.docuport.utilities.Driver;
import app.docuport.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DocuportApiStepDefs {
    String accessToken;
    Response response;
    private static final Logger LOG = LogManager.getLogger();
    @Given("User logged in to Docuport api as {string} role")
    public void user_logged_in_to_Docuport_api_as_role(String userRole) {
       Map<String,String> userInfo= DocuportApiUtil.getUserInfo(userRole);

        LOG.info("username " + userInfo.get("username"));
        LOG.info("password " + userInfo.get("password"));

        accessToken = DocuportApiUtil.getAccessToken(userInfo.get("username"),userInfo.get("password"));
        System.out.println("accessToken = " + accessToken);
    }

    @Given("User sends GET request to {string} with query param {string} for EmailAddress")
    public void user_sends_GET_request_to_with_query_param_for_EmailAddress(String endPoint, String userRole) {
        String queryParamValue=DocuportApiUtil.getUserInfo(userRole).get("username");
       response= given().accept(ContentType.JSON)
                .and().log().all()
                .and().header("authorization",accessToken)
                .and().queryParam("EmailAddress",queryParamValue)
                .when().get(Environment.BASE_URL + endPoint);
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int expStatusCode) {
        assertEquals(expStatusCode,response.statusCode());
    }

    @Then("content type is {string}")
    public void content_type_is(String expContentType) {
        assertEquals(expContentType,response.contentType());
    }

    @Then("role is {string}")
    public void role_is(String expUserRole) {
        assertEquals("Role doesn't match",expUserRole, response.body().path("items[0].roles[0].name"));

        JsonPath jsonPath = response.jsonPath();
        assertEquals(expUserRole, jsonPath.getString("items[0].roles[0].name"));

        //TODO: In Hooks class make one before method to initialize "baseURI" - @DocuportApi
    }


    @Given("User logged in to Docuport app as {string} role")
    public void user_logged_in_to_Docuport_app_as_role(String userRole) {
        Driver.getDriver().get(Environment.URL);
        LoginPage loginPage = new LoginPage();
       Map<String,String > userInfo =  DocuportApiUtil.getUserInfo(userRole);
        loginPage.login(userInfo.get("username"),userInfo.get("password"));
    }

    @When("User goes to profile page")
    public void user_goes_to_profile_page() {

    }

    @Then("User should see same info on UI and API")
    public void user_should_see_same_info_on_UI_and_API() {

    }
}
