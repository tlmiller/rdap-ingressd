package net.apnic.rdap;

import com.netflix.zuul.ZuulFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

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

}
