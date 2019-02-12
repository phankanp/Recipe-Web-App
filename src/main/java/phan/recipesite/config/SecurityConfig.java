package phan.recipesite.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import phan.recipesite.service.UserDetailsServiceImpl;
import phan.recipesite.web.FlashMessage;


@Configuration
@EnableWebSecurity
@ComponentScan("phan.recipesite")
@EnableTransactionManagement
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;


    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configures security for authorizing user requests, login page, and logout process
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .usernameParameter("username")
                .failureHandler(loginFailureHandler())
                .and()
                .logout()
                .and()
                .rememberMe().key("secretKey").tokenValiditySeconds(86400)
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and()
                .csrf().disable()
                .headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    // Redirects back to login if authentication fails
    private AuthenticationFailureHandler loginFailureHandler() {
        return ((request, response, exception) -> {
            request.getSession().setAttribute("flash", new FlashMessage("Incorrect username and/or password. " +
                    "Please try again.", FlashMessage.Status.FAILURE));
            response.sendRedirect("/login");
        });
    }

}
