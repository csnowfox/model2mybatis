package org.csnowfox.maven.plugin.model2mybatis.entity;

/**
 * @ClassName: Column
 * @Description Pdm parsing result field
 * @Author Csnowfox
 * @Date 2019/4/27 16:45
 **/
public class Column {

	String id;
	String defaultValue;
	String name;
	String type;
	String typefull;
	String code;
	String comment;
	Integer length;
	String sLength;
	String sPrecision;
	Boolean pkFlag;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type.toUpperCase();
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return this.code.toLowerCase();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getLength() {
		return this.length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Boolean getPkFlag() {
		return this.pkFlag;
	}

	public void setPkFlag(Boolean pkFlag) {
		this.pkFlag = pkFlag;
	}

	public String getComment() {
		return this.comment == null ? "" : this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSPrecision() {
		return this.sPrecision;
	}

	public void setSPrecision(String precision) {
		this.sPrecision = precision;
	}

	public String getSLength() {
		return this.sLength;
	}

	public void setSLength(String length) {
		this.sLength = length;
	}

	public String getTypefull() {
		return this.typefull;
	}

	public void setTypefull(String typefull) {
		this.typefull = typefull;
	}

	public String getMethod() {
		String code = this.code.toLowerCase();
		return code.substring(0, 1).toUpperCase() + code.substring(1);
	}
}