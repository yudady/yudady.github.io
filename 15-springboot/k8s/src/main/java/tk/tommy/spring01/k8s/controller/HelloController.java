package tk.tommy.spring01.k8s.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "index ~~~~~~~~  hello world!!!";
    }


    @GetMapping("/hello")
    public String hello() {
        return "hello world!!!";
    }
}
