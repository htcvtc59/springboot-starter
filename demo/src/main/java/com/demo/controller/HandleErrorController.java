package com.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/")
public class HandleErrorController implements ErrorController {
    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                ModelAndView modelAndView = new ModelAndView("404");
                return modelAndView;
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                ModelAndView modelAndView = new ModelAndView("500");
                return modelAndView;
            }
        }
        ModelAndView modelAndView = new ModelAndView("403");
        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
