package com.study.club.config;

import com.nimbusds.jwt.JWT;
import com.study.club.security.filter.ApiCheckFilter;
import com.study.club.security.filter.ApiLoginFilter;
import com.study.club.security.handler.ApiLoginFailHandler;
import com.study.club.security.handler.ClubLoginSuccessHandler;
import com.study.club.security.service.ClubUserDetailsService;
import com.study.club.security.util.JWTUtil;
import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

// SecurityConfig 클래스는 시큐리티 관련 기능을 쉽게 설정하기 위해서 WebSecurityConfigurerAdapter 라는 클래스를 상속으로 처리, 주로 overRide 를 통해 여러 설정을 조정하게 된다.
@Configuration
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClubUserDetailsService userDetailsService;

    @Bean
    PasswordEncoder passwordEncoder() {
        // 패스워드를 인코딩하는 것인데 주 목적은 패스워드를 암호화 하는 것.
        // BCryptPasswordEncoder는 'bcrypt'라는 해시 함수를 이용해서 패스워드를 암호화하는 목족으로 설계된 클래스.
        // 복호화 불가능
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApiCheckFilter apiCheckFilter() {
        return new ApiCheckFilter("/notes/**/*", jwtUtil());
    }

    @Bean
    public ApiLoginFilter apiLoginFilter() throws Exception {
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/api/login", jwtUtil());
        apiLoginFilter.setAuthenticationManager(authenticationManager());
        apiLoginFilter.setAuthenticationFailureHandler(new ApiLoginFailHandler());
        return apiLoginFilter;
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // /sample/all 은 모든 사용자 인가
        // /sample/member 은 USER 권한을 가진 사람만
//        http.authorizeRequests().antMatchers("/sample/all").permitAll()
//                .antMatchers("/h2-console/**").permitAll()
//                .antMatchers("/sample/member").hasRole("USER");

        // 인증/인가 절차에 문제가 발생했을 때 로그인 페이지를 보여주도록 지정
        // 별도의 디자인을 적용하기 위해서는 추가적인 설정이 필요
        // loginPage(), loginProcessUrl(), defaultSuccessUrl(), failureUrl() 등을 이용해서 필요한 설정을 지정할 수 있다.
        // 대부분의 애플리케이션은 고유한 디자인을 적용하기 때문에 loginPage()를 사용해서 별도의 로그인 페이지를 이용하는 경우가 많다.
        http.formLogin();
        http.csrf().ignoringAntMatchers("/h2-console/**").disable();
        http.logout();
        http.oauth2Login().successHandler(successHandler());
        http.rememberMe().tokenValiditySeconds(60*60*24*7); // 7days
        http.userDetailsService(userDetailsService);

        // 필터의 순서를 변경
        http.addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    private AuthenticationSuccessHandler successHandler() {
        return new ClubLoginSuccessHandler(passwordEncoder());
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 사용자 계정은 user1
//        auth.inMemoryAuthentication()
//                .withUser("user1")
//                .password("$2a$10$ib84P.GC3t8p3vebneSRL.bZk2UUFPnYS/.6adu21rLPSGgjYesGq")
//                .roles("USER");
//    }
}
