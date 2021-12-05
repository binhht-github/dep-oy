package com.spring.controller.v1.sso;

import com.spring.security.oauth2.UserPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
//@RequestMapping()
public class SS0GoogleController {

    @GetMapping("/auth/google")
    public void startGoogleAuth(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Google Oauth Login Initiated");
                String uri = request.getRequestURI();
        System.out.println(uri);
    }

    // On Successful OAuth Google will return principal object
    // Principal Object Consist of username ,name ,email depending on scope mention in yml.

    @GetMapping("/oauth2/callback/google")
    public UserPrincipal callbackGoogle(UserPrincipal principal) {
        System.out.println("***** call back google *****");
        System.out.println(principal.getEmail());
        return principal;

    }
}
