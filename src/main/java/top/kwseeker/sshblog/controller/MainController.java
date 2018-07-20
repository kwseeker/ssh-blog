package top.kwseeker.sshblog.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import top.kwseeker.sshblog.domain.Authority;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.service.AuthorityService;
import top.kwseeker.sshblog.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页控制器.
 */
@Controller
public class MainController {

	private static final Long ROLE_USER_AUTHORITY_ID = 2L;

	@Autowired
	private UserService userService;
	@Autowired
	private AuthorityService authorityService;

	@GetMapping("/")
	public String root() {
		return "redirect:/index";
	}
	
	@GetMapping("/index")
	@ApiOperation(value = "系统主页")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	@ApiOperation(value = "登录页面")
	public String login() {
		return "login";
	}

	@GetMapping("/login-error")
	@ApiOperation(value = "登录异常")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登陆失败，用户名或者密码错误！");
		return "login";
	}
	
	@GetMapping("/register")
	@ApiOperation(value = "注册页面")
	public String register() {
		return "register";
	}

	@PostMapping("/register")
	@ApiOperation(value = "用户注册")
	public String registerUser(User user) {
		List<Authority> authorityList = new ArrayList<>();
		authorityList.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
		user.setAuthorities(authorityList);
		userService.saveUser(user);
		return "redirect:/login";
	}

	@GetMapping("/search")
	@ApiOperation(value = "")
	public String search() {
		return "search";
	}
}
