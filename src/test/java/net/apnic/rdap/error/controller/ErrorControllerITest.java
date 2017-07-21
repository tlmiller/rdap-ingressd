package net.apnic.rdap.error.controller;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import com.jayway.jsonpath.matchers.JsonPathMatchers;

import java.util.stream.Stream;

import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matchers;
import org.hamcrest.object.HasToString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ErrorControllerITest
{
    @LocalServerPort
    private int serverPort;

    private Matcher<ResponseEntity<String>> isValidRDAPResponse =
        Matchers.allOf(
            hasProperty("headers", hasProperty("content-type", HasToString.hasToString("application/rdap+json"))),
            hasProperty("body", Matchers.allOf(
                JsonPathMatchers.isJson(),
                JsonPathMatchers.hasJsonPath("$.description"),
                JsonPathMatchers.hasJsonPath("$.errorCode"),
                JsonPathMatchers.hasJsonPath("$.title"),
                JsonPathMatchers.hasJsonPath("$.notices"),
                JsonPathMatchers.hasJsonPath("$.rdapConformance", contains("rdap_level_0"))
                )
            )
        );

    private Matcher<ResponseEntity<String>> is400RDAPErrorResponse =
        Matchers.allOf(
            hasProperty("statusCodeValue", Matchers.is(400)),
            isValidRDAPResponse,
            hasProperty("body", Matchers.allOf(
                JsonPathMatchers.hasJsonPath("$.errorCode", Matchers.is("400")),
                JsonPathMatchers.hasJsonPath("$.title", Matchers.is("Bad Request"))
                )
            )
        );

    private Matcher<ResponseEntity<String>> is404RDAPErrorResponse =
        Matchers.allOf(
            hasProperty("statusCodeValue", Matchers.is(404)),
            isValidRDAPResponse,
            hasProperty("body", Matchers.allOf(
                JsonPathMatchers.hasJsonPath("$.errorCode", Matchers.is("404")),
                JsonPathMatchers.hasJsonPath("$.title", Matchers.is("Not Found"))
                )
            )
        );


    @Test
    public void returns400ForMalformedRequests()
    {
        Stream<String> badPaths = Stream.of("/ip/not a real ip address",
                                            "/ip/1.2.3", "/ip/1.2.3.4/99",
                                            "/ip/ffff:ffff:ffff::/2",
                                            "/ip/ffff:");

        badPaths.forEach(badPath ->
        {
            ResponseEntity<String> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.serverPort + badPath,
                              String.class);

            assertThat(response, is400RDAPErrorResponse);
        });
    }

    @Test
    public void returns404ForMalformedRequests()
    {
        Stream.of("/entity/doesnotexist",
                  "/domain/apnic.example",
                  "/nameserver/ns1.apnic.example")
        .forEach(nonPath ->
        {
            ResponseEntity<String> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.serverPort + nonPath,
                              String.class);

            assertThat(response, is404RDAPErrorResponse);
        });
    }
}
