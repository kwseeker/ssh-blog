package top.kwseeker.sshblog.controller;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.kwseeker.sshblog.domain.Authority;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.service.AuthorityService;
import top.kwseeker.sshblog.service.UserService;
import top.kwseeker.sshblog.vo.Response;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


/**
 * 用户控制器.
 * 功能：
 * 1）查询所有用户列表
 * 2）获取form表单页面 及 新增用户
 * 3）删除用户
 * 4）获取用户修改页面及数据
 */
@RestController
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")	//基于角色的权限控制
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	UserService userService;

	@Autowired
	private AuthorityService authorityService;

	//===============================================================
	/**
	 * 分页显示模糊查询到的用户的列表
	 * @param async
	 * @param pageIndex 	页面编码
	 * @param pageSize		每页大小
	 * @param name 		模糊查询名字
	 * @param model
	 * @return
	 */
	@GetMapping
	@ApiOperation(value = "用户列表")
	public ModelAndView list(@RequestParam(value = "async", required = false) boolean async,
		 	@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
		 	@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
		 	@RequestParam(value = "name", required = false, defaultValue = "") String name,
		 	Model model) {

		logger.info("Request: /users Param: async={} pageIndex={} pageSize={} name={}", async, pageIndex, pageSize, name);

		Pageable pageable = new PageRequest(pageIndex, pageSize);
		Page<User> page = userService.listUsersByNameLike(name, pageable);
		List<User> userList = page.getContent(); //当前所在页面数据列表

		model.addAttribute("page", page);
		model.addAttribute("userList", userList);
		//TODO
		return new ModelAndView(async?"users/list :: #mainContainerReplace":"users/list",
				"userModel", model);
	}

	/**
	 * 获取form表单页面
	 */
	@GetMapping("/add")
	public ModelAndView createForm(Model model) {
		model.addAttribute("user", new User(null, null, null, null));
		return new ModelAndView("users/add", "userModel", model);
	}

	/**
	 * 新建用户
	 * @Param user 			用户数据
	 * @Param authorityId	用户权限编号
	 */
	@PostMapping
	@ApiOperation(value = "新建用户")
	public ResponseEntity<Response> create(User user, Long authorityId) {
		List<Authority> authorityList = new ArrayList<>();
		authorityList.add(authorityService.getAuthorityById(authorityId));
		user.setAuthorities(authorityList);

		if(user.getId() == null) {	//新建的用户
			user.setEncodePassword(user.getPassword());
		} else {	//旧用户数据更新
			//密码加密后再存入数据库
			User originalUser = userService.getUserById(user.getId());
			String rawPassword = originalUser.getPassword();
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodePasswd = encoder.encode(user.getPassword());
			boolean isMatch = encoder.matches(rawPassword, encodePasswd);
			if (!isMatch) {
				user.setEncodePassword(user.getPassword());
			}else {
				user.setPassword(user.getPassword());
			}
		}

		try {
			userService.saveUser(user);
		} catch (ConstraintViolationException e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		//TODO: ResponseEntity 与 @ResponseStatus
		return ResponseEntity.ok().body(new Response(true, "处理成功", user));
	}

	/**
	 * 删除用户
	 */
	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除用户")
	public ResponseEntity<Response> delete(@PathVariable("id") Long id, Model model) {
		try {
			userService.removeUser(id);
		} catch (Exception e) {
			return  ResponseEntity.ok().body( new Response(false, e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(true, "用户删除成功"));
	}

	/**
	 * 获取用户修改页面及数据
	 */
	@GetMapping("/edit/{id}")
	@ApiOperation(value = "获取修改用户界面")
	public ModelAndView modifyForm(@PathVariable("id") Long id, Model model) {
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		return new ModelAndView("users/edit", "userModel", model);
	}
}
