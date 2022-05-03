#Documentation

## Contents
[1. Getting started](Documentation.md#1-getting-started)  
[2. Writing tests](Documentation.md#2-writing-tests)  
[3. Running tests](Documentation.md#3-running-tests)  


## 1. Getting started
To use bumbleB, you need to add the following Maven-dependencies to your project:
```
<dependency>
    <groupId>com.jiial.bumbleB</groupId>
    <artifactId>frameworkB</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite-engine</artifactId>
    <version>1.8.1</version>
</dependency>
```


In addition, you will need the following plugin (the configuration can be changed, but this is the tested configuration):
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.19.1</version>
    <dependencies>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-surefire-provider</artifactId>
            <version>1.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.1.0</version>
        </dependency>
    </dependencies>
    <configuration>
        <argLine>
            -javaagent:"${settings.localRepository}"/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar
        </argLine>
        <useSystemClassLoader>true</useSystemClassLoader>
        <forkMode>always</forkMode>
        <parallel>all</parallel>
        <useUnlimitedThreads>true</useUnlimitedThreads>
    </configuration>
</plugin>
```
To enable AspectJ, you will also need to create a src/main/resources/META-INF/aop.xml-file with the following content:
```
<aspectj>
    <weaver options="-verbose -showWeaveInfo">
        <include within="com.jiial.bumbleB.aspects.*"/>
        <include within="com.jiial.bumbleB.{PATH_TO_MY_STEPS}.*"/>
        <include within="com.jiial.bumbleB.{PATH_TO_MY_PROJECT}.*"/>
    </weaver>
    <aspects>
        <aspect name="com.jiial.bumbleB.aspects.StepAspect"/>
    </aspects>
</aspectj>
```
AspectJ is used to access information about parameters passed to method within given/when/then-steps.

If you're starting a new project, you can just use the [examplesB](examplesB)-project as your starting point to skip all of these steps.

Finally, you have to set the *settings.localRepository* environment variable to your local Maven repository. This is used in the Maven surefire plugin configuration for AspectJ to find the aspect weaver.



## 2. Writing tests
// TODO
## 3. Running tests
// TODO