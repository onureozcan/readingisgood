package com.example.readingisgood.config;


import com.example.readingisgood.factory.UserAuthenticationFactory;
import com.example.readingisgood.filter.AuthFilter;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandlerImpl())
                .and()
                .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/**")
                .authenticated()
                .and()
                .addFilterAfter(
                        new AuthFilter(getApplicationContext().getBean(UserAuthenticationFactory.class)),
                        BasicAuthenticationFilter.class
                )
                .authorizeRequests();
    }
}
