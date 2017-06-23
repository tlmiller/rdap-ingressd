package net.apnic.rdap.filter.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

public class RedirectFilter extends ZuulFilter {
    private Function<HttpServletRequest, Optional<URI>> requestToRedirect;

    public RedirectFilter(Function<HttpServletRequest, Optional<URI>> requestToRedirect) {
        this.requestToRedirect = requestToRedirect;
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
        requestToRedirect.apply(RequestContext.getCurrentContext().getRequest())
                .ifPresent(uri -> {
                    try {
                        RequestContext.getCurrentContext().getResponse().sendRedirect(uri.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return null;
    }
}
