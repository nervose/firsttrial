package com.nervose.tktest;

import retrofit2.Retrofit;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Test {
    @FunctionalInterface
    interface Lambda<T>{
        public T add(T a, T b);
    }
    public static void main(String[] args) {
    }

    private static void test1() {
        System.out.println(useLambda((a, b) -> a+b,1,2));
        useLambda2(System.out::println,"123");
        Integer t= Arrays.stream(new Integer[]{5,2,3,4}).map(a->a+1).sorted().findAny().get();
        Arrays.stream(new Integer[]{5,2,3,4}).map(a->a+1).sorted().collect(Collectors.toList());
    }

    public static <T> T useLambda(Lambda<T> lambda, T a, T b){
        return lambda.add(a,b);
    }
    public static void useLambda2(Consumer<String> consumer,String s){
        consumer.accept(s);
    }


}
