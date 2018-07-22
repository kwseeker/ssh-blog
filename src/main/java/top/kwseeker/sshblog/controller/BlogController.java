package top.kwseeker.sshblog.controller;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.domain.es.EsBlog;
import top.kwseeker.sshblog.service.EsBlogService;
import top.kwseeker.sshblog.vo.TagVO;

import java.util.List;

/**
 * 主页控制器.
 * GET /blogs			从 ES 中分页查询按order排序的数据
 * GET /blogs/newest	从 ES 中查询最新的五条博客的数据
 * GET /blogs/hotest	从 ES 中查询最火的五条博客的数据
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EsBlogService esBlogService;

	@ApiOperation(value = "博客列表")
	@GetMapping
	public String listEsBlogs(
			@RequestParam(value = "order", required = false, defaultValue = "new") String order,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "async", required = false) boolean async,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			Model model) {

		Page<EsBlog> page = null;
		List<EsBlog> list = null;
		boolean isEmpty = true;
		try {
			if(order.equals("hot")) {
				Sort sort = new Sort(Sort.Direction.DESC,"readSize", "commentSize", "voteSize", "createTime");
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = esBlogService.listHotestEsBlogs(keyword, pageable);
			} else if(order.equals("new")) {
				Sort sort = new Sort(Sort.Direction.DESC, "createTime");
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = esBlogService.listNewestEsBlogs(keyword, pageable);
			}

			isEmpty = false;
		} catch (Exception e) {
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = esBlogService.listEsBlogs(pageable);
		}

		list = page.getContent();

		model.addAttribute("order", order);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("blogList", list);

		if(!async && !isEmpty) {
			List<EsBlog> newest = esBlogService.listTop5NewestEsBlogs();
			model.addAttribute("newest", newest);
			List<EsBlog> hotest = esBlogService.listTop5HotestEsBlogs();
			model.addAttribute("hotest", hotest);
			List<TagVO> tags = esBlogService.listTop30Tags();
			model.addAttribute("tags", tags);
			List<User> users = esBlogService.listTop12Users();
			model.addAttribute("users", users);
		}

		return (async?"/index ::  #mainContainerRepleace":"/index");
	}

//	@GetMapping
//	public String listBlogs(@RequestParam(value="order",required=false,defaultValue="new") String order,
//			@RequestParam(value="tag",required=false) Long tag) {
//		logger.info("order: {}, tag: {}", order, tag);
//		return "redirect:/index?order="+order+"&tag="+tag;
//	}

	@ApiOperation(value = "最新博客列表")
	@GetMapping("/newest")
	public String listNewestEsBlogs(Model model) {
		List<EsBlog> newest = esBlogService.listTop5NewestEsBlogs();
		model.addAttribute("newest", newest);
		return "newest";
	}

	@ApiOperation(value = "最热博客列表")
	@GetMapping("/hotest")
	public String listHotestEsBlogs(Model model) {
		List<EsBlog> hotest = esBlogService.listTop5HotestEsBlogs();
		model.addAttribute("hotest", hotest);
		return "hotest";
	}
}
