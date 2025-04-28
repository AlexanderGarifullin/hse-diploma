package hse.dss.controller;

import hse.dss.entity.User;
import hse.dss.entity.UserDetails;
import hse.dss.repository.UserRepository;
import hse.dss.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.*;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class SecurityController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(name="error", required=false) String error,
                            Model model) {
        if (error != null) {
            log.warn("Login error occurred");
            model.addAttribute("loginError", "Неверное имя пользователя или пароль");
        }
        log.info("Opening login page");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        log.info("Opening registration page");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("user") User u,
                             org.springframework.validation.BindingResult br,
                             HttpServletResponse resp) {
        // Удаляем пробелы у имени пользователя
        u.setUsername(u.getUsername() != null ? u.getUsername().trim() : null);

        log.info("Attempting to register user: {}", u.getUsername());

        if (userRepository.findByUsername(u.getUsername()).isPresent()) {
            log.warn("Registration failed: username '{}' is already taken", u.getUsername());
            br.rejectValue("username", "taken", "Имя занято");
            return "register";
        }

        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepository.save(u);

        log.info("User '{}' successfully registered", u.getUsername());

        String token = jwtService.generateToken(new UserDetails(u));
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        resp.addCookie(cookie);

        log.info("JWT token generated and cookie set for user '{}'", u.getUsername());

        return "redirect:/webclient/tasks";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpServletResponse resp) {
        username = username.trim();

        log.info("Attempting login for username: {}", username);

        Optional<User> optUser = userRepository.findByUsername(username);

        if (optUser.isEmpty() ||
                !passwordEncoder.matches(password, optUser.get().getPassword())) {
            log.warn("Login failed for user {}", username);
            return "redirect:/webclient/auth/login?error";
        }

        User user = optUser.get();
        String token = jwtService.generateToken(new UserDetails(user));
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        resp.addCookie(cookie);

        log.info("User '{}' successfully logged in, JWT cookie set", username);

        return "redirect:/webclient/tasks";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("User logging out");

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("Invalidating user session");
            session.invalidate();
        }

        return "redirect:/webclient/auth/login";
    }
}
