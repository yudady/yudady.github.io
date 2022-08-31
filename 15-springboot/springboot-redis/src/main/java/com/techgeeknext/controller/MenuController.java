package com.techgeeknext.controller;

import com.techgeeknext.entity.Menu;
import com.techgeeknext.repository.MenuRepo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
	
    @Autowired
    private MenuRepo menuRepo;

    @PostMapping
    public Menu save(@RequestBody Menu menuDetails) {

        String log = ToStringBuilder.reflectionToString(menuDetails, ToStringStyle.JSON_STYLE);
        System.out.println("log = " + log);
        return menuRepo.save(menuDetails);
    }

    @GetMapping
    public List<Menu> getMenus() {
        return menuRepo.findAll();
    }

    @GetMapping("/{id}")
    public Menu findMenuItemById(@PathVariable int id) {
        return menuRepo.findItemById(id);
    }


    @DeleteMapping("/{id}")
    public String deleteMenuById(@PathVariable int id)   {
    	return menuRepo.deleteMenu(id);
	}

}
