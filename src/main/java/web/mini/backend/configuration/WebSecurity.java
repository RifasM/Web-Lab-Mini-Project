package web.mini.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import web.mini.backend.exception.CustomAccessDeniedHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, account_enabled from users where username=?")
                .authoritiesByUsernameQuery("select username, user_role from users where username=?")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable Cross Site Scripting
        // http.csrf().disable();

        // The pages does not require login
        http.authorizeRequests().antMatchers("/", "/login", "/logout", "/signup").permitAll();

        // /home page requires login as USER or ADMIN.
        // If no login, it will redirect to /login page.
        http.authorizeRequests().antMatchers("/home", "/api/**",
                "/profile/**", "/create*", "/edit*", "/view*/**", "/like", "/comment").
                access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");

        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin/**", "/home/disabled").access("hasRole('ROLE_ADMIN')");

        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will be thrown.
        //http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

        // Config for Login Form
        http.authorizeRequests()
                .and()
                    .formLogin()//
                    // Submit URL of login page.
                //.loginProcessingUrl("/auth-login") // Submit URL
                .loginPage("/login")//
                .defaultSuccessUrl("/home")//
                .failureUrl("/login?error=true")//
                //.usernameParameter("username")//
                //.passwordParameter("password")//
                .permitAll()
                // Config for Logout Page
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                //.logoutUrl("/logout")
                .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                .and().
                    exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // raw = Password@123
        // hash = $2y$12$JC2kCe9r5GqKtQG/2JldZ.yXnE0vV7dAdTFtS61HR/vEje/hG3Hca
        auth.inMemoryAuthentication()
                .withUser("user").password("$2y$12$JC2kCe9r5GqKtQG/2JldZ.yXnE0vV7dAdTFtS61HR/vEje/hG3Hca").roles("USER")
                .and()
                .withUser("admin").password("$2y$12$JC2kCe9r5GqKtQG/2JldZ.yXnE0vV7dAdTFtS61HR/vEje/hG3Hca").roles("ADMIN");
    }
}
