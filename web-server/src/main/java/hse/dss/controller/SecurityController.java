package hse.dss.controller;

import hse.dss.entity.User;
import hse.dss.entity.UserDetails;
import hse.dss.repository.UserRepository;
import hse.dss.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model m) {
        m.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("user") User u, BindingResult br, HttpServletResponse resp) {
        if (userRepository.findByUsername(u.getUsername()).isPresent()) {
            br.rejectValue("username", "taken", "Имя занято");
            return "register";
        }

        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepository.save(u);
        String token = jwtService.generateToken(new UserDetails(u));

        var cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        resp.addCookie(cookie);
        return "redirect:/webclient/tasks";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpServletResponse resp) {
        var user = userRepository.findByUsername(username)
                .orElseThrow();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/login?error";
        }
        String token = jwtService.generateToken(new UserDetails(user));

        var cookie = new Cookie("JWT", token);
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
