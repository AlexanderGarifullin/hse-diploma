package hse.dss.controller;

import hse.dss.entity.User;
import hse.dss.entity.UserDetails;
import hse.dss.repository.UserRepository;
import hse.dss.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.*;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(name="error", required=false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Неверное имя пользователя или пароль");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("user") User u,
                             org.springframework.validation.BindingResult br,
                             HttpServletResponse resp) {
        // Удаляем пробелы у имени пользователя
        u.setUsername(u.getUsername() != null ? u.getUsername().trim() : null);

        if (userRepository.findByUsername(u.getUsername()).isPresent()) {
            br.rejectValue("username", "taken", "Имя занято");
            return "register";
        }

        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepository.save(u);

        String token = jwtService.generateToken(new UserDetails(u));
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        resp.addCookie(cookie);

        return "redirect:/webclient/tasks";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpServletResponse resp) {
        username = username.trim();

        Optional<User> optUser = userRepository.findByUsername(username);

        if (optUser.isEmpty() ||
                !passwordEncoder.matches(password, optUser.get().getPassword())) {
            return "redirect:/webclient/auth/login?error";
        }

        User user = optUser.get();
        String token = jwtService.generateToken(new UserDetails(user));
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        resp.addCookie(cookie);

        return "redirect:/webclient/tasks";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/webclient/auth/login";
    }
}
