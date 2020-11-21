package com.demo.allframework.controller;

import com.demo.allframework.entity.SysUser;
import com.demo.allframework.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 用户信息表(SysUser)表控制层
 *
 * @author YUDI
 * @since 2020-04-28 22:57:28
 */
@Slf4j
@Controller
@RequestMapping("sysUser")
public class SysUserController {

    // private static final Logger log =  LoggerFactory.getLogger(SysUserController.class);

    @Resource
    private SysUserService userService;

    @ModelAttribute
    public SysUser getEntity(@RequestParam(value = "id",required = false) Long id){
        SysUser user;
        if(StringUtils.isEmpty(id)){
            user = new SysUser();
        }else{
            user = userService.queryById(id);
        }
        return user;
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public SysUser selectOne(Long id) {
        return userService.queryById(id);
    }

    @GetMapping("form")
    public String form( SysUser sysUser, ModelMap modelMap, Model model){
        if(sysUser.getId() == null){

        }
        // sysUser = userService.queryById(5L);

        modelMap.addAttribute("sysUser",sysUser);
        //
        log.info("info日志输出......{}",sysUser);
        log.debug("debug日志输出......");
        log.warn("warn日志输出......");
        log.error("error日志输出......");
        return "user/userForm";
    }

    @PostMapping("save")
    @ResponseBody
    public String save(@ModelAttribute SysUser user,HttpServletRequest request){
        int row = 0;
        if(user.getId() != null){
            user.setUpdateBy(user.getId().toString());
            user.setUpdateTime(new Date());
            row = userService.update(user);
        }else{
            user.setCreateBy("0");
            user.setCreateTime(new Date());
            row = userService.insert(user);
        }
        if(row > 0){
            return "保存成功!";
        }else{
            return "保存失败!";
        }
    }

    @GetMapping("query")
    @ResponseBody
    public String query(){
        SysUser user = userService.queryById(1L);
        System.out.println(user.toString());
        return "查询成功";
    }

}