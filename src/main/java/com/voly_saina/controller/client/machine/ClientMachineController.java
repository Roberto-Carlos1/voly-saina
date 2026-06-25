package com.voly_saina.controller.client.machine;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.TypeMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/client/machines")
public class ClientMachineController {

    @Autowired
    private MachineService machineService;
    
    @Autowired
    private TypeMachineService typeMachineService;

    // liste toutes les machines
    @GetMapping("/catalogue")
    public String catalogue(Model model) {
        List<TypeMachine> types = typeMachineService.findAll();
        model.addAttribute("types", types);
        return "client/machines/catalogue";
    }

    // liste les machines par type
    @GetMapping("/type/{typeId}")
    public String machinesByType(@PathVariable Long typeId, Model model) {
        TypeMachine type = typeMachineService.findById(typeId);
        
        List<Machine> machines = machineService.findByTypeMachine(typeId);
        
        model.addAttribute("type", type);
        model.addAttribute("machines", machines);
        return "client/machines/list-by-type";
    }

    // lister les machines disponibles
    @GetMapping("/disponibles")
    public String machinesDisponibles(Model model) {
        List<Machine> machines = machineService.findAvailableMachines();
        model.addAttribute("machines", machines);
        return "client/machines/disponibles";
    }

    // detail d'une machine
    @GetMapping("/{id}")
    public String detailMachine(@PathVariable Long id, Model model) {
        Machine machine = machineService.findById(id);
        if (machine == null) return "error/404";
        model.addAttribute("machine", machine);
        return "client/machines/detail";
    }
}