package top.kwseeker.sshblog.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.kwseeker.sshblog.domain.Blog;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.domain.Vote;
import top.kwseeker.sshblog.service.BlogService;
import top.kwseeker.sshblog.service.CatalogService;
import top.kwseeker.sshblog.service.UserService;
import top.kwseeker.sshblog.util.ConstraintViolationExceptionHandler;
import top.kwseeker.sshblog.vo.Response;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 用户主页控制器.
 * /u/{username} 重定向到 /u/{username}/blogs
 *
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private BlogService blogService;

	@Autowired
	private CatalogService catalogService;

	@GetMapping("/{username}")
	@ApiOperation(value = "用户主页")
	public String userSpace(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return "redirect:/u/" + username + "/blogs";
	}
 
	@GetMapping("/{username}/blogs")
	@ApiOperation(value = "用户主页博客列表")
	public String listBlogsByOrder(
			@PathVariable("username") String username,
			@RequestParam(value = "order",required = false,defaultValue = "new") String order,
			@RequestParam(value = "catalog",required = false ) Long catalogId,
			@RequestParam(value = "keyword",required = false, defaultValue = "") String keyword,
			@RequestParam(value = "async", required = false) boolean async,
			@RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize",required = false,defaultValue = "10") int pageSize,
			Model model) {
		
		User user = (User)userDetailsService.loadUserByUsername(username);
		Page<Blog> page = null;

		if(catalogId != null && catalogId > 0) {			//分类查询
			Catalog catalog = catalogService.getCatalogById(catalogId);
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = blogService.listBlogsByCatalog(catalog, pageable);
			order = "";
		} else if(order.equals("hot")) {		//最热查询
			Sort sort = new Sort(Sort.Direction.DESC, "readSize", "commentSize", "voteSize");
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
		} else if(order.equals("new")) {		//最新查询
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = blogService.listBlogsByTitleVote(user, keyword, pageable);
		}

		List<Blog> list = page.getContent();

		model.addAttribute("user", user);
		model.addAttribute("order", order);
		model.addAttribute("catalogId", catalogId);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("blogList", list);
		return (async?"/userspace/u :: #mainContainerRepleace":"/userspace/u");
	}
	
	@GetMapping("/{username}/profile")
	@ApiOperation(value = "用户设置")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView profile(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("/userspace/profile", "userModel", model);
	}

	/**
	 * 保存个人设置
	 */
	@PostMapping("/{username}/profile")
	@ApiOperation(value = "保存用户设置")
	@PreAuthorize("authentication.name.equals(#username)")
	public String saveProfile(@PathVariable("username") String username, User user) {
		User originalUser = userService.getUserById(user.getId());
		originalUser.setEmail(user.getEmail());
		originalUser.setName(user.getName());

		String rawPassword = originalUser.getPassword();
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePassword = encoder.encode(user.getPassword());
		boolean isMatch = encoder.matches(rawPassword, encodePassword);
		if (!isMatch) {
			originalUser.setEncodePassword(user.getPassword());
		}

		userService.saveUser(originalUser);
		return "redirect:/u/" + username + "/profile";
	}

	//返回头像编辑页面附带要修改的用户信息
	@GetMapping("/{username}/avatar")
	@ApiOperation(value = "用户头像")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView avatar(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("/userspace/avatar", "userModel", model);
	}

	//保存头像
	@PostMapping("/{username}/avatar")
	@ApiOperation(value = "保存用户头像")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
		String avatarUrl = user.getAvatar();

		User originalUser = userService.getUserById(user.getId());
		originalUser.setAvatar(avatarUrl);
		userService.saveUser(originalUser);

		return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
	}

	//返回用户页博客展示页面
	@GetMapping("/{username}/blogs/{id}")
	@ApiOperation(value = "通过ID获取用户博客")
	public String getBlogById(@PathVariable("username") String username,
							  @PathVariable("id") Long id,
							  Model model) {
		User principal = null;
		Blog blog = blogService.getBlogById(id);

		blogService.readingIncrease(id);

		//判断操作用户是否是博客的所有者
		boolean isBlogOwner = false;
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				&&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal !=null && username.equals(principal.getUsername())) {
				isBlogOwner = true;
			}
		}

		// 判断操作用户的点赞情况
		List<Vote> votes = blog.getVotes();
		Vote currentVote = null; // 当前用户的点赞情况

		if (principal !=null) {
			for (Vote vote : votes) {
				vote.getUser().getUsername().equals(principal.getUsername());
				currentVote = vote;
				break;
			}
		}

		model.addAttribute("isBlogOwner", isBlogOwner);
		model.addAttribute("blogModel",blog);
		model.addAttribute("currentVote",currentVote);

		return "/userspace/blog";
	}

	//删除博客
	@DeleteMapping("/{username}/blogs/{id}")
	@ApiOperation(value = "通过ID删除用户博客")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username,@PathVariable("id") Long id) {

		try {
			blogService.removeBlog(id);
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/u/" + username + "/blogs";
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

	//获取新增博客的界面
	@GetMapping("/{username}/blogs/edit")
	@ApiOperation(value = "新建博客")
	public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
		User user = (User)userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);

		model.addAttribute("blog", new Blog(null, null, null));
		model.addAttribute("catalogs", catalogs);
		return new ModelAndView("/userspace/blogedit", "blogModel", model);
	}

	//获取编辑博客的界面
	@GetMapping("/{username}/blogs/edit/{id}")
	@ApiOperation(value = "重新编辑博客")
	public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
		// 获取用户分类列表
		User user = (User)userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);

		model.addAttribute("blog", blogService.getBlogById(id));
		model.addAttribute("catalogs", catalogs);
		return new ModelAndView("/userspace/blogedit", "blogModel", model);
	}

	//保存博客
	@PostMapping("/{username}/blogs/edit")
	@ApiOperation(value = "保存博客")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
		// 对 Catalog 进行空处理
		if (blog.getCatalog().getId() == null) {
			return ResponseEntity.ok().body(new Response(false,"未选择分类"));
		}
		try {
			// 判断是修改还是新增
			if (blog.getId()!=null) {
				Blog originalBlog = blogService.getBlogById(blog.getId());
				originalBlog.setTitle(blog.getTitle());
				originalBlog.setContent(blog.getContent());
				originalBlog.setSummary(blog.getSummary());
				originalBlog.setCatalog(blog.getCatalog());
				originalBlog.setTags(blog.getTags());
				blogService.saveBlog(originalBlog);
			} else {
				User user = (User)userDetailsService.loadUserByUsername(username);
				blog.setUser(user);
				blogService.saveBlog(blog);
			}
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

}
