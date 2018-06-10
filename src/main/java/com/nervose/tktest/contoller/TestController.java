package com.nervose.tktest.contoller;

import com.nervose.tktest.entity.Account;
import com.nervose.tktest.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AccountService accountService;

    @RequestMapping(path = "/gettest",method = RequestMethod.GET)
    public String getHello(){
        return "hello world";
    }

    @RequestMapping(path = "/posttest",method = RequestMethod.POST)
    public String postHello(@RequestBody Account account){
        return account.getName();
    }

    @RequestMapping(path = "/insert",method = RequestMethod.POST)
    public Account addAccount(@RequestBody Account account){
        return accountService.saveUser(account);
    }

    @RequestMapping(path = "/show",method = RequestMethod.GET)
    public List<Account> showAccounts(){
        return accountService.listUsers();
    }

    @RequestMapping(path = "/select",method = RequestMethod.GET)
    public Account showAccount(@RequestParam Integer id){
        return accountService.selectUser(id);
    }

    @RequestMapping(path = "/update",method = RequestMethod.POST)
    public Account showAccount(@RequestBody Account account){
        return accountService.updateUser(account);
    }

    @RequestMapping(path = "/test",method = RequestMethod.GET)
    public String test(){
        redisTemplate.opsForValue().set("testValue","123456");
        return (String) redisTemplate.opsForValue().get("testValue");
    }
}
