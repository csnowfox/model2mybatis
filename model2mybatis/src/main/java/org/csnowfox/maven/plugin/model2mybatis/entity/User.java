package org.csnowfox.maven.plugin.model2mybatis.entity;

/**
 * @ClassName: User
 * @Description Pdm analysis result user
 * @Author Csnowfox
 * @Date 2019/4/27 16:45
 **/
public class User {

	private String id;
	private String name;
	private String code;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}