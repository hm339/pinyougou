package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 获取当前登录用户的用户名
     * @return
     */
    @RequestMapping("/showName")
    public Map showName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map map = new HashMap();
        map.put("username", name);

        return map;
    }
}
