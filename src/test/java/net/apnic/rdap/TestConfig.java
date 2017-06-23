package net.apnic.rdap;

import com.netflix.zuul.ZuulFilter;
import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.filters.RedirectFilter;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@EnableZuulProxy
@SpringBootApplication
@EnableAutoConfiguration(exclude={ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages = {"net.apnic.rdap"}, excludeFilters={
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value={
                ZuulFilter.class,
                Application.class
        }),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern="net\\.apnic\\.rdap\\.scraper.*")
})
public class TestConfig {

    @Autowired
    ResourceLocator<Object> resourceLocator;

    @Autowired
    Function<RDAPRequestPath, Object> pathToResource;

    @Bean
    ZuulFilter redirectFilter() {

        Function<HttpServletRequest, Optional<Object>> requestToResource = (request) -> {
            RDAPRequestPath path = RDAPRequestPath.createRequestPath(request.getRequestURI());
            return Optional.ofNullable(pathToResource.apply(path));
        };

        Function<Object, Optional<URI>> resouceToRedirect = resource -> {
            RDAPAuthority authority = ((Supplier<RDAPAuthority>) (
                    () -> {
                        try {
                            return resourceLocator.authorityForResource(resource);
                        } catch (ResourceNotFoundException e) {
                            return null;
                        }
                    })).get();
            return Optional.ofNullable(authority)
                    .map(rdapAuthority -> rdapAuthority.getDefaultServerURI());
        };

        Function<HttpServletRequest, Optional<URI>> requestToRedirect = resouceToRedirect.compose(requestToResource);

        return new RedirectFilter(requestToRedirect);
    }

}
