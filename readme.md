# HAA WSEAR

## To create new web services

This project is to develop web services and assemble web package to ear project.
To create a new web service in the ear package, create a maven child project in the HAA_WSEAR project.
Add it as a dependency in the HMMS_netwiser_WSEAR and set its type to war.

```xml
<dependency>
      <groupId>com.hkhs.hmms</groupId>
      <artifactId>haa_enhancement</artifactId>
      <version>1.0-SNAPSHOT</version>
      <!--IMPORTANT-->
      <type>war</type>
</dependency>
```

[Refenece ear plugin](<http://people.apache.org/~epunzalan/maven-ear-plugin/usage.html>)
