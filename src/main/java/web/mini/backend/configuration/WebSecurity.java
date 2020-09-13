package web.mini.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import web.mini.backend.exception.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable Cross Site Scripting
        // TODO: Enable CSRF
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
        //http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

        // Config for Login Form
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                //.loginProcessingUrl("/auth-login") // Submit URL
                .loginPage("/login")//
                //.defaultSuccessUrl("/home")//
                .failureUrl("/login?error=true")//
                //.usernameParameter("username")//
                //.passwordParameter("password")//
                .permitAll()
                // Config for Logout Page
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // raw = password
        // hash = $2a$04$gjESNyb94FwBhFte44hls.uEflDlH7Rk1tHzt3uXCnLJm4yjDq/FO
        auth.inMemoryAuthentication()
                .withUser("user@pixies.org").password("$2a$04$gjESNyb94FwBhFte44hls.uEflDlH7Rk1tHzt3uXCnLJm4yjDq/FO").roles("USER")
                .and()
                .withUser("admin@pixies.org").password("$2a$04$gjESNyb94FwBhFte44hls.uEflDlH7Rk1tHzt3uXCnLJm4yjDq/FO").roles("ADMIN");
    }
}
