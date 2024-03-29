package com.example.spring.controller;

import com.example.spring.config.JwtProperties;
import com.example.spring.domain.User;
import com.example.spring.dto.UserDto;
import com.example.spring.service.UserService;
import com.example.spring.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

  private final UserService userService;
  private final CookieUtil cookieUtil;
  private final JwtProperties jwtProperties;

  @Autowired
  public AuthController(final UserService userService, final CookieUtil cookieUtil, final JwtProperties jwtProperties) {
    this.userService = userService;
    this.cookieUtil = cookieUtil;
    this.jwtProperties = jwtProperties;
  }

  @PostMapping("/register")
  public ResponseEntity<Integer> register(@RequestBody final UserDto.RegisterRequest dto) {
    int id = userService.register(dto.getUsername(), dto.getPassword()).getId();
    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody final UserDto.LoginRequest dto, HttpServletResponse resp) {
    String token = userService.login(dto.getUsername(), dto.getPassword());
    cookieUtil.add(resp, jwtProperties.getName(), token, false, -1);
    return new ResponseEntity<>(token, HttpStatus.OK);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestAttribute("session") final User session, HttpServletResponse resp) {
    cookieUtil.clear(resp, jwtProperties.getName());
    return new ResponseEntity<>(session.getUsername(), HttpStatus.OK);
  }

  @GetMapping("/session")
  public User session(@RequestAttribute("session") final User session) {
    return session;
  }
}
