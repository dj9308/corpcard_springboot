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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                                .requestMatchers("/layouts", "/login", "/user/**").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/font/**", "/icons/**").permitAll()
                                .requestMatchers("/approval").hasRole("APPROVAL")
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
                .defaultSuccessUrl("/payhist", true)
                .successHandler((request, response, authentication) -> {
                    logger.info("Authentication successful");
                    response.sendRedirect("/");
                })
                .failureHandler((request, response, exception) -> {
                    logger.info("Authentication failure");
                    response.sendRedirect("/viewLogin?error=true");
                })
                .permitAll()
                //로그아웃
                .and()
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession();
                    session.invalidate();
                })
                .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/login"));

        return http.build();
    }
}
