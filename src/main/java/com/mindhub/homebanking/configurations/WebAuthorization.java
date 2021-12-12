package com.mindhub.homebanking.configurations;

import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.http.HttpResponse;


@Configuration
@EnableWebSecurity
public class WebAuthorization extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/clients","/api/payments").permitAll()
                .antMatchers("/web/index.html","/web/Styles/**","/web/Scripts/index.js","/web/assets/**").permitAll()
                .antMatchers("/web/**", "/api/clients/**", "/api/accounts/**", "/api/transactions/**", "/api/loans","/api/transactions/pdf/**", "/api/payments/**").hasAuthority("USER")
                .antMatchers("/rest/**", "/api/**","/h2-console/**", "/manager.html", "/api/loans/created/**", "/api/loans/**" ).hasAuthority("ADMIN")

                .and()
                    .formLogin()
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .loginPage("/api/login")
                        .successHandler((req, res, auth) -> clearAuthenticationAttributes(req))
                        .failureHandler((req, res, exc) -> {
                            if (exc.getMessage().contains("Maximum sessions of 1 for this principal exceeded")){
                                res.sendError(HttpServletResponse.SC_CONFLICT,"Ya posee sesión iniciada");
                            } else{
                           res.sendError(HttpServletResponse.SC_UNAUTHORIZED);}
                        })

                .and()
                    .logout()
                        .logoutUrl("/api/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())

                .and()
                    .sessionManagement()
                        .invalidSessionUrl("/web/index.html")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                        .expiredUrl("/?invalid-session=true")
                        .sessionRegistry(sessionRegistry());

        http.csrf()
                .disable();

        http.headers()
                .frameOptions()
                .disable();

        http.exceptionHandling()
                .authenticationEntryPoint((req, res, exc) -> res.sendRedirect("/web/index.html"));

        http.cors();

    }

    @Bean
     public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session !=null){
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

}
