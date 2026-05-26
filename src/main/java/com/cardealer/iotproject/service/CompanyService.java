package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.Company;
import com.cardealer.iotproject.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CompanyService {
    
    private static final Logger log = Logger.getLogger(CompanyService.class.getName());
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
    
    
    @Transactional(readOnly = true)
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Company getFirstCompany() {
        List<Company> companies = companyRepository.findAll();
        return companies.isEmpty() ? null : companies.get(0);
    }
    
    @Transactional
    public Company saveCompany(Company company) {
        if (company.getCompanyId() == null) {
            log.info("Creating new company: " + company.getCompanyName());
        } else {
            log.info("Updating company: " + company.getCompanyName());
        }
        return companyRepository.save(company);
    }
    
    @Transactional
    public Company updateCompany(Long id, Company companyDetails) {
        Company company = getCompanyById(id);
        company.setCompanyName(companyDetails.getCompanyName());
        company.setRnc(companyDetails.getRnc());
        company.setAddress(companyDetails.getAddress());
        company.setCity(companyDetails.getCity());
        company.setProvincia(companyDetails.getProvincia());
        company.setZipCode(companyDetails.getZipCode());
        company.setPhone(companyDetails.getPhone());
        company.setCell(companyDetails.getCell());
        company.setContact(companyDetails.getContact());
        company.setEmailAddr(companyDetails.getEmailAddr());
        company.setWebsite(companyDetails.getWebsite());
        company.setApplicationTitle(companyDetails.getApplicationTitle());
        company.setLogoPath(companyDetails.getLogoPath());

          company.setAboutus(companyDetails.getAboutus());
         company.setOurmission(companyDetails.getOurmission());
        return companyRepository.save(company);
    }
    
    @Transactional
    public void deleteCompany(Long id) {
        Company company = getCompanyById(id);
        companyRepository.delete(company);
        log.info("Deleted company: " + company.getCompanyName());
    }
    
    @Transactional(readOnly = true)
    public boolean companyExists() {
        return companyRepository.count() > 0;
    }
}