package top.kwseeker.sshblog.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.service.CatalogService;
import top.kwseeker.sshblog.util.ConstraintViolationExceptionHandler;
import top.kwseeker.sshblog.vo.CatalogVO;
import top.kwseeker.sshblog.vo.Response;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Controller
@RequestMapping("/catalogs")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 获取分类列表
     */
    @GetMapping
    @ApiOperation(value = "分类列表")
    public String listComments(@RequestParam(value="username",required=true) String username, Model model) {
        User user = (User)userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        // 判断操作用户是否是分类的所有者
        boolean isOwner = false;

        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        model.addAttribute("isCatalogsOwner", isOwner);
        model.addAttribute("catalogs", catalogs);
        return "/userspace/u :: #catalogRepleace";
    }
    /**
     * 发表分类
     */
    @PostMapping
    @ApiOperation(value = "发表分类")
    @PreAuthorize("authentication.name.equals(#catalogVO.username)")// 指定用户才能操作方法
    public ResponseEntity<Response> create(@RequestBody CatalogVO catalogVO) {

        String username = catalogVO.getUsername();
        Catalog catalog = catalogVO.getCatalog();

        User user = (User)userDetailsService.loadUserByUsername(username);

        try {
            catalog.setUser(user);
            catalogService.saveCatalog(catalog);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", null));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除分类")
    @PreAuthorize("authentication.name.equals(#username)")  // 指定用户才能操作方法
    public ResponseEntity<Response> delete(String username, @PathVariable("id") Long id) {
        try {
            catalogService.removeCatalog(id);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", null));
    }

    /**
     * 获取分类编辑界面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit")
    @ApiOperation(value = "编辑分类")
    public String getCatalogEdit(Model model) {
        Catalog catalog = new Catalog(null, null);
        model.addAttribute("catalog",catalog);
        return "/userspace/catalogedit";
    }
    /**
     * 根据 Id 获取分类信息
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit/{id}")
    @ApiOperation(value = "通过ID获取分类")
    public String getCatalogById(@PathVariable("id") Long id, Model model) {
        Catalog catalog = catalogService.getCatalogById(id);
        model.addAttribute("catalog",catalog);
        return "/userspace/catalogedit";
    }

}
