# model2mybatis
Maven plugin, using pdm file as source, generating mybatis code

Due to the need of version management, database modeling uses pdm mode, and it is necessary to generate code directly from pdm.
Keep pdm consistent with the code by code generation during maven compilation.

* [spring-boot example](https://github.com/csnowfox/model2mybatis-example) 

### pom.xml config example
```
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/resource</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.csnowfox.maven.plugin</groupId>
            <artifactId>model2mybatis</artifactId>
            <version>0.0.9</version>

            <executions>

                <execution>
                    <id>fund</id>
                    <goals>
                        <goal>echo</goal>
                    </goals>
                    <phase>generate-sources</phase>
                    <configuration>
                        <!-- Where is the generated mybatis file placed? -->
                        <pathdao>src/main/java/org/csnowfox/maven/plugin/example/dao</pathdao>
                        <!-- The package name of the generated java class of mybatis -->
                        <pathpack>org.csnowfox.maven.plugin.example.dao</pathpack>
                        <!-- Project name -->
                        <projectname>model2mybatis-example.dao</projectname>
                        <!-- Path to the pdm file -->
                        <pathpdm>src/main/resources/pdm</pathpdm>
                        <!-- The user of the table and then table you want to generate-->
                        <tables>fund:FUND_CALENDAR;</tables>
                        <!-- Where is the generated sql file? -->
                        <pathsql>../SQL/</pathsql>
                        <!-- The name of sql file -->
                        <namesql>fund.sql</namesql>
                        <!-- The Interface you want the mapper to implement -->
                        <interfaceName>org.csnowfox.maven.plugin.example.SqlMapper</interfaceName>
                    </configuration>
                </execution>

            </executions>
        </plugin>
    </plugins>

    <pluginManagement>
        <plugins>
            <!--This plugin's configuration is used to store Eclipse m2e settings only. It has no influence on the Maven build itself.-->
            <plugin>
                <groupId>org.eclipse.m2e</groupId>
                <artifactId>lifecycle-mapping</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <lifecycleMappingMetadata>
                        <pluginExecutions>
                            <pluginExecution>
                                <pluginExecutionFilter>
                                    <groupId>org.csnowfox.maven.plugin</groupId>
                                    <artifactId>model2mybatis</artifactId>
                                    <versionRange>
                                        [0.0.9,)
                                    </versionRange>
                                    <goals>
                                        <goal>echo</goal>
                                    </goals>
                                </pluginExecutionFilter>
                                <action>
                                    <ignore></ignore>
                                </action>
                            </pluginExecution>
                        </pluginExecutions>
                    </lifecycleMappingMetadata>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```
