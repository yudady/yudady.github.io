package com.tommy.back;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class RestUrlController {
    @GetMapping("/ok")
    public String getAllEmployees() {
        return "ok";
    }

}
