package com.nervose.tktest.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduleComponent {
    public final static long SECOND = 1 * 1000;
    String pat2 = "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒" ;
    SimpleDateFormat sdf=new SimpleDateFormat(pat2) ;


    @Scheduled(fixedRate = SECOND * 10)
    public void fixedDelayJob() throws InterruptedException {
    }
}
