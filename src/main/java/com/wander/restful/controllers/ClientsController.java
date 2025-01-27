package com.wander.restful.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wander.restful.models.Client;
import com.wander.restful.models.ClientDto;
import com.wander.restful.repositories.ClientRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/clients")
public class ClientsController {
    
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping({"", "/"})
    public String getClients(Model model) {
        var clients = clientRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("clients", clients);
        return "clients/index";
    }

    @GetMapping("/create")
    public String createClient(Model model) {
        ClientDto clientDto = new ClientDto();
        model.addAttribute("clientDto", clientDto);
        return "clients/create";
    }

    @PostMapping("/create")
    public String createClient(@Valid @ModelAttribute ClientDto clientDto, BindingResult result) {
        //email check
        if(clientRepository.findByEmail(clientDto.getEmail()) != null) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email is already taken"));
        }
        //other errors
        if(result.hasErrors()) {
            return "clients/create";
        }

        //saving client
        Client client = new Client();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());
        client.setCreatedAt(new Date());
        clientRepository.save(client);

        return "redirect:/clients";
    }

    @GetMapping("/edit")
    public String editClient(Model model, @RequestParam Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if(client == null) return "redirect:/clients";

        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(clientDto.getLastName());
        clientDto.setEmail(clientDto.getEmail());
        clientDto.setPhone(clientDto.getPhone());
        clientDto.setAddress(clientDto.getAddress());
        clientDto.setStatus(clientDto.getStatus());

        model.addAttribute("client", client);
        model.addAttribute("clientDto", clientDto);

        return "clients/edit";
    }

    @PostMapping("/edit")
    public String editClient(Model model, @RequestParam Long id, @Valid @ModelAttribute ClientDto clientDto, BindingResult result) {
        Client client = clientRepository.findById(id).orElse(null);
        if(client == null) return "redirect:/clients";
        
        model.addAttribute("client", client);
        
        if(result.hasErrors()) return "clients/edit";

        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());

        try {
            clientRepository.save(client);
        } catch (Exception e) {
            // email already used
            return "clients/edit";
        }

        return "redirect:/clients";
    }
        
}
