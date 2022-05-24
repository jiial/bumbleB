package com.jiial.bumbleB.aspects;

import com.jiial.bumbleB.annotations.Example;
import com.jiial.bumbleB.framework.Framework;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ExampleAspect {

    @Pointcut("@annotation(example)")
    public void callExample(Example example) {
    }

    @Before("callExample(example)")
    public void setClassNameToFramework(JoinPoint joinPoint, Example example) {
        Framework.state.get().setClassName(joinPoint.getThis().getClass().getSimpleName());
        Framework.state.get().setPackageName(joinPoint.getThis().getClass().getPackageName());
    }
}
