package  com.rvy.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

	@Pointcut("within(com.sapient.ofd.dao.*)"+
			" || within(com.sapient.ofd.service.*) || within(com.sapient.ofd.controller.*)")
	public void applicationPackagePointcut() {
	}

	@AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), e.getCause() != null? e.getCause() : "No Clear Error Message");
	}

	@Around("applicationPackagePointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
			}
			try {
				Object result = joinPoint.proceed();
				if (log.isDebugEnabled()) {
					log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
							joinPoint.getSignature().getName(), result);
				}
				return result;
			} catch (Exception e) {
				log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
						joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
				throw e;
			}
		}catch(Exception e) {
				throw e;
		}
	}
}
