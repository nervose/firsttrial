package com.nervose.tktest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TktestApplicationTests {
	@Autowired
	private RedisTemplate redisTemplate;
	@Test
	public void contextLoads() {
//		for(int i=0;i<100;i++){
//			Map<String,String>map=new HashMap<>();
//			String[] keyPairs=createKeyPair();
//			map.put("pk",keyPairs[0]);
//			map.put("sk",keyPairs[1]);
//			redisTemplate.opsForHash().putAll(i,map);
//		}
		for(int i=0;i<10;i++){
			System.out.println(redisTemplate.opsForHash().get(i,"sk"));
		}
	}

}
