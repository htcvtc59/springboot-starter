package com.demo.database.service.impl;

import com.demo.database.repositories.CompanyPageReponsitory;
import com.demo.database.repositories.CompanyRepository;
import com.demo.database.entities.Company;
import com.demo.database.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;

    @Autowired
    public void setCompanyRepository(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    private CompanyPageReponsitory companyPageReponsitory;

    @Autowired
    public void setCompanyPageReponsitory(CompanyPageReponsitory companyPageReponsitory) {
        this.companyPageReponsitory = companyPageReponsitory;
    }

    @Override
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        this.companyRepository.findAll().forEach(e -> companies.add(e));
        return companies;
    }


    @Override
    public List<Company> findByName(String name, Pageable pageable) {
        Page<Company> pagedResult = this.companyPageReponsitory.findByName(name, pageable);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Company>();
        }
    }

    @Override
    public Company findById(Long id) {
        Company companies = this.companyRepository.findById(id).get();
        return companies;
    }

    @Override
    public synchronized Company create(Company company) {
        Pageable paging = PageRequest.of(0, 2);
        List<Company> list = findByName(company.getName(), paging);
        if (list.size() > 0) {
            return company;
        } else {
            this.companyRepository.save(company);
            return company;
        }
    }

    @Override
    public Company update(Company company) {
        Company found = this.companyRepository.findById(company.getId()).get();
        if (found.getId() > 0) {
            this.companyRepository.save(company);
            return company;
        } else {
            return new Company();
        }
    }

    @Override
    public Long delete(Long id) {
        this.companyRepository.delete(findById(id));
        return id;
    }
}
