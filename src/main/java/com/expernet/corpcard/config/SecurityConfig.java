package com.expernet.corpcard.config;

import com.expernet.corpcard.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
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
     * Login Service
     */
    @Autowired
    private LoginService loginService;
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    public SecurityConfig(LoginService loginService, TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.loginService = loginService;
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
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
        http    //global
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                //세션 Stateless 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //인증 & 페이지별 권한 설정
                .and()
                .userDetailsService(loginService)
                .authorizeHttpRequests((auth) -> {
                    try {
                        auth    //권한에 따른 접근 제한
                                .requestMatchers("/login", "/fragments/**").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/font/**", "/icons/**").permitAll()
                                .requestMatchers("/approval").hasRole("CHIEF")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                                //jwt 인증으로 설정
                                .and()
                                .apply(new JwtSecurityConfig(tokenProvider));
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
                .successHandler(new JwtAuthenticationSuccessHandler(tokenProvider))
//                .successHandler((request, response, authentication) -> {
//                    Collection<GrantedAuthority> credentials = (Collection<GrantedAuthority>) authentication.getAuthorities();
//                    if (credentials.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CHIEF"))) {
//                        response.sendRedirect("/approval");
//                    } else {
//                        response.sendRedirect("/payhist");
//                    }
//                })
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
