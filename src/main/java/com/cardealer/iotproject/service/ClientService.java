package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.Client;
import com.cardealer.iotproject.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ClientService {
    
    private static final Logger log = Logger.getLogger(ClientService.class.getName());
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Optional<Client> getClientByCedula(String cedula) {
        return clientRepository.findByCedula(cedula);
    }
    
    @Transactional(readOnly = true)
    public Optional<Client> getClientByRnc(String rnc) {
        return clientRepository.findByRnc(rnc);
    }
    
    @Transactional
    public Client createClient(Client client) {
        // Validate required fields based on tipo
        validateClient(client);
        
        // Check for duplicate cedula
        if (client.getCedula() != null && !client.getCedula().isEmpty()) {
            if (clientRepository.existsByCedula(client.getCedula())) {
                throw new RuntimeException("Ya existe un cliente con la cédula: " + client.getCedula());
            }
        }
        
        // Check for duplicate RNC
        if (client.getRnc() != null && !client.getRnc().isEmpty()) {
            if (clientRepository.existsByRnc(client.getRnc())) {
                throw new RuntimeException("Ya existe un cliente con el RNC: " + client.getRnc());
            }
        }
        
        client.setStatus(1); // Active by default
        
        Client saved = clientRepository.save(client);
        log.info("Cliente creado: " + saved.getFullName() + " (ID: " + saved.getId() + ")");
        return saved;
    }
    
    @Transactional
    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);
        
        // Update fields
        if (clientDetails.getCedula() != null) {
            // Check if new cedula is already used by another client
            if (!clientDetails.getCedula().equals(client.getCedula()) && 
                clientRepository.existsByCedula(clientDetails.getCedula())) {
                throw new RuntimeException("Ya existe otro cliente con la cédula: " + clientDetails.getCedula());
            }
            client.setCedula(clientDetails.getCedula());
        }
        
        if (clientDetails.getFirstname() != null) {
            client.setFirstname(clientDetails.getFirstname());
        }
        
        if (clientDetails.getLastname() != null) {
            client.setLastname(clientDetails.getLastname());
        }
        
        if (clientDetails.getRnc() != null) {
            // Check if new RNC is already used by another client
            if (!clientDetails.getRnc().equals(client.getRnc()) && 
                clientRepository.existsByRnc(clientDetails.getRnc())) {
                throw new RuntimeException("Ya existe otro cliente con el RNC: " + clientDetails.getRnc());
            }
            client.setRnc(clientDetails.getRnc());
        }
        
        if (clientDetails.getCell() != null) {
            client.setCell(clientDetails.getCell());
        }
        
        if (clientDetails.getBusinessPhone() != null) {
            client.setBusinessPhone(clientDetails.getBusinessPhone());
        }
        
        if (clientDetails.getAddress() != null) {
            client.setAddress(clientDetails.getAddress());
        }
        
        if (clientDetails.getCiudad() != null) {
            client.setCiudad(clientDetails.getCiudad());
        }
        
        if (clientDetails.getProvincia() != null) {
            client.setProvincia(clientDetails.getProvincia());
        }
        
        if (clientDetails.getEmpresa() != null) {
            client.setEmpresa(clientDetails.getEmpresa());
        }
        
        if (clientDetails.getContact() != null) {
            client.setContact(clientDetails.getContact());
        }
        
        if (clientDetails.getTipo() != null) {
            client.setTipo(clientDetails.getTipo());
        }
        
        if (clientDetails.getStatus() != null) {
            client.setStatus(clientDetails.getStatus());
        }
        
        // Re-validate after updates
        validateClient(client);
        
        Client saved = clientRepository.save(client);
        log.info("Cliente actualizado: " + saved.getFullName() + " (ID: " + saved.getId() + ")");
        return saved;
    }
    
    @Transactional
    public Client updateClientStatus(Long id, Integer status) {
        Client client = getClientById(id);
        client.setStatus(status);
        return clientRepository.save(client);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
        log.info("Cliente eliminado: " + client.getFullName() + " (ID: " + client.getId() + ")");
    }
    
    @Transactional(readOnly = true)
    public Page<Client> searchClients(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return clientRepository.findAll(pageable);
        }
        return clientRepository.searchClients(search.trim(), pageable);
    }
    
    private void validateClient(Client client) {
        Integer tipo = client.getTipo();
        
        if (tipo == null) {
            throw new RuntimeException("El tipo de cliente es requerido");
        }
        
        if (tipo == 1) { // Business
            if (client.getEmpresa() == null || client.getEmpresa().trim().isEmpty()) {
                throw new RuntimeException("El nombre de la empresa es requerido para clientes empresariales");
            }
            if (client.getRnc() == null || client.getRnc().trim().isEmpty()) {
                throw new RuntimeException("El RNC es requerido para clientes empresariales");
            }
            if (client.getContact() == null || client.getContact().trim().isEmpty()) {
                throw new RuntimeException("La persona de contacto es requerida para clientes empresariales");
            }
        } else if (tipo == 2) { // Individual
            if (client.getFirstname() == null || client.getFirstname().trim().isEmpty()) {
                throw new RuntimeException("El nombre es requerido para clientes individuales");
            }
            if (client.getLastname() == null || client.getLastname().trim().isEmpty()) {
                throw new RuntimeException("El apellido es requerido para clientes individuales");
            }
        } else {
            throw new RuntimeException("Tipo de cliente inválido. Debe ser 1 (Empresa) o 2 (Individuo)");
        }
    }
}