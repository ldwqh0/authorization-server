package com.xyyh.authorization.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/oauth/authorize")
public class AuthorizationEndpoint {

    /**
     * 返回授权页面
     *
     * @param model
     * @param sessionStatus
     * @param principal
     * @return
     */
    @GetMapping
    public ModelAndView authorize(
        Map<String, Object> model,
        @RequestParam Map<String, String> params,
        @ModelAttribute AuthorizationRequest authorizationRequest,
        SessionStatus sessionStatus,
        Principal principal) {
        return new ModelAndView("/oauth/confirm_access", model);
    }

    /**
     * 提交授权验证请求
     *
     * @param approvalParameters 授权验证参数
     * @param model
     * @param sessionStatus
     * @param principal          当前用户信息
     * @return
     */
    @PostMapping
    public View approveOrDeny(
        @RequestParam Map<String, String> approvalParameters,
        Map<String, ?> model,
        SessionStatus sessionStatus,
        Principal principal) {

        return null;
    }
}
