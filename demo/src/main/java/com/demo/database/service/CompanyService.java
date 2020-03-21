package com.demo.database.service;

import com.demo.database.entities.Company;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {
    List<Company> findAll();
    List<Company> findByName(String name, Pageable pageable);
    Company findById(Long id);
    Company create(Company company);
    Company update(Company company);
    Long delete(Long id);
}
