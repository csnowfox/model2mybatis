package org.csnowfox.maven.plugin.pdm2mybatis.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Key
 * @Description pdm解析结果主键
 * @Author Csnowfox
 * @Date 2019/4/27 16:45
 **/
public class Key {

	String id;
	String name;
	String code;
	String[] columnId;
	Boolean pkFlag;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getColumnId() {
		return this.columnId;
	}

	public void setColumnId(String[] columnId) {
		this.columnId = columnId;
	}

	public Boolean getPkFlag() {
		return this.pkFlag;
	}

	public void setPkFlag(Boolean pkFlag) {
		this.pkFlag = pkFlag;
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

	public Column[] qryColumn(Column[] cols) {
		if ((this.columnId == null) || (this.columnId.length <= 0)) {
			return null;
		}
		if ((cols == null) || (cols.length <= 0)) {
			return null;
		}
		List lisc = new ArrayList();
		for (int i = 0; i < this.columnId.length; i++) {
			for (int j = 0; j < cols.length; j++) {
				if (cols[j].id.equals(this.columnId[i])) {
					lisc.add(cols[j]);
				}
			}
		}
		Column[] a = new Column[0];
		return (Column[]) lisc.toArray(a);
	}
}