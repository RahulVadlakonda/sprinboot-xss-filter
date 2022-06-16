package com.spring.boot.service;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personService")
public class PersonController {

    @GetMapping(value = "/param")
    public String getTest1(@RequestParam String name, @RequestParam String boom) {
        return name + " " + boom;

    }

    @RequestMapping(value = "/body")
    public String getTest2(@RequestBody Person person) {
        return person.getfName() + " " + person.getlName();

    }

}