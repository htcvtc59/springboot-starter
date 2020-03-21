package com.demo.controller;

import com.demo.database.entities.Company;
import com.demo.database.service.CompanyService;
import com.demo.response.DataResponse;
import com.demo.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/dash")
public class DatabaseController {

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public ModelAndView List() {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView("dash");
            List<Company> companies = companyService.findAll();
            mav.addObject("companies", companies);
            mav.addObject("company", new Company());
            return mav;
        } catch (Exception ex) {
        }
        return mav;
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.POST)
    public ModelAndView Create(@ModelAttribute Company company, HttpSession session) {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView("dash");
            if (company.getName().length() > 0 &&
                    company.getPrivateKey().length() > 0 &&
                    company.getSecretKey().length() > 0) {
                Company Createcompany = companyService.create(company);
            }
            List<Company> companies = companyService.findAll();
            mav.addObject("companies", companies);
            mav.addObject("company", new Company());
            return mav;
        } catch (Exception ex) {
        }
        return mav;
    }

    @RequestMapping(value = {"/delete/{id}"}, method = RequestMethod.GET)
    public ModelAndView Delete(@PathVariable Long id) {
        ModelAndView mav = null;
        try {
            System.out.println(id);
            mav = new ModelAndView("dash");
            if (id > 0) {
                companyService.delete(id);
            }
            List<Company> companies = companyService.findAll();
            mav.addObject("companies", companies);
            mav.addObject("company", new Company());
            return mav;
        } catch (Exception ex) {
        }
        return mav;
    }

    @RequestMapping(value = {"/get/{id}"}, method = RequestMethod.GET)
    public ModelAndView Get(@PathVariable Long id) {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView("dash");
            if (id > 0) {
                Company company = companyService.findById(id);
                mav.addObject("company", company);
                mav.addObject("updateModal", "updateModal");
            }
            List<Company> companies = companyService.findAll();
            mav.addObject("companies", companies);
            return mav;
        } catch (Exception ex) {
        }
        return mav;
    }

    @RequestMapping(value = {"/update/{id}"}, method = RequestMethod.POST)
    public ModelAndView Get(@PathVariable Long id, @ModelAttribute Company company) {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView("dash");
            if (id > 0) {
                Company byId = companyService.findById(id);
                byId.setName(company.getName());
                byId.setPrivateKey(company.getPrivateKey());
                byId.setSecretKey(company.getSecretKey());
                companyService.update(byId);
            }
            List<Company> companies = companyService.findAll();
            mav.addObject("companies", companies);
            mav.addObject("company", new Company());
            return mav;
        } catch (Exception ex) {
        }
        return mav;
    }


    @RequestMapping(value = {"/get/name"}, method = RequestMethod.GET)
    @ResponseBody
    private DataResponse findByName(@RequestParam String name,
                                    @RequestParam int offset, @RequestParam int limit) {
        try {
            if (name.length() == 0) {
                Pageable paging = PageRequest.of(0, 5);
                List<Company> companies = companyService.findByName("", paging);
                return new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL", companies, DataResponse.DataType.NORMAL);
            }

            Pageable paging = PageRequest.of(offset, limit);
            List<Company> companies = companyService.findByName(name, paging);
            return new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL", companies, DataResponse.DataType.NORMAL);
        } catch (Exception ex) {
        }
        return DataResponse.FAILED;
    }

}
