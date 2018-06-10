package com.nervose.tktest.contoller;

import com.nervose.tktest.dto.TestRequest;
import com.nervose.tktest.dto.TestResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/valid")
public class Validcontroller {

    @RequestMapping(path = "/t1", method = RequestMethod.POST)
    public String valid(@RequestBody @Validated TestRequest testRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuffer message = new StringBuffer();
            bindingResult.getAllErrors().forEach((error)->{
                message.append(error.getDefaultMessage()).append(";");
            });
            return message.toString();
        }
        return testRequest.toString();
    }
    @RequestMapping(path = "/t2", method = RequestMethod.GET)
    public TestResponse jsonDateTest() {
        TestResponse testResponse=new TestResponse();
        testResponse.setCreateTime(new Date());
        return testResponse;
    }

}
