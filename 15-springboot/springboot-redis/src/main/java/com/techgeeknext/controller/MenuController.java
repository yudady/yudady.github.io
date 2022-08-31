package com.techgeeknext.controller;

import com.google.gson.Gson;
import com.techgeeknext.entity.Menu;
import com.techgeeknext.repository.MenuRepo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuRepo menuRepo;


    @PostMapping
    public Menu save(@RequestBody Menu menuDetails) {

        String value = new Gson().toJson(menuDetails);
        System.out.println("value = " + value);
        menuRepo.store("" + menuDetails.getId(), value);

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
