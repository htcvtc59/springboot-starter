package com.demo.database.repositories;

import com.demo.database.entities.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPageReponsitory extends PagingAndSortingRepository<Company, Long> {
    @Query("select c from Company c where c.name like %?1% order by c.name")
    Page<Company> findByName(String name, Pageable pageable);
}
