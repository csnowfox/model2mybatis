package org.csnowfox.maven.plugin.model2mybatis.entity;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import com.google.common.io.Files;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.csnowfox.maven.plugin.model2mybatis.Model2MybatisJavaCode;
import org.csnowfox.maven.plugin.model2mybatis.utils.MavenLogger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: Key
 * @Description pdm解析表pojo
 * @Author Csnowfox
 * @Date 2019/4/27 16:45
 **/
public class Table {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Table.class);

	String id;
	String tableName;
	String tableCode;
	String comment;
	User user;
	Column[] cols;
	Key[] keys;
	String projectName;

	public Table() {
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableCode() {
		return this.tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public Column[] getCols() {
		return this.cols;
	}

	public void setCols(Column[] cols) {
		this.cols = cols;
	}

	public Key[] getKeys() {
		return this.keys;
	}

	public void setKeys(Key[] keys) {
		this.keys = keys;
	}

	public String getComment() {
		return this.comment == null ? "" : this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDbUser() {
		if ((this.user == null) || (this.user.getCode() == null)) {
			return "";
		}
		return this.user.getCode() + ".";
	}

	public String getIbatisUser() {
		if ((this.user == null) || (this.user.getCode() == null)) {
			return "";
		}
		return this.user.getCode().toLowerCase() + "_";
	}

	public String getTableCommon() {
		String common = this.getComment();
		String name = this.getTableName();
		return (common != null && !common.trim().equals("")) ? common : name;
	}


	public String getTableClassName() {
		StringBuffer tableClassName = new StringBuffer();
		String code = this.tableCode.toLowerCase();
		StringTokenizer tokenizer = new StringTokenizer(code, "_");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			token = token.substring(0, 1).toUpperCase() + token.substring(1);
			tableClassName.append(token);
		}
		return tableClassName.toString();
	}

	public Key getPrimaryKey() {
		if ((this.keys == null) || (this.keys.length <= 0)) {
			return null;
		}
		for (int i = 0; i < this.keys.length; i++) {
			if (this.keys[i].getPkFlag().booleanValue()) {
				return this.keys[i];
			}
		}
		return null;
	}

	public void writeout(Table tab, String packPath, String savePath, String dbUser, String interfaceName) throws Exception {
		MavenLogger.info("name[" + this.tableName
				+ "],code[" + this.tableCode + "], 描述[" + this.comment
				+ "]=======");
		dbUser = dbUser.toLowerCase();
		String tablepath = savePath + File.separator + dbUser + File.separator;
		String name = "";

		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(Model2MybatisJavaCode.class, "/org/csnowfox/maven/plugin/model2mybatis/template");

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("project_package", packPath + "." + dbUser);
		root.put("entity_class", getTableClassName());
		root.put("entity_interface", interfaceName);
		root.put("entity_comment", tab.comment);
		root.put("entity_table_name", this.tableCode);

		List<ClassColumns> cols = new LinkedList<>();
		List<ClassColumns> keys = new LinkedList<>();
		List<ClassColumns> notKeys = new LinkedList<>();

		if (this.cols != null) {
			for (int i = 0; i < this.cols.length; i++) {

				ClassColumns item = new ClassColumns();

				String percision = this.cols[i].getSPrecision();
				String type = this.cols[i].getType();
				String typeRight = getTypeRight(type, this.cols[i].getLength(), percision);
				item.setJdbcClazz(this.cols[i].getType());
				item.setClazz(typeRight);
				item.setName(underlineToCamel(this.cols[i].getCode()));
				item.setJdbcName(this.cols[i].getCode());
				item.setComment(((this.cols[i].getComment() != null && !this.cols[i]
						.getComment().trim().equals("")) ? this.cols[i]
						.getComment() : this.cols[i].getName()));
				item.setUpcaseCamelName(item.getName().substring(0, 1).toUpperCase() + item.getName().substring(1));

				cols.add(item);
				if (!isPrimaryKey(this.cols[i])) {
					notKeys.add(item);
				} else {
					keys.add(item);
				}

			}
		}
		root.put("class_columns", cols);
		root.put("class_primarkKeys", keys);
		root.put("class_notKeys", notKeys);

		{
			Template entityTemplate = cfg.getTemplate("entity.ftl");
			File outputFile = new File(tablepath + File.separator + getTableClassName() + ".java");
			Files.createParentDirs(outputFile);
			entityTemplate.process(root, new FileWriter(outputFile));
		}

		{
			Template exampleTemplate = cfg.getTemplate("example.ftl");
			File outputFile = new File(tablepath + File.separator + getTableClassName() + "Example.java");
			Files.createParentDirs(outputFile);
			exampleTemplate.process(root, new FileWriter(outputFile));
		}

		{
			Template mapperTemplate = cfg.getTemplate("mapper.ftl");
			File outputFile = new File(tablepath + File.separator + getTableClassName() + "Mapper.java");
			Files.createParentDirs(outputFile);
			mapperTemplate.process(root, new FileWriter(outputFile));
		}

		{
			Template providerTemplate = cfg.getTemplate("provider.ftl");
			File outputFile = new File(tablepath + File.separator + getTableClassName() + "SqlProvider.java");
			Files.createParentDirs(outputFile);
			providerTemplate.process(root, new FileWriter(outputFile));
		}
	}

	public boolean isExistTimestamp() {
		if (this.cols != null) {
			for (int i = 0; i < this.cols.length; i++) {
				if ("TIMESTAMP".equals(this.cols[i].getType())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isExistDATE() {
		if (this.cols != null) {
			for (int i = 0; i < this.cols.length; i++) {
				if ("DATE".equals(this.cols[i].getType())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPrimaryKey(Column col) {
		Key pk = getPrimaryKey();
		if (pk == null) {
			return false;
		}
		String[] columnIds = pk.columnId;
		if ((columnIds == null) || (columnIds.length <= 0)) {
			return false;
		}
		for (int i = 0; i < columnIds.length; i++) {
			if (columnIds[i].equalsIgnoreCase(col.id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 带下划线命名变更至驼峰命名
	 * @param param
	 * @return
	 */
	private static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == '_') {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private String fitType(String type, int length, String percision) {
		String typeRight = "VARCHAR";
		if (type.equals("NUMBER")) {
			if (percision != null && Integer.valueOf(percision) > 0) {
				if (length >= 12) {
					typeRight = "DECIMAL";
				} else {
					typeRight = "Double";
				}
			} else if (length >= 9) {
				typeRight = "BIGINT";
			} else {
				typeRight = "Integer";
			}
		} else if (type.equals("DATE")) {
			typeRight = "Date";
		} else if (type.equals("TIMESTAMP")) {
			typeRight = "TIMESTAMP";
		} else if (type.equals("INTEGER")) {
			typeRight = "Integer";
		} else {
			typeRight = "VARCHAR";
		}

		return typeRight.toUpperCase();
	}

	private String getTypeRight(String type, Integer length, String percision) {
		String typeRight = "";
		String typeIgnoreCase = type.toUpperCase();
		if (typeIgnoreCase.equals("NUMBER")) {
			if(length == null){
				typeRight = "BigDecimal";
			}else{
			if (percision != null && Integer.valueOf(percision) > 0) {
				if (length >= 12) {
					typeRight = "BigDecimal";
				} else {
					typeRight = "Double";
				}
			} else if (length >= 9) {
				typeRight = "Long";
			} else {
				typeRight = "Integer";
			}
		}} else if (typeIgnoreCase.equals("INT")) {
			typeRight = "Integer";
		} else if (typeIgnoreCase.startsWith("DECIMAL")) {
			typeRight = "BigDecimal";
		} else if (typeIgnoreCase.equals("DATE")) {
			typeRight = "Date";
		} else if (typeIgnoreCase.equals("TIMESTAMP")) {
			typeRight = "Date";
		} else if (typeIgnoreCase.equals("INTEGER")) {
			typeRight = "Integer";
		} else if (typeIgnoreCase.startsWith("VARCHAR")) {
			typeRight = "String";
		}else {
			typeRight = "String";
		}

		return typeRight;
	}

}