package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.BranchDTO;
import com.cardealer.iotproject.model.entity.Branch;
import com.cardealer.iotproject.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchService {
    
    @Autowired
    private BranchRepository branchRepository;
    
    /**
     * Convert Entity to DTO
     */
    private BranchDTO convertToDTO(Branch branch) {
        if (branch == null) return null;
        
        BranchDTO dto = new BranchDTO();
        dto.setBranchId(branch.getBranchId());
        dto.setBranchName(branch.getBranchName());
        dto.setManagerName(branch.getManagerName());
        dto.setManagerCell(branch.getManagerCell());
        dto.setManagerEmail(branch.getManagerEmail());
        dto.setProfileImagePath(branch.getProfileImagePath());
        dto.setAddress(branch.getAddress());
        dto.setCity(branch.getCity());
        dto.setProvincia(branch.getProvincia());
        dto.setZipCode(branch.getZipCode());
        dto.setPhone(branch.getPhone());
        dto.setEmail(branch.getEmail());
        dto.setIsActive(branch.getIsActive());
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setUpdatedAt(branch.getUpdatedAt());
        return dto;
    }
    
    /**
     * Convert DTO to Entity
     */
    private Branch convertToEntity(BranchDTO dto) {
        if (dto == null) return null;
        
        Branch branch = new Branch();
        branch.setBranchName(dto.getBranchName());
        branch.setManagerName(dto.getManagerName());
        branch.setManagerCell(dto.getManagerCell());
        branch.setManagerEmail(dto.getManagerEmail());
        branch.setProfileImagePath(dto.getProfileImagePath());
        branch.setAddress(dto.getAddress());
        branch.setCity(dto.getCity());
        branch.setProvincia(dto.getProvincia());
        branch.setZipCode(dto.getZipCode());
        branch.setPhone(dto.getPhone());
        branch.setEmail(dto.getEmail());
        branch.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return branch;
    }
    
    /**
     * Get all branches
     */
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAllByOrderByBranchNameAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get only active branches
     */
    public List<BranchDTO> getActiveBranches() {
        return branchRepository.findByIsActiveTrueOrderByBranchNameAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get branch by ID
     */
    public BranchDTO getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
        return convertToDTO(branch);
    }
    
    /**
     * Create a new branch
     */
    @Transactional
    public BranchDTO createBranch(BranchDTO branchDTO) {
        // Validate branch name is not empty
        if (branchDTO.getBranchName() == null || branchDTO.getBranchName().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la sucursal es requerido");
        }
        
        // Check if branch name already exists
        if (branchRepository.existsByBranchNameIgnoreCase(branchDTO.getBranchName().trim())) {
            throw new RuntimeException("Ya existe una sucursal con el nombre: " + branchDTO.getBranchName());
        }
        
        Branch branch = convertToEntity(branchDTO);
        branch.setBranchName(branchDTO.getBranchName().trim());
        branch = branchRepository.save(branch);
        return convertToDTO(branch);
    }
    
    /**
     * Update an existing branch
     */
    @Transactional
    public BranchDTO updateBranch(Long id, BranchDTO branchDTO) {
        Branch existingBranch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
        
        // Validate branch name is not empty
        if (branchDTO.getBranchName() == null || branchDTO.getBranchName().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la sucursal es requerido");
        }
        
        // Check if branch name already exists (excluding current branch)
        if (!existingBranch.getBranchName().equalsIgnoreCase(branchDTO.getBranchName()) &&
            branchRepository.existsByBranchNameIgnoreCase(branchDTO.getBranchName().trim())) {
            throw new RuntimeException("Ya existe una sucursal con el nombre: " + branchDTO.getBranchName());
        }
        
        existingBranch.setBranchName(branchDTO.getBranchName().trim());
        existingBranch.setManagerName(branchDTO.getManagerName());
        existingBranch.setManagerCell(branchDTO.getManagerCell());
        existingBranch.setManagerEmail(branchDTO.getManagerEmail());
        existingBranch.setProfileImagePath(branchDTO.getProfileImagePath());
        existingBranch.setAddress(branchDTO.getAddress());
        existingBranch.setCity(branchDTO.getCity());
        existingBranch.setProvincia(branchDTO.getProvincia());
        existingBranch.setZipCode(branchDTO.getZipCode());
        existingBranch.setPhone(branchDTO.getPhone());
        existingBranch.setEmail(branchDTO.getEmail());
        existingBranch.setIsActive(branchDTO.getIsActive());
        
        existingBranch = branchRepository.save(existingBranch);
        return convertToDTO(existingBranch);
    }
    
    /**
     * Delete a branch
     */
    @Transactional
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
        branchRepository.delete(branch);
    }
    
    /**
     * Toggle branch active status
     */
    @Transactional
    public BranchDTO toggleBranchStatus(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
        branch.setIsActive(!branch.getIsActive());
        branch = branchRepository.save(branch);
        return convertToDTO(branch);
    }
    
    /**
     * Get total count of branches
     */
    public long getTotalBranchCount() {
        return branchRepository.count();
    }
    
    /**
     * Get active branches count
     */
    public long getActiveBranchCount() {
        return branchRepository.countByIsActiveTrue();
    }
}