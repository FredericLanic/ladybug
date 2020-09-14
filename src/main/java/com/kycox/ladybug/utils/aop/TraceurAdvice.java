package com.kycox.ladybug.utils.aop;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AfterReturningAdvice;

public class TraceurAdvice implements AfterReturningAdvice {
	private static final Log logger = LogFactory.getLog(TraceurAdvice.class);

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		logger.info(target + "." + method.getName() + "->" + returnValue);
	}
}
