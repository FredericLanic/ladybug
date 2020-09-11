package com.kycox.ladybug.utils.aop;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;

public class TraceurAdvice implements AfterReturningAdvice {
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		System.out.println(target + "." + method.getName() + "->" + returnValue);
	}
}
