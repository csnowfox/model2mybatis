# pdm2mybatis
Maven plugin, using pdm file as source, generating mybatis code

Due to the need of version management, database modeling uses pdm mode, and it is necessary to generate code directly from pdm.
Keep pdm consistent with the code by code generation during maven compilation.

### pom.xml config example
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>csnowfox</groupId>
		<artifactId>isp</artifactId>
		<version>1.0.6-RELEASE</version>
	</parent>
	<artifactId>isp-api</artifactId>
	<url>http://maven.apache.org</url>
	<packaging>jar</packaging>

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
					<source>1.7</source>
					<target>1.7</target>
					<encoding>UTF-8</encoding>
					<skipTests>true</skipTests>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.csnowfox.maven.plugin</groupId>
				<artifactId>pdm2mybatis</artifactId>
				<version>0.0.9</version>

				<executions>

					<execution>
						<id>fund</id>
						<goals>
							<goal>echo</goal>
						</goals>
						<phase>generate-sources</phase>
						<configuration>
							<pathdao>src/main/java/com/csnowfox/isp/api/</pathdao>
							<pathsvn>src/main/resource/mybatis-config/mapper-fund/</pathsvn>
							<pathpack>org.csnowfox.isp.api</pathpack>
							<projectname>isp.api</projectname>
							<pathpdm>src/main/resource/mybatis-config/pdm</pathpdm>
							<tables>fund:MONITOR_DATA_CONSTRAINT;YM_TRANSFER_DATA;</tables>
							<pathsql>../SQL/</pathsql>
							<namesql>fae_fund.sql</namesql>
							<interfaceName>org.csnowfox.isp.api.service.SqlMapper</interfaceName>
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
										<artifactId>pdm2mybatis</artifactId>
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
</project>
```
