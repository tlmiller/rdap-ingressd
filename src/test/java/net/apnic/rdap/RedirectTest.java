package net.apnic.rdap;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment= WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class RedirectTest {

    @LocalServerPort
    int port;

    @MockBean
    ResourceLocator<Object> locator;

    @MockBean
    Function<RDAPRequestPath, Object> pathToResource;

    @Test
    public void canRedirect() throws ResourceNotFoundException {
        //given
        RDAPAuthority authority = RDAPAuthority.createAnonymousAuthority();
        authority.addServer(URI.create("http://dont.care"));

        when(pathToResource.apply(any())).thenReturn(AsnRange.parse("AS1"));
        when(locator.authorityForResource(any())).thenReturn(authority);
        String rdapRequestForRemoteResource = "/autnum/1";

        //when
        ResponseEntity<String> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port +
                        rdapRequestForRemoteResource, String.class);

        //then
        assertThat(response.getStatusCodeValue(), is(302));
    }
}
