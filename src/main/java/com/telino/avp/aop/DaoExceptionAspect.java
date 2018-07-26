package com.telino.avp.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.telino.avp.exception.AvpDaoException;

@Aspect
@Component
public class DaoExceptionAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(DaoExceptionAspect.class);

	@Pointcut("execution(* com.telino.avp.dao..*.*(..))")
	public void daoOperation() {
	}

	@Around("daoOperation()")
	public Object loggingDaoException(ProceedingJoinPoint pjp) throws AvpDaoException {

		try {
			return pjp.proceed();
		} catch (Throwable ex) {
			String arguments = Arrays.toString(pjp.getArgs());
			String msg =  pjp.getSignature().toString() + " with arguments ("
					+ arguments + ")\nThe exception is: " + ex.getMessage();
			LOGGER.error(msg);
			throw new AvpDaoException(msg, ex);
		}

	}

}
