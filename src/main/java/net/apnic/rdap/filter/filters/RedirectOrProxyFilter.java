package net.apnic.rdap.filter.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import net.apnic.rdap.authority.RDAPAuthority;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

public class RedirectOrProxyFilter extends ZuulFilter {
    private Function<HttpServletRequest, Optional<RDAPAuthority>> requestToAuthority;
    private Function<RDAPAuthority, URI> uriStrategy;
    private Function<RDAPAuthority, Action> authorityAction;

    public enum Action {
        REDIRECT,
        PROXY;
    }

    public RedirectOrProxyFilter(
            Function<HttpServletRequest, Optional<RDAPAuthority>> requestToAuthority,
            Function<RDAPAuthority, URI> uriStrategy,
            Function<RDAPAuthority, Action> authorityAction) {
        this.requestToAuthority = requestToAuthority;
        this.uriStrategy = uriStrategy;
        this.authorityAction = authorityAction;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        requestToAuthority.apply(RequestContext.getCurrentContext().getRequest())
                .ifPresent(authority -> {
                    URI uri = uriStrategy.apply(authority);
                    switch (authorityAction.apply(authority)) {
                        case REDIRECT:
                            try {
                                RequestContext.getCurrentContext().getResponse().sendRedirect(uri.toString());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case PROXY:
                            //TODO
                            break;
                    }

                });

        return null;
    }
}
