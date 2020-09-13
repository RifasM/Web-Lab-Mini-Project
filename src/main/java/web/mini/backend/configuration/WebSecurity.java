package web.mini.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // The pages does not require login
        http.authorizeRequests().antMatchers("/", "/login", "/logout", "signup").permitAll();

        // /home page requires login as USER or ADMIN.
        // If no login, it will redirect to /login page.
        http.authorizeRequests().antMatchers("/home/**").access("hasAnyRole('USER', 'ADMIN')");

        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ADMIN')");

        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will be thrown.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

        // Config for Login Form
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                //.loginProcessingUrl("/auth-login") // Submit URL
                .loginPage("/login")//
                //.defaultSuccessUrl("/home")//
                //.failureUrl("/login?error=true")//
                //.usernameParameter("userName")//
                //.passwordParameter("password")//
                .permitAll()
                // Config for Logout Page
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler);

        // Disable Cross Site Scripting
        // TODO: Enable CSRF
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user@pixies.org").password("password").roles("USER")
                .and()
                .withUser("admin@pixies.org").password("password").roles("ADMIN");
    }
}
