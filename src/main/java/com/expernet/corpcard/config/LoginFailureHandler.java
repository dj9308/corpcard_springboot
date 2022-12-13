package com.expernet.corpcard.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMsg = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";

        request.setAttribute("errorMsg", errorMsg);

        request.getRequestDispatcher("/viewLogin").forward(request, response);
    }
}
