package com.svu.resume.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/templates")
@Slf4j
public class TemplateService {
    public Map<String,Object> getTemplates(Authentication authentication){
        List<String> availableTemplates;
        Boolean isPremium = true;
        availableTemplates = List.of("01","02","03");
        Map<String,Object> restrictions=new HashMap<>();
        restrictions.put("availableTemplates",availableTemplates);
        restrictions.put("allTemplates",List.of("01","02","03")); 
        restrictions.put("subscription","premium"); 
        restrictions.put("isPremium",isPremium); 
        return restrictions;
    }
}
