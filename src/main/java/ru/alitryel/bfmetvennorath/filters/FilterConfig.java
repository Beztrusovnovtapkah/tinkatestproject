package ru.alitryel.bfmetvennorath.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alitryel.bfmetvennorath.repositories.UserRepository;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    private final UserRepository userRepository;

    @Bean
    public FilterRegistrationBean<BanFilter> loggingFilter() {
        FilterRegistrationBean<BanFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BanFilter(userRepository));
        registrationBean.addUrlPatterns("/users/*");
        registrationBean.addUrlPatterns("/admin/*");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}

