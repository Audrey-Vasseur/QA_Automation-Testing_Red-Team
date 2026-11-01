
package api;

import core.AppConfig;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PostsApiTest {

    private static final int POST_ID = 3;
    private static final String POST_SLUG = "hello-world";

    @BeforeAll
    static void configureApi() {
        RestAssured.baseURI = AppConfig.get("api.base.url");
        RestAssured.basePath =
                "/wp/v2/sites/" + AppConfig.get("api.site.id");
    }

    // Structure de requête réutilisable
    private RequestSpecification apiRequest() {
        return given()
                .accept("application/json");
    }

    @Test
    @DisplayName("API-01 - Retrieve posts successfully")
    void shouldRetrievePosts() {

        apiRequest()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("API-02 - Retrieve a specific post")
    void shouldRetrievePostById() {

        apiRequest()
                .when()
                .get("/posts/" + POST_ID)
                .then()
                .statusCode(200)
                .body("id", equalTo(POST_ID))
                .body("slug", equalTo(POST_SLUG))
                .body("status", equalTo("publish"))
                .body("title.rendered", containsString("Bonjour"));
    }

    @Test
    @DisplayName("API-03 - Filter posts by slug")
    void shouldFilterPostsBySlug() {

        apiRequest()
                .queryParam("slug", POST_SLUG)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(POST_ID))
                .body("[0].slug", equalTo(POST_SLUG));
    }

    @Test
    @DisplayName("API-04 - Limit the number of returned posts")
    void shouldRespectPerPageParameter() {

        apiRequest()
                .queryParam("per_page", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }


    @Test
    @DisplayName("API-05 - Retrieve a post with a non-existent ID")
    void shouldReturn404ForNonExistentPost() {

        apiRequest()
                .when()
                .get("/posts/9999999999")
                .then()
                .statusCode(404)
                .body("code", equalTo("rest_post_invalid_id"))
                .body("message", equalTo("Invalid post ID."))
                .body("data.status", equalTo(404));
    }

    @Test
    @DisplayName("API-06 - Reject an invalid per_page parameter")
    void shouldRejectInvalidPerPageParameter() {

        apiRequest()
                .queryParam("per_page", 101)
                .when()
                .get("/posts")
                .then()
                .statusCode(400)
                .body("code", equalTo("rest_invalid_param"))
                .body("message",
                        equalTo("Invalid parameter(s): per_page"))
                .body("data.status", equalTo(400))
                .body("data.details.per_page.code",
                        equalTo("rest_out_of_bounds"))
                .body("data.details.per_page.message",
                        equalTo("per_page must be between 1 (inclusive) and 100 (inclusive)"));
    }

}
