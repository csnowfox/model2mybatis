package org.csnowfox.maven.plugin.pdm2mybatis.entity;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.tools.SimpleJavaFileObject;

import org.codehaus.plexus.util.StringUtils;

import org.csnowfox.maven.plugin.pdm2mybatis.CreateFile;
import org.csnowfox.maven.plugin.pdm2mybatis.LogUtils;

public class Table {
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

/*	public String getTableClassName() {
		System.out.println("code=" + code);
		int replaceIndex = -1;
		for (int i = 0; i < 20; i++) {
			replaceIndex = code.indexOf("_");
			if (replaceIndex < 0)
				break;
			code = code.replace("_", "");
			code = code.substring(0, replaceIndex)
					+ code.substring(replaceIndex, replaceIndex + 1)
							.toUpperCase() + code.substring(replaceIndex + 1);
		}
		return code.substring(0, 1).toUpperCase() + code.substring(1) + "Bean";
	}*/
	
	/**
	 * 添加新方法--张美宏
	 * */
	public String getTableClassName() {
	StringBuffer tableClassName = new StringBuffer();
	String code = this.tableCode.toLowerCase();
	StringTokenizer tokenizer = new StringTokenizer(code, "_");
	while(tokenizer.hasMoreTokens()){
		String token = tokenizer.nextToken().trim();
		token = token.substring(0, 1).toUpperCase() + token.substring(1);
		tableClassName.append(token);
	}
	return tableClassName.toString() + "Bean";
}

	public String getKeyClassName() {
		String tableclass = getTableClassName();
		return tableclass.replace("Bean", "Key");
	}

	public String getExampleClassName() {
		String tableclass = getTableClassName();
		return tableclass.replace("Bean", "Example");
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

	public void writeout(Table tab, String packPath, String savePath, String dbUser,
			boolean iscreatePer, String interfaceName)
			throws Exception {
		
		LogUtils.logger.info("==id[" + this.id + "],name[" + this.tableName
				+ "],code[" + this.tableCode + "], 描述[" + this.comment
				+ "]=======");
		dbUser = dbUser.toLowerCase();
		String tablepath = savePath + dbUser + File.separator  + "bean" + File.separator;
		String keypath = savePath + dbUser + File.separator  + "bean" + File.separator;
		String examplepath = savePath + dbUser + File.separator  + "bean" + File.separator;
		String persistencepath = savePath + dbUser + File.separator  + "dao" + File.separator;
		String name = "";

		name = getTableClassName() + ".java";
		CreateFile.writeFile(entityTableMode(packPath + "." + dbUser), tablepath, name);

		name = getKeyClassName() + ".java";
		CreateFile.writeFile(entityKeyMode(packPath+ "." + dbUser), keypath, name);

		name = getTableClassName() + "Example.java";
		CreateFile.writeFile(entityExampleMode(packPath+ "." + dbUser), examplepath, name);

		if (iscreatePer) {
			name = getTableClassName() + "Mapper" + ".java";
			CreateFile.writeFile(persistenceMode(tab, dbUser,packPath+ "." + dbUser, interfaceName), persistencepath,
					name);
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

	private List<String> entityTableMode(String packPath) throws Exception {
		String LINECHAR = "\r\n";
		List msg = new ArrayList();
		msg.add("package " + packPath + ".bean;" + LINECHAR + LINECHAR);
		if (isExistTimestamp()) {
			msg.add("import java.sql.Timestamp;" + LINECHAR);
		}
		
		msg.add("import java.util.Date;" + LINECHAR);
		msg.add("import java.math.BigDecimal;" + LINECHAR);
		msg.add("import java.io.Serializable;" + LINECHAR);
		
		msg.add(LINECHAR);

		msg.add("/**" + LINECHAR);
		msg.add(" * " + LINECHAR);
		msg.add(" * <h1>Title : " + getTableCommon() + " Bean</h1> " + LINECHAR);
		msg.add(" *" + LINECHAR);
		msg.add(" * @author		.csnowfox.org" + LINECHAR);
		msg.add(" * @version		1.0" + LINECHAR);
		msg.add(" */" + LINECHAR);
		msg.add("@SuppressWarnings(\"serial\")" + LINECHAR);
		msg.add("public class " + getTableClassName() + " extends "
				+ getKeyClassName() + " implements Serializable {" + LINECHAR);

		if (this.cols != null) {
			for (int i = 0; i < this.cols.length; i++) {
				String type = this.cols[i].getType();
				String percision = this.cols[i].getSPrecision();
				String typeRight = getTypeRight(type, this.cols[i].getLength(),
						percision);

				if (!isPrimaryKey(this.cols[i])) {
					msg.add("    private "
							+ typeRight
							+ " "
							+ underlineToCamel(this.cols[i].getCode())
							+ "; //"
							+ ((this.cols[i].getComment() != null && !this.cols[i]
									.getComment().trim().equals("")) ? this.cols[i]
									.getComment() : this.cols[i].getName())
							+ LINECHAR);
				}
			}
			msg.add(LINECHAR + LINECHAR);
			msg.add("    public void init() {" + LINECHAR);
			for (int i = 0; i < this.cols.length; i++) {
				if (!isPrimaryKey(this.cols[i])) {
					String type = this.cols[i].getType();
					String percision = this.cols[i].getSPrecision();
					String typeRight = getTypeRight(type,
							this.cols[i].getLength(), percision);

					String datatype = typeRight;
					if ("Double".equals(datatype)) {
						msg.add("        if("
								+ underlineToCamel(this.cols[i].getCode())
								+ "==null) "
								+ underlineToCamel(this.cols[i].getCode())
								+ "=0d;" + LINECHAR);
					} else if ("Integer".equals(datatype)) {
						msg.add("        if("
								+ underlineToCamel(this.cols[i].getCode())
								+ "==null) "
								+ underlineToCamel(this.cols[i].getCode())
								+ "=0;" + LINECHAR);
					} else if ("Long".equals(datatype)) {
						msg.add("        if("
								+ underlineToCamel(this.cols[i].getCode())
								+ "==null) "
								+ underlineToCamel(this.cols[i].getCode())
								+ "=0l;" + LINECHAR);
					}
				}
			}
			msg.add("    }" + LINECHAR);

			for (int i = 0; i < this.cols.length; i++) {

				String type = this.cols[i].getType();
				String percision = this.cols[i].getSPrecision();
				String typeRight = getTypeRight(type, this.cols[i].getLength(),
						percision);

				if (!isPrimaryKey(this.cols[i])) {
					String code = underlineToCamel(this.cols[i].getCode()
							.toLowerCase());
					code = code.substring(0, 1).toUpperCase()
							+ code.substring(1);
					msg.add("    /**get "
							+ ((this.cols[i].getComment() != null && !this.cols[i]
									.getComment().trim().equals("")) ? this.cols[i]
									.getComment() : this.cols[i].getName())
							+ "*/" + LINECHAR);
					msg.add("    public " + typeRight + " get" + code + "() {"
							+ LINECHAR);
					msg.add("        return "
							+ underlineToCamel(this.cols[i].getCode()) + ";"
							+ LINECHAR);
					msg.add("    }" + LINECHAR);
					msg.add("    /**set "
							+ ((this.cols[i].getComment() != null && !this.cols[i]
									.getComment().trim().equals("")) ? this.cols[i]
									.getComment() : this.cols[i].getName())
							+ "*/" + LINECHAR);
					msg.add("    public void set" + code + "(" + typeRight
							+ " " + underlineToCamel(this.cols[i].getCode())
							+ ") {" + LINECHAR);
					msg.add("        this."
							+ underlineToCamel(this.cols[i].getCode()) + " = "
							+ underlineToCamel(this.cols[i].getCode()) + ";"
							+ LINECHAR);
					msg.add("    }" + LINECHAR);
				}
			}
			msg.add(LINECHAR + LINECHAR);
			msg.add("    /** set 赋值操作*/" + LINECHAR);
			msg.add("    public void setEntity(" + getTableClassName()
					+ " orgEntity) {" + LINECHAR);
			msg.add("        if (orgEntity == null) return ; " + LINECHAR);
			for (int i = 0; i < this.cols.length; i++) {
				msg.add("        if(orgEntity."
						+ underlineToCamel(this.cols[i].getCode()) + "!=null) "
						+ underlineToCamel(this.cols[i].getCode())
						+ " = orgEntity."
						+ underlineToCamel(this.cols[i].getCode()) + ";"
						+ LINECHAR);
			}

			msg.add("    }" + LINECHAR);
		}

		msg.add("}" + LINECHAR);
		return msg;
	}

	public List<String> entityKeyMode(String packPath) throws Exception {
		String LINECHAR = "\r\n";
		List msg = new ArrayList();
		msg.add("package " + packPath + ".bean;" + LINECHAR + LINECHAR);

		msg.add(LINECHAR);
		msg.add("import java.math.BigDecimal;" + LINECHAR);
		msg.add("import java.io.Serializable;" + LINECHAR);
		msg.add(LINECHAR);

		msg.add("/**" + LINECHAR);
		msg.add(" * " + LINECHAR);
		msg.add(" * <h1>Title : " + getTableCommon() + " Key</h1> " + LINECHAR);
		msg.add(" *" + LINECHAR);
		msg.add(" * @author		.csnowfox.org" + LINECHAR);
		msg.add(" * @version		1.0" + LINECHAR);
		msg.add(" */" + LINECHAR);
		msg.add("@SuppressWarnings(\"serial\")" + LINECHAR);
		msg.add("public class " + getKeyClassName() + " implements Serializable {" + LINECHAR);

		msg.add(LINECHAR);
		Key key = getPrimaryKey();
		if ((key != null) && (key.columnId != null)
				&& (key.columnId.length > 0)) {
			Column[] carr = key.qryColumn(this.cols);
			if (carr != null) {
				for (int i = 0; i < carr.length; i++) {

					String type = carr[i].getType();
					String percision = carr[i].getSPrecision();
					String typeRight = getTypeRight(type, carr[i].getLength(),
							percision);

					msg.add("    protected "
							+ typeRight
							+ " "
							+ underlineToCamel(carr[i].getCode())
							+ "; //"
							+ ((carr[i].getComment() != null && !carr[i]
									.getComment().trim().equals("")) ? carr[i]
									.getComment() : carr[i].getName())
							+ LINECHAR);
				}
				msg.add(LINECHAR + LINECHAR);

				String param = "";
				for (int i = 0; i < carr.length; i++) {

					String type = carr[i].getType();
					String percision = carr[i].getSPrecision();
					String typeRight = getTypeRight(type, carr[i].getLength(),
							percision);

					param = param + typeRight + " "
							+ underlineToCamel(carr[i].getCode());
					if (i < carr.length - 1) {
						param = param + ", ";
					}
				}
				msg.add("    public void setKey(" + param + ") {" + LINECHAR);
				for (int i = 0; i < carr.length; i++) {
					msg.add("        this."
							+ underlineToCamel(carr[i].getCode()) + "="
							+ underlineToCamel(carr[i].getCode()) + ";"
							+ LINECHAR);
				}
				msg.add("    }" + LINECHAR);

				for (int i = 0; i < carr.length; i++) {

					String type = carr[i].getType();
					String percision = carr[i].getSPrecision();
					String typeRight = getTypeRight(type, carr[i].getLength(),
							percision);

					String code = carr[i].getMethod();
					msg.add("    /**get "
							+ ((carr[i].getComment() != null && !carr[i]
									.getComment().trim().equals("")) ? carr[i]
									.getComment() : carr[i].getName()) + "*/"
							+ LINECHAR);
					msg.add("    public " + typeRight + " get"
							+ underlineToCamel(code) + "() {" + LINECHAR);
					msg.add("        return "
							+ underlineToCamel(carr[i].getCode()) + ";"
							+ LINECHAR);
					msg.add("    }" + LINECHAR);
					msg.add("    /**set "
							+ ((carr[i].getComment() != null && !carr[i]
									.getComment().trim().equals("")) ? carr[i]
									.getComment() : carr[i].getName()) + "*/"
							+ LINECHAR);
					msg.add("    public void set" + underlineToCamel(code)
							+ "(" + typeRight + " "
							+ underlineToCamel(carr[i].getCode()) + ") {"
							+ LINECHAR);
					msg.add("        this."
							+ underlineToCamel(carr[i].getCode()) + " = "
							+ underlineToCamel(carr[i].getCode()) + ";"
							+ LINECHAR);
					msg.add("    }" + LINECHAR);
				}
			}
		}
		msg.add("}" + LINECHAR);
		return msg;
	}

	public List<String> persistenceMode(Table table, String dbuser, String packPath, String interfaceName) throws Exception {
		String LINECHAR = "\r\n";

		if (dbuser!=null && !"".equals(dbuser.trim())) {
			dbuser = dbuser + ".";
		}
		String resultMapField = "";
		String insfield = "";
		String insfieldas = "";
		String insvalue = "";
		String getfield = "";
		String getkey = "";
		String getkeyWithRecord="";
		String getwhere = "";
		String updset1 = "";
		String updset2 = "";
		String insertSelectiveField = "";
		String insertSelectiveValue = "";
		String insertSelectiveProvider = "";
		String updateByPrimaryKeySelectiveProvider = "";
		String updateField = "";
		String updateValue = "";
		String updateValueSelect = "";
		String updateValueSelectSelective = "";
		String updateProvider = "";
		String username = table.getIbatisUser();

		List msg = new ArrayList();
		if ((table.getCols() == null) || (table.getCols().length <= 0)) {
			return msg;
		}

		Column[] cols = table.getCols();
		Column[] keycs = (Column[]) null;
		Key key = table.getPrimaryKey();
		if (key != null) {
			keycs = table.getPrimaryKey().qryColumn(cols);
		}

		for (int i = 0; i < cols.length; i++) {

			resultMapField = resultMapField + "      <result column=\""
					+ cols[i].getCode().toUpperCase() + "\" property=\""
					+ underlineToCamel(cols[i].getCode().toLowerCase())
					+ "\" />";
			insfield = insfield + cols[i].getCode().toUpperCase();
			insfieldas = insfieldas + cols[i].getCode().toUpperCase() + " " + underlineToCamel(cols[i].getCode());
			String p = cols[i].getSPrecision();
			Integer l = cols[i].getLength();
			insvalue = insvalue + "#{" + underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p)
					+ "}";
			getfield = getfield + LINECHAR + "                  "
					+ cols[i].getCode() + " \""
					+ cols[i].getCode().toLowerCase() + "\"";
			if (("TIMESTAMP".equalsIgnoreCase(cols[i].getType()))
					|| ("DATE".equalsIgnoreCase(cols[i].getType()))) {
				updset1 = updset1 + cols[i].getCode() + "=sysdate ";
				if (i < cols.length - 1) {
					updset1 = updset1 + ", ";
				}
			}
			boolean flag = false;
			if (keycs != null) {
				for (int j = 0; j < keycs.length; j++) {
					if (keycs[j].getCode().equalsIgnoreCase(cols[i].getCode())) {
						flag = true;
						break;
					}
				}
			}
			if (!"TIMESTAMP".equalsIgnoreCase(cols[i].getType())) {
				getwhere = getwhere + LINECHAR
						+ "        <isNotEmpty prepend=\" and \" property=\""
						+ cols[i].getCode() + "\"><![CDATA["
						+ cols[i].getCode() + "=#" + cols[i].getCode()
						+ "#]]></isNotEmpty>";
				if (!flag) {
					updset2 = updset2 + LINECHAR
							+ "        <isNotEmpty prepend=\" , \" property=\""
							+ cols[i].getCode() + "\"><![CDATA["
							+ cols[i].getCode() + "=#" + cols[i].getCode()
							+ "#]]></isNotEmpty>";
				}
			}

			String code = underlineToCamel(this.cols[i].getCode()
					.toLowerCase());
			// 处理首字母大写
			code = code.substring(0, 1).toUpperCase()
					+ code.substring(1);
			insertSelectiveProvider = insertSelectiveProvider +
					"                if (bean.get" + code + "() != null) {" +
					"VALUES(\"" + cols[i].getCode().toUpperCase() + "\",\"#{";
			updateByPrimaryKeySelectiveProvider = updateByPrimaryKeySelectiveProvider +
					"                if (bean.get" + code + "() != null) {" +
					"SET(\"" + cols[i].getCode().toUpperCase() + " = #{";

			if (!"TIMESTAMP".equalsIgnoreCase(cols[i].getType())) {
				insertSelectiveProvider = insertSelectiveProvider
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}\");}"
						+ LINECHAR;
				updateByPrimaryKeySelectiveProvider = updateByPrimaryKeySelectiveProvider
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}\");}"
						+ LINECHAR;
			} else {
				insertSelectiveProvider = insertSelectiveProvider
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}\");}"
						+ LINECHAR;
				updateByPrimaryKeySelectiveProvider = updateByPrimaryKeySelectiveProvider
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}\");}"
						+ LINECHAR;
			}

			if (!"TIMESTAMP".equalsIgnoreCase(cols[i].getType())) {
				insertSelectiveField = insertSelectiveField
						+ "      <if test=\""
						+ underlineToCamel(cols[i].getCode()) + " != null\">"
						+ cols[i].getCode().toUpperCase() + ",</if>" + LINECHAR;
			} else {
				insertSelectiveField = insertSelectiveField
						+ "      <if test=\""
						+ underlineToCamel(cols[i].getCode()) + " != null\">"
						+ cols[i].getCode().toUpperCase() + ",</if>" + LINECHAR;
			}

			if (!"TIMESTAMP".equalsIgnoreCase(cols[i].getType())) {
				insertSelectiveValue = insertSelectiveValue
						+ "      <if test=\""
						+ underlineToCamel(cols[i].getCode()) + " != null\">#{"
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "},</if>"
						+ LINECHAR;
			} else {
				insertSelectiveValue = insertSelectiveValue
						+ "      <if test=\""
						+ underlineToCamel(cols[i].getCode()) + " != null\">#{"
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP},</if>"
						+ LINECHAR;
			}

			if (!"TIMESTAMP".equalsIgnoreCase(cols[i].getType())) {
				updateField = updateField + "      <if test=\"record."
						+ underlineToCamel(cols[i].getCode()) + " != null\">"
						+ cols[i].getCode().toUpperCase() + " = #{record."
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}, </if>"
						+ LINECHAR;
			}
			else {
				updateField = updateField + "      <if test=\"record."
						+ underlineToCamel(cols[i].getCode()) + " != null\">"
						+ cols[i].getCode().toUpperCase() + " = #{record."
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}, </if>"
						+ LINECHAR;
			}

			if (!"TIMESTAMP".equalsIgnoreCase(cols[i].getType())) {
				updateProvider = updateProvider + "                SET(\"" + cols[i].getCode().toUpperCase() + "= #{"
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}\");" + LINECHAR;

				updateValue = updateValue + "      "
						+ cols[i].getCode().toUpperCase() + " = #{record."
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}";

				updateValueSelect = updateValueSelect + "      \"" +
						cols[i].getCode().toUpperCase() + "= #{record." + underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}";
				updateValueSelectSelective = updateValueSelectSelective + "      \"<if test=\\\"record." + underlineToCamel(cols[i].getCode()) + " != null\\\">" +
						cols[i].getCode().toUpperCase() + "= #{record." + underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}";
			}
			else {
				updateProvider = updateProvider + "                SET(\"" + cols[i].getCode().toUpperCase() + "= #{"
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}\");" + LINECHAR;

				updateValue = updateValue + "      "
						+ cols[i].getCode().toUpperCase() + " = #{record."
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}";

				updateValueSelect = updateValueSelect + "      \"" +
						cols[i].getCode().toUpperCase() + "= #{record." + underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}";

				updateValueSelectSelective = updateValueSelectSelective + "      \"<if test=\\\"record." + underlineToCamel(cols[i].getCode()) + " != null\\\">" +
						cols[i].getCode().toUpperCase() + "= #{record." + underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}";

			}

			if (i < cols.length - 1) {
				insfield = insfield + ",";
				insfieldas = insfieldas + ",";
				insvalue = insvalue + ", ";
				getfield = getfield + ", ";
				updateValue = updateValue + ",";
				updateValueSelect = updateValueSelect + ",\" +" + LINECHAR;
				updateValueSelectSelective = updateValueSelectSelective + ",</if>\" +" + LINECHAR;
				resultMapField = resultMapField + LINECHAR;
			} else {
				updateValueSelect = updateValueSelect + "\" +" + LINECHAR;
				updateValueSelectSelective = updateValueSelectSelective + ",</if>\" +" + LINECHAR;
			}

			updateValue = updateValue + LINECHAR;

		}

		if (keycs != null) {
			for (int i = 0; i < keycs.length; i++) {
				if (i > 0) {
					getkey = getkey + " and ";
					getkeyWithRecord = getkeyWithRecord + LINECHAR + " and ";
				}

				String p = keycs[i].getSPrecision();
				Integer l = keycs[i].getLength();

				getkey = getkey + keycs[i].getCode() + " = #{"
						+ underlineToCamel(keycs[i].getCode()) + ", jdbcType=" + fitType(keycs[i].getType(), l == null ? 0:l, p) + "} ";

				getkeyWithRecord=getkeyWithRecord + keycs[i].getCode() + " = #{record."
						+ underlineToCamel(keycs[i].getCode()) + ", jdbcType=" + fitType(keycs[i].getType(), l == null ? 0:l, p) + "} ";

				if (updset1.length() <= 0) {
					updset1 = updset1 + keycs[i].getCode() + "="
							+ keycs[i].getCode();
				}
			}
		}

		Column[] keycols = (Column[]) null;
		if (key != null) {
			keycols = getPrimaryKey().qryColumn(this.cols);
		}

		msg.add("package " + packPath + ".dao;" + LINECHAR + LINECHAR);

		msg.add(LINECHAR);
		
		msg.add("import org.springframework.stereotype.Component;" + LINECHAR);
		msg.add("import org.apache.ibatis.annotations.*;" + LINECHAR);
		msg.add("import org.apache.ibatis.jdbc.SQL;" + LINECHAR);

		if(StringUtils.isNotBlank(interfaceName)){
			msg.add("import " + interfaceName + ";" + LINECHAR);
		}

		msg.add("import " + packPath + ".bean." + getTableClassName() + ";"
				+ LINECHAR);
		msg.add("import " + packPath + ".bean." + getTableClassName()
				+ "Example;" + LINECHAR);
		msg.add("import " + packPath + ".bean." + getKeyClassName() + ";"
				+ LINECHAR);

		msg.add(LINECHAR);
		msg.add("import java.util.List;" + LINECHAR);
		msg.add("import org.apache.ibatis.annotations.Param;" + LINECHAR
				+ LINECHAR);
		
		msg.add(LINECHAR);

		msg.add("/**" + LINECHAR);
		msg.add(" * " + LINECHAR);
		msg.add(" * <h1>Title : " + getTableCommon() + " Mapper 数据库操作类</h1> "
				+ LINECHAR);
		msg.add(" *" + LINECHAR);
		msg.add(" * @author		.csnowfox.org" + LINECHAR);
		msg.add(" * @version		1.0" + LINECHAR);
		msg.add(" */" + LINECHAR);
		
		msg.add("@Component(value=\"" + packPath + ".dao." + getTableClassName() + "Mapper\")" + LINECHAR);
		String tmpMapper = StringUtils.isNotBlank(interfaceName) ? " extends SqlMapper {" : " {";
		msg.add("public interface " + getTableClassName() + "Mapper" + tmpMapper + LINECHAR);

		msg.add(LINECHAR);

		msg.add("    @Select(\"select count(1) from " + dbuser + table.getTableCode() + " <if test=\\\"_parameter != null\\\">$${ExampleWhereClause}</if>\")" + LINECHAR);
		msg.add("    int countByExample(" + getTableClassName()	+ "Example example);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @Delete(\"delete from " + dbuser + table.getTableCode() + " <if test=\\\"_parameter != null\\\">$${ExampleWhereClause}</if>\")" + LINECHAR);
		msg.add("    int deleteByExample(" + getTableClassName() + "Example example);" + LINECHAR);
		msg.add(LINECHAR);

		msg.add("    @Delete(\"delete from " + dbuser + table.getTableCode() + " where " + getkey + "\")" + LINECHAR);
		msg.add("    int deleteByPrimaryKey(" + getKeyClassName() + " key);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @InsertProvider(type = " + getTableClassName() + "SqlProvider.class, method=\"insertSelective\")" + LINECHAR);
		msg.add("    int insert(" + getTableClassName() + " record);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @InsertProvider(type = " + getTableClassName() + "SqlProvider.class, method=\"insertSelective\")" + LINECHAR);
		msg.add("    int insertSelective(" + getTableClassName() + " record);" + LINECHAR);
		msg.add(LINECHAR);

		msg.add("    @Select(\"$${OracleDialectPrefix} select\\n\" + " + LINECHAR);
		msg.add("        \"<if test=\\\"distinct\\\">distinct</if>\\n\" +" + LINECHAR);
		msg.add("        \"" + insfieldas + "\\n\" +" + LINECHAR);
		msg.add("        \"from " + dbuser + table.getTableCode() + "\\n\" +" + LINECHAR);
		msg.add("        \"<if test=\\\"_parameter != null\\\">$${ExampleWhereClause}</if>\\n\" +" + LINECHAR);
		msg.add("        \"<if test=\\\"orderByClause != null\\\">order by ${orderByClause}</if> \" +" + LINECHAR);
		msg.add("        \"$${OracleDialectSuffix}\")" + LINECHAR);
		msg.add("    List<" + getTableClassName() + "> selectByExample(" + getTableClassName() + "Example example);" + LINECHAR);
		msg.add(LINECHAR);

		msg.add("    @Select(\"select " + insfieldas + " from " + dbuser + table.getTableCode() + (getkey == null || "".equals(getkey.trim())? "":" where " + getkey)  + "\")" + LINECHAR);
		msg.add("    " + getTableClassName() + " selectByPrimaryKey(" + getKeyClassName() + " key);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @Select(\"$${OracleDialectPrefix} select\" + " + LINECHAR);
		msg.add("        \"<if test=\\\"distinct\\\">distinct</if>\" +" + LINECHAR);
		msg.add("        \"" + insfieldas + "\\n\" +" + LINECHAR);
		msg.add("        \"from " + dbuser + table.getTableCode() + "\" +" + LINECHAR);
		msg.add("        \"<if test=\\\"_parameter != null\\\">$${ExampleWhereClause}</if>\" +" + LINECHAR);
		msg.add("        \"<if test=\\\"orderByClause != null\\\">order by ${orderByClause}</if> \" +" + LINECHAR);
		msg.add("        \"$${OracleDialectSuffix} for update\")" + LINECHAR);
		msg.add("    List<" + getTableClassName() + "> selectByExampleForupdate("
				+ getTableClassName() + "Example example);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @Select(\"select " + insfieldas + " from " + dbuser + table.getTableCode() + (getkey == null || "".equals(getkey.trim())? "":" where " + getkey)  + " for update\")" + LINECHAR);
		msg.add("    " + getTableClassName() + " selectByPrimaryKeyForupdate(" + getKeyClassName() + " key);" + LINECHAR);
		msg.add(LINECHAR);

		msg.add("    @Update(\"update " + dbuser + table.getTableCode() + " <set>\" +" + LINECHAR + updateValueSelectSelective +
				"      \"</set><if test=\\\"_parameter != null\\\">$${Update_By_Example_Where_Clause}</if>\")" + LINECHAR);
		msg.add("    int updateByExampleSelective(@Param(\"record\") "
				+ getTableClassName() + " record, @Param(\"example\") "
				+ getTableClassName() + "Example example);" + LINECHAR);
		msg.add(LINECHAR);

		msg.add("    @Update(\"update " + dbuser + table.getTableCode() + " set \" +" + LINECHAR + updateValueSelect +
				"      \"<if test=\\\"_parameter != null\\\">$${Update_By_Example_Where_Clause}</if>\")" + LINECHAR);
		msg.add("    int updateByExample(@Param(\"record\") "
				+ getTableClassName() + " record, @Param(\"example\") "
				+ getTableClassName() + "Example example);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @UpdateProvider(type = " + getTableClassName() + "SqlProvider.class, method = \"updateByPrimaryKeySelective\")" + LINECHAR);
		msg.add("    int updateByPrimaryKeySelective(" + getTableClassName()	+ " record);" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    @UpdateProvider(type = " + getTableClassName() + "SqlProvider.class, method = \"updateByPrimaryKey\")" + LINECHAR);
		msg.add("    int updateByPrimaryKey(" + getTableClassName() + " record);" + LINECHAR);

		msg.add("" +LINECHAR + LINECHAR);
		msg.add("    class " + getTableClassName() + "SqlProvider {\n" +
				"\n" +
				"        private final String tableName = \"" + dbuser + table.getTableCode() + "\";\n" +
				"\n" +
				"        public String insertSelective(final "+ getTableClassName() +" bean) {\n" +
				"            return new SQL() {{\n" +
				"                INSERT_INTO(tableName);\n" + insertSelectiveProvider +
				"            }}.toString();\n" +
				"        }\n" +
				"\n" +
				"        public String updateByPrimaryKey(final " + getTableClassName() + " bean) {\n" +
				"            return new SQL() {{\n" +
				"                UPDATE(tableName);\n" + updateProvider +
				((getkey == null || "".equals(getkey.trim())) ? "" : "                WHERE(\"" + getkey + "\");\n") +
				"            }}.toString();\n" +
				"        }\n" +
				"\n" +
				"        public String updateByPrimaryKeySelective(final " + getTableClassName() + " bean) {\n" +
				"            return new SQL() {{\n" +
				"                UPDATE(tableName);\n" + updateByPrimaryKeySelectiveProvider +
				((getkey == null || "".equals(getkey.trim())) ? "" : "                WHERE(\"" + getkey + "\");\n") +
				"            }}.toString();\n" +
				"        }\n" +
				"    }");
		msg.add("}" + LINECHAR + LINECHAR);
		return msg;
	}

	public List<String> entityExampleMode(String packPath) throws Exception {
		String LINECHAR = "\r\n";
		List msg = new ArrayList();
		Key key = getPrimaryKey();
		Column[] keycols = (Column[]) null;
		if (key != null) {
			keycols = getPrimaryKey().qryColumn(this.cols);
		}

		msg.add("package " + packPath + ".bean;" + LINECHAR + LINECHAR);

		msg.add(LINECHAR);
		msg.add("import java.util.ArrayList;" + LINECHAR);
		msg.add("import java.util.Date;" + LINECHAR);
		msg.add("import java.util.List;" + LINECHAR);
		msg.add("import java.math.BigDecimal;" + LINECHAR);

		msg.add("import net.javaw.mybatis.generator.Page;" + LINECHAR
				+ LINECHAR);
		msg.add(LINECHAR);

		msg.add("/**" + LINECHAR);
		msg.add(" * " + LINECHAR);
		msg.add(" * <h1>Title : " + getTableCommon() + " Example 查询辅助类</h1> "
				+ LINECHAR);
		msg.add(" *" + LINECHAR);
		msg.add(" * @author		.csnowfox.org" + LINECHAR);
		msg.add(" * @version		1.0" + LINECHAR);
		msg.add(" */" + LINECHAR);
		msg.add("public class " + getTableClassName() + "Example {" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("    protected String orderByClause;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    protected boolean distinct;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    protected List<Criteria> oredCriteria;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    protected Page page;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public " + getTableClassName() + "Example() {" + LINECHAR);
		msg.add("        oredCriteria = new ArrayList<Criteria>();" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public void setOrderByClause(String orderByClause) {"
				+ LINECHAR);
		msg.add("        this.orderByClause = orderByClause;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public String getOrderByClause() {" + LINECHAR);
		msg.add("        return orderByClause;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public void setDistinct(boolean distinct) {" + LINECHAR);
		msg.add("        this.distinct = distinct;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public boolean isDistinct() {" + LINECHAR);
		msg.add("        return distinct;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public List<Criteria> getOredCriteria() {" + LINECHAR);
		msg.add("        return oredCriteria;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public void or(Criteria criteria) {" + LINECHAR);
		msg.add("        oredCriteria.add(criteria);" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public Criteria or() {" + LINECHAR);
		msg.add("        Criteria criteria = createCriteriaInternal();"
				+ LINECHAR);
		msg.add("        oredCriteria.add(criteria);" + LINECHAR);
		msg.add("        return criteria;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public Criteria createCriteria() {" + LINECHAR);
		msg.add("        Criteria criteria = createCriteriaInternal();"
				+ LINECHAR);
		msg.add("        if (oredCriteria.size() == 0) {" + LINECHAR);
		msg.add("            oredCriteria.add(criteria);" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add("        return criteria;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    protected Criteria createCriteriaInternal() {" + LINECHAR);
		msg.add("        Criteria criteria = new Criteria();" + LINECHAR);
		msg.add("        return criteria;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public void clear() {" + LINECHAR);
		msg.add("        oredCriteria.clear();" + LINECHAR);
		msg.add("        orderByClause = null;" + LINECHAR);
		msg.add("        distinct = false;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public void setPage(Page page) {" + LINECHAR);
		msg.add("        this.page=page;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    public Page getPage() {" + LINECHAR);
		msg.add("        return page;" + LINECHAR);
		msg.add("    }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("    protected abstract static class GeneratedCriteria {"
				+ LINECHAR);
		msg.add("        protected List<Criterion> criteria;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected GeneratedCriteria() {" + LINECHAR);
		msg.add("            super();" + LINECHAR);
		msg.add("            criteria = new ArrayList<Criterion>();" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public boolean isValid() {" + LINECHAR);
		msg.add("            return criteria.size() > 0;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public List<Criterion> getAllCriteria() {" + LINECHAR);
		msg.add("            return criteria;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public List<Criterion> getCriteria() {" + LINECHAR);
		msg.add("            return criteria;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected void addCriterion(String condition) {"
				+ LINECHAR);
		msg.add("            if (condition == null) {" + LINECHAR);
		msg.add("                throw new RuntimeException(\"Value for condition cannot be null\");"
				+ LINECHAR);
		msg.add("            }" + LINECHAR);
		msg.add("            criteria.add(new Criterion(condition));"
				+ LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected void addCriterion(String condition, Object value, String property) {"
				+ LINECHAR);
		msg.add("            if (value == null) {" + LINECHAR);
		msg.add("                throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"
				+ LINECHAR);
		msg.add("            }" + LINECHAR);
		msg.add("            criteria.add(new Criterion(condition, value));"
				+ LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected void addCriterion(String condition, Object value1, Object value2, String property) {"
				+ LINECHAR);
		msg.add("            if (value1 == null || value2 == null) {"
				+ LINECHAR);
		msg.add("                throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"
				+ LINECHAR);
		msg.add("            }" + LINECHAR);
		msg.add("            criteria.add(new Criterion(condition, value1, value2));"
				+ LINECHAR);
		msg.add("        }" + LINECHAR);

		msg.add(LINECHAR);

		for (int i = 0; i < this.cols.length; i++) {
			String code = this.cols[i].getCode();
			String type = this.cols[i].getType();
			String percision = this.cols[i].getSPrecision();
			String typeRight = getTypeRight(type, this.cols[i].getLength(),
					percision);

			String acode = code.substring(0, 1).toUpperCase()
					+ code.substring(1);
			String camelCode = underlineToCamel(code);
			String camelAcode = underlineToCamel(acode);

			msg.add("       public Criteria and" + camelAcode + "IsNull() {"
					+ LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " is null\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode
					+ "IsNotNull() {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " is not null\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "EqualTo("
					+ typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " =\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "NotEqualTo("
					+ typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " <>\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "GreaterThan("
					+ typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " >\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode
					+ "GreaterThanOrEqualTo(" + typeRight + " value) {"
					+ LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " >=\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "LessThan("
					+ typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " <\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode
					+ "LessThanOrEqualTo(" + typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " <=\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "Like("
					+ typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " like\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "NotLike("
					+ typeRight + " value) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " not like\", value, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "In(List<"
					+ typeRight + "> values) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " in\", values, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "NotIn(List<"
					+ typeRight + "> values) {" + LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " not in\", values, \"" + code + "\");" + LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "Between("
					+ typeRight + " value1, " + typeRight + " value2) {"
					+ LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " between\", value1, value2, \"" + code + "\");"
					+ LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode + "NotBetween("
					+ typeRight + " value1, " + typeRight + " value2) {"
					+ LINECHAR);
			msg.add("            addCriterion(\"" + code.toUpperCase()
					+ " not between\", value1, value2, \"" + code + "\");"
					+ LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add("        public Criteria and" + camelAcode
					+ "LikeInsensitive(String value) {" + LINECHAR);
			msg.add("            addCriterion(\"upper(" + code.toUpperCase()
					+ ") like\", value.toUpperCase(), \"" + code + "\");"
					+ LINECHAR);
			msg.add("            return (Criteria) this;" + LINECHAR);
			msg.add("        }" + LINECHAR);
			msg.add(LINECHAR);
			msg.add(LINECHAR);
		}

		msg.add("    }" + LINECHAR);

		msg.add("   public static class Criteria extends GeneratedCriteria {"
				+ LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected Criteria() {" + LINECHAR);
		msg.add("            super();" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add("    }" + LINECHAR);

		msg.add("   public static class Criterion {" + LINECHAR);
		msg.add("        private String condition;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private Object value;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private Object secondValue;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private boolean noValue;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private boolean singleValue;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private boolean betweenValue;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private boolean listValue;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        private String typeHandler;" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public String getCondition() {" + LINECHAR);
		msg.add("            return condition;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public Object getValue() {" + LINECHAR);
		msg.add("            return value;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public Object getSecondValue() {" + LINECHAR);
		msg.add("            return secondValue;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public boolean isNoValue() {" + LINECHAR);
		msg.add("            return noValue;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public boolean isSingleValue() {" + LINECHAR);
		msg.add("            return singleValue;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public boolean isBetweenValue() {" + LINECHAR);
		msg.add("            return betweenValue;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public boolean isListValue() {" + LINECHAR);
		msg.add("            return listValue;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        public String getTypeHandler() {" + LINECHAR);
		msg.add("            return typeHandler;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected Criterion(String condition) {" + LINECHAR);
		msg.add("            super();" + LINECHAR);
		msg.add("            this.condition = condition;" + LINECHAR);
		msg.add("            this.typeHandler = null;" + LINECHAR);
		msg.add("            this.noValue = true;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected Criterion(String condition, Object value, String typeHandler) {"
				+ LINECHAR);
		msg.add("            super();" + LINECHAR);
		msg.add("            this.condition = condition;" + LINECHAR);
		msg.add("            this.value = value;" + LINECHAR);
		msg.add("            this.typeHandler = typeHandler;" + LINECHAR);
		msg.add("            if (value instanceof List<?>) {" + LINECHAR);
		msg.add("                this.listValue = true;" + LINECHAR);
		msg.add("            } else {" + LINECHAR);
		msg.add("                this.singleValue = true;" + LINECHAR);
		msg.add("            }" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected Criterion(String condition, Object value) {"
				+ LINECHAR);
		msg.add("            this(condition, value, null);" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {"
				+ LINECHAR);
		msg.add("            super();" + LINECHAR);
		msg.add("            this.condition = condition;" + LINECHAR);
		msg.add("            this.value = value;" + LINECHAR);
		msg.add("            this.secondValue = secondValue;" + LINECHAR);
		msg.add("            this.typeHandler = typeHandler;" + LINECHAR);
		msg.add("            this.betweenValue = true;" + LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("        protected Criterion(String condition, Object value, Object secondValue) {"
				+ LINECHAR);
		msg.add("            this(condition, value, secondValue, null);"
				+ LINECHAR);
		msg.add("        }" + LINECHAR);
		msg.add("    }" + LINECHAR);

		msg.add("}" + LINECHAR + LINECHAR);
		return msg;
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
		if (type.equals("NUMBER")) {
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
		}} else if (type.equals("DATE")) {
			typeRight = "Date";
		} else if (type.equals("TIMESTAMP")) {
			typeRight = "Date";
		} else if (type.equals("INTEGER")) {
			typeRight = "Integer";
		} else {
			typeRight = "String";
		}

		return typeRight;
	}

}

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}
