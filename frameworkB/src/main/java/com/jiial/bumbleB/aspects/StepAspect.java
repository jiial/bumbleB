package com.jiial.bumbleB.aspects;

import com.jiial.bumbleB.annotations.Step;
import com.jiial.bumbleB.framework.Framework;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class StepAspect {

    @Pointcut("@annotation(step)")
    public void callStep(Step step) {
    }

    @Before("callStep(step)")
    public void setMethodNameToFramework(JoinPoint joinPoint, Step step) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(Step.class)) {
            String annotationValue = method.getAnnotation(Step.class).value();
            Object[] args = Framework.state.get().getArgs();
            if (args.length > 0) {
                for (Object arg : args) {
                    annotationValue = annotationValue.replaceFirst("\\{(.*?)}", arg.toString());
                }
            }
            Framework.state.get().setStepDefinition(annotationValue);
        }
    }
}
