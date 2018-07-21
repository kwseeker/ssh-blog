package top.kwseeker.sshblog.controller;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import top.kwseeker.sshblog.vo.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户设置（用户的增删改查、角色修改、博客修改）.
 */
@Controller
@RequestMapping("/admins")
public class AdminController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取后台管理主页面
	 * @return
	 */
	@GetMapping
	@ApiOperation(value = "后台管理")
	public ModelAndView listUsers(Model model) {
		List<Menu> list = new ArrayList<>();
		list.add(new Menu("用户管理", "/users"));
		list.add(new Menu("角色管理", "/roles"));
		list.add(new Menu("博客管理", "/blogs"));
		list.add(new Menu("评论管理", "/commits"));
		model.addAttribute("list", list);

		//TODO: 跟下代码看看是哪个ViewResolver处理ModelAndView的以及怎么处理的？
		return new ModelAndView("/admins/index", "model", model);
	}

}
