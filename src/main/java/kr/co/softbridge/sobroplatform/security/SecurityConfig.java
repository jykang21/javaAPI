package kr.co.softbridge.sobroplatform.security;

import kr.co.softbridge.sobroplatform.config.jwt.JwtTokenProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    private JwtTokenProvider jwtTokenProvider;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "/favicon.ico"
                        , "/v2/api-docs"
                        , "/swagger-resources/**"
                        , "/swagger-ui.html"
                        , "/webjars/**"
                        , "/swagger/**"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
		        .httpBasic().disable() // REST API만을 고려, 기본 설정 해제
		        .csrf().disable() // csrf 사용 X
                .exceptionHandling()
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
