package com.expernet.corpcard.config;

import com.expernet.corpcard.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Security Configure Class
 *
 * @author (주)엑스퍼넷 설동재
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.12.07	설동재	최초 생성
 *
 * </pre>
 * @since 2022.12.07
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Login Service
     */
    @Autowired
    private LoginService loginService;

    /**
     * UserDetailsService Bean Setting
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return loginService;
    }

    /**
     * BCrypt Encoder Bean Setting
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Connfiguration Setting
     *
     * @param http : HttpSecurity
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        //global
        http
                .csrf().disable()
                .userDetailsService(loginService)
                .authorizeHttpRequests((auth) -> {
                    try {
                        auth
                                .requestMatchers("/login", "/common/**", "/fragments/**").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/font/**", "/icons/**").permitAll()
                                .requestMatchers("/approval").hasRole("CHIEF")
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .anyRequest().authenticated();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                //로그인
                .formLogin()
                .loginPage("/viewLogin")
                .usernameParameter("userId")
                .passwordParameter("userPasswd")
                .loginProcessingUrl("/login_proc")
                .successHandler((request, response, authentication) -> {
                    Collection<GrantedAuthority> credentials = (Collection<GrantedAuthority>) authentication.getAuthorities();
                    if(credentials.stream().anyMatch(a->a.getAuthority().equals("ROLE_CHIEF"))){
                        response.sendRedirect("/approval");
                    }else{
                        response.sendRedirect("/payhist");
                    }
                })
                .failureHandler((request, response, exception) -> {
                    String errorMsg = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
                    request.setAttribute("errorMsg", errorMsg);
                    request.getRequestDispatcher("/viewLogin").forward(request, response);
                })
                .permitAll()
            .and()
                //로그아웃
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession();
                    session.invalidate();
                })
                .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/viewLogin"));
        return http.build();
    }
}
