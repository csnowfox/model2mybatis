package org.csnowfox.maven.plugin.pdm2mybatis.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.csnowfox.maven.plugin.pdm2mybatis.CreateFile;
import org.codehaus.plexus.util.FileUtils;

/**
 * mapper xml文件生成服务类
 */
public class Xml {

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

	public void writeout(Table table, String packPath, String xmlPath)
			throws Exception {
		List lismsg = xmlMode(table, table.getDbUser(), packPath + "." + table.getUser().getName().toLowerCase());
		String name = table.getTableCode().toLowerCase() + "-mapper.xml";
		CreateFile.writeFile(lismsg, xmlPath + File.separator + table.getUser().getName().toLowerCase() + File.separator, name);
		// 0.0.8升级至0.0.9版本后清除代码中的xml文件,0.0.9的做法没有修改xml的生成，只是在生成后把他delete掉了。
		System.out.println("##delete file:" + xmlPath + File.separator + table.getUser().getName().toLowerCase() + File.separator + name);
		FileUtils.fileDelete(xmlPath + File.separator + table.getUser().getName().toLowerCase() + File.separator + name);
	}

	public List<String> xmlMode(Table table, String dbuser, String packPath)
			throws Exception {
		String LINECHAR = "\r\n";
		String resultMapField = "";
		String insfield = "";
		String insvalue = "";
		String getfield = "";
		String getkey = "";
		String getkeyWithRecord="";
		String getwhere = "";
		String updset1 = "";
		String updset2 = "";
		String insertSelectiveField = "";
		String insertSelectiveValue = "";
		String updateField = "";
		String updateValue = "";
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
				updateValue = updateValue + "      "
						+ cols[i].getCode().toUpperCase() + " = #{record."
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=" + fitType(cols[i].getType(), l == null ? 0:l, p) + "}";
			}
			else {
				updateValue = updateValue + "      "
						+ cols[i].getCode().toUpperCase() + " = #{record."
						+ underlineToCamel(cols[i].getCode()) + ", jdbcType=TIMESTAMP}";
			}

			if (i < cols.length - 1) {
				insfield = insfield + ", ";
				insvalue = insvalue + ", ";
				getfield = getfield + ", ";
				updateValue = updateValue + ",";
				resultMapField = resultMapField + LINECHAR;
			}

			updateValue = updateValue + LINECHAR;

		}

		if (keycs != null) {
			for (int i = 0; i < keycs.length; i++) {
				if (i > 0) {
					getkey = getkey + LINECHAR + "                  and ";
					getkeyWithRecord = getkeyWithRecord + LINECHAR + "                  and ";
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
		msg.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + LINECHAR);
		msg.add("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
				+ LINECHAR);
		msg.add("<mapper namespace=\"" + packPath + ".dao."
				+ table.getTableClassName() + "Mapper\">" + LINECHAR + LINECHAR);

		msg.add("  <resultMap id=\"BaseResultMap\" type=\"" + packPath
				+ ".bean." + table.getTableClassName() + "\">" + LINECHAR);
		msg.add(resultMapField + LINECHAR);
		msg.add("  </resultMap>");

		msg.add(LINECHAR + LINECHAR);

		msg.add("  <sql id=\"Example_Where_Clause\">" + LINECHAR);
		msg.add("    <where>" + LINECHAR);
		msg.add("      <foreach collection=\"oredCriteria\" item=\"criteria\" separator=\"or\">"
				+ LINECHAR);
		msg.add("        <if test=\"criteria.valid\">" + LINECHAR);
		msg.add("          <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">"
				+ LINECHAR);
		msg.add("            <foreach collection=\"criteria.criteria\" item=\"criterion\">"
				+ LINECHAR);
		msg.add("              <choose>" + LINECHAR);
		msg.add("                <when test=\"criterion.noValue\">" + LINECHAR);
		msg.add("                  and ${criterion.condition}" + LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("                <when test=\"criterion.singleValue\">"
				+ LINECHAR);
		msg.add("                  and ${criterion.condition} #{criterion.value}"
				+ LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("                <when test=\"criterion.betweenValue\">"
				+ LINECHAR);
		msg.add("                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"
				+ LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("                <when test=\"criterion.listValue\">"
				+ LINECHAR);
		msg.add("                  and ${criterion.condition}" + LINECHAR);
		msg.add("                  <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">"
				+ LINECHAR);
		msg.add("                    #{listItem}" + LINECHAR);
		msg.add("                  </foreach>" + LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("              </choose>" + LINECHAR);
		msg.add("            </foreach>" + LINECHAR);
		msg.add("          </trim>" + LINECHAR);
		msg.add("        </if>" + LINECHAR);
		msg.add("      </foreach>" + LINECHAR);
		msg.add("    </where>" + LINECHAR);
		msg.add("  </sql>" + LINECHAR);
		msg.add(LINECHAR);
		msg.add("  <sql id=\"Update_By_Example_Where_Clause\">" + LINECHAR);
		msg.add("    <where>" + LINECHAR);
		msg.add("      <foreach collection=\"example.oredCriteria\" item=\"criteria\" separator=\"or\">"
				+ LINECHAR);
		msg.add("        <if test=\"criteria.valid\">" + LINECHAR);
		msg.add("          <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">"
				+ LINECHAR);
		msg.add("            <foreach collection=\"criteria.criteria\" item=\"criterion\">"
				+ LINECHAR);
		msg.add("              <choose>" + LINECHAR);
		msg.add("                <when test=\"criterion.noValue\">" + LINECHAR);
		msg.add("                  and ${criterion.condition}" + LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("                <when test=\"criterion.singleValue\">"
				+ LINECHAR);
		msg.add("                  and ${criterion.condition} #{criterion.value}"
				+ LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("                <when test=\"criterion.betweenValue\">"
				+ LINECHAR);
		msg.add("                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"
				+ LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("                <when test=\"criterion.listValue\">"
				+ LINECHAR);
		msg.add("                  and ${criterion.condition}" + LINECHAR);
		msg.add("                  <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">"
				+ LINECHAR);
		msg.add("                    #{listItem}" + LINECHAR);
		msg.add("                  </foreach>" + LINECHAR);
		msg.add("                </when>" + LINECHAR);
		msg.add("              </choose>" + LINECHAR);
		msg.add("            </foreach>" + LINECHAR);
		msg.add("          </trim>" + LINECHAR);
		msg.add("        </if>" + LINECHAR);
		msg.add("      </foreach>" + LINECHAR);
		msg.add("    </where>" + LINECHAR);
		msg.add("  </sql>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <sql id=\"Base_Column_List\">" + LINECHAR);
		msg.add("    " + insfield + LINECHAR);
		msg.add("  </sql>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <select id=\"selectByExample\" parameterType=\"" + packPath
				+ ".bean." + table.getTableClassName()
				+ "Example\" resultMap=\"BaseResultMap\">" + LINECHAR);
		msg.add("    <include refid=\"OracleDialectPrefix\" />");
		msg.add("    select" + LINECHAR);
		msg.add("    <if test=\"distinct\">" + LINECHAR);
		msg.add("      distinct" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("    <include refid=\"Base_Column_List\" />" + LINECHAR);
		msg.add("    from " + dbuser + table.getTableCode() + LINECHAR);
		msg.add("    <if test=\"_parameter != null\">" + LINECHAR);
		msg.add("      <include refid=\"Example_Where_Clause\" />" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("    <if test=\"orderByClause != null\">" + LINECHAR);
		msg.add("      order by ${orderByClause}" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("    <include refid=\"OracleDialectSuffix\" />" + LINECHAR);
		msg.add("  </select>" + LINECHAR);
		
		msg.add(LINECHAR);

		msg.add("  <select id=\"selectByExampleForupdate\" parameterType=\"" + packPath
				+ ".bean." + table.getTableClassName()
				+ "Example\" resultMap=\"BaseResultMap\">" + LINECHAR);
		msg.add("    <include refid=\"OracleDialectPrefix\" />");
		msg.add("    select" + LINECHAR);
		msg.add("    <if test=\"distinct\">" + LINECHAR);
		msg.add("      distinct" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("    <include refid=\"Base_Column_List\" />" + LINECHAR);
		msg.add("    from " + dbuser + table.getTableCode() + LINECHAR);
		msg.add("    <if test=\"_parameter != null\">" + LINECHAR);
		msg.add("      <include refid=\"Example_Where_Clause\" />" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("    <if test=\"orderByClause != null\">" + LINECHAR);
		msg.add("      order by ${orderByClause}" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("    <include refid=\"OracleDialectSuffix\" />" + LINECHAR);
		msg.add("    for update" + LINECHAR);
		msg.add("  </select>" + LINECHAR);		

		msg.add(LINECHAR);
		
		if ((keycs != null) && (keycs.length > 0)) {
			msg.add("  <select id=\"selectByPrimaryKey\" parameterType=\"" + packPath
					+ ".bean." + table.getKeyClassName()+ "\" resultMap=\"BaseResultMap\">" + LINECHAR);
				msg.add("    select " + LINECHAR);
				msg.add("    <include refid=\"Base_Column_List\" />" + LINECHAR);
				msg.add("    from " + dbuser + table.getTableCode() + LINECHAR);
				msg.add("    where " + getkey + LINECHAR);
				msg.add("  </select>" + LINECHAR);
				
				msg.add(LINECHAR);
			

			msg.add("  <select id=\"selectByPrimaryKeyForupdate\" parameterType=\"" + packPath
					+ ".bean." + table.getKeyClassName()+ "\" resultMap=\"BaseResultMap\">" + LINECHAR);
				msg.add("    select " + LINECHAR);
				msg.add("    <include refid=\"Base_Column_List\" />" + LINECHAR);
				msg.add("    from " + dbuser + table.getTableCode() + LINECHAR);
				msg.add("    where " + getkey + LINECHAR);
				msg.add("    for update");
				msg.add("  </select>" + LINECHAR);
				
				msg.add(LINECHAR);
			
			msg.add("  <delete id=\"deleteByPrimaryKey\" parameterType=\"" + packPath
				+ ".bean." + table.getKeyClassName()+ "\">" + LINECHAR);
			msg.add("    delete from " + dbuser + table.getTableCode() + LINECHAR);
			msg.add("    where " + getkey + LINECHAR);
			msg.add("  </delete>" + LINECHAR);
			
			msg.add(LINECHAR);
		}
		
		msg.add("  <delete id=\"deleteByExample\" parameterType=\"" + packPath
				+ ".bean." + table.getTableClassName() + "Example\">"
				+ LINECHAR);
		msg.add("    delete from " + dbuser + table.getTableCode() + LINECHAR);
		msg.add("    <if test=\"_parameter != null\">" + LINECHAR);
		msg.add("      <include refid=\"Example_Where_Clause\" />" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("  </delete>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <insert id=\"insert\" parameterType=\"" + packPath
				+ ".bean." + table.getTableClassName() + "\">" + LINECHAR);
		msg.add("    insert into " + dbuser + table.getTableCode() + " ("
				+ insfield + ")" + LINECHAR);
		msg.add("    values (" + insvalue + ")" + LINECHAR);
		msg.add("  </insert>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <insert id=\"insertSelective\" parameterType=\"" + packPath
				+ ".bean." + table.getTableClassName() + "\">" + LINECHAR);
		msg.add("    insert into " + dbuser + table.getTableCode() + LINECHAR);
		msg.add("    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">"
				+ LINECHAR);
		msg.add(insertSelectiveField);
		msg.add("    </trim>" + LINECHAR);
		msg.add("    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">"
				+ LINECHAR);
		msg.add(insertSelectiveValue);
		msg.add("    </trim>" + LINECHAR);
		msg.add("  </insert>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <select id=\"countByExample\" parameterType=\"" + packPath
				+ ".bean." + table.getTableClassName()
				+ "Example\" resultType=\"java.lang.Integer\">" + LINECHAR);
		msg.add("    select count(*) from " + dbuser + table.getTableCode()
				+ LINECHAR);
		msg.add("    <if test=\"_parameter != null\">" + LINECHAR);
		msg.add("      <include refid=\"Example_Where_Clause\" />" + LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("  </select>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <update id=\"updateByExampleSelective\" parameterType=\"map\">"
				+ LINECHAR);
		msg.add("    update " + dbuser + table.getTableCode() + LINECHAR);
		msg.add("    <set>" + LINECHAR);
		msg.add(updateField);
		msg.add("    </set>" + LINECHAR);
		msg.add("    <if test=\"_parameter != null\">" + LINECHAR);
		msg.add("      <include refid=\"Update_By_Example_Where_Clause\" />"
				+ LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("  </update>" + LINECHAR);

		msg.add(LINECHAR);

		msg.add("  <update id=\"updateByExample\" parameterType=\"map\">"
				+ LINECHAR);
		msg.add("    update " + dbuser + table.getTableCode() + LINECHAR);
		msg.add("    set " + LINECHAR);
		msg.add(updateValue);
		msg.add("    <if test=\"_parameter != null\">" + LINECHAR);
		msg.add("      <include refid=\"Update_By_Example_Where_Clause\" />"
				+ LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("  </update>" + LINECHAR);

		msg.add(LINECHAR);

		if ((keycs != null) && (keycs.length > 0)) {
			msg.add("  <update id=\"updateByPrimaryKeySelective\" parameterType=\"map\">"
					+ LINECHAR);
			msg.add("    update " + dbuser + table.getTableCode() + LINECHAR);
			msg.add("    <set>" + LINECHAR);
			msg.add(updateField);
			msg.add("    </set>" + LINECHAR);
			msg.add("    where " + getkeyWithRecord + LINECHAR);
			msg.add("  </update>" + LINECHAR);
	
			msg.add(LINECHAR);
	
			msg.add("  <update id=\"updateByPrimaryKey\" parameterType=\"map\">"
					+ LINECHAR);
			msg.add("    update " + dbuser + table.getTableCode() + LINECHAR);
			msg.add("    set " + LINECHAR);
			msg.add(updateValue);
			msg.add("    where " + getkeyWithRecord + LINECHAR);
			msg.add("  </update>" + LINECHAR);
	
			msg.add(LINECHAR);
		}
		
		msg.add("  <sql id=\"OracleDialectPrefix\" >" + LINECHAR);
		msg.add("    <if test=\"page != null\" >" + LINECHAR);
		msg.add("      select * from ( select row_.*, rownum rownum_ from ( "
				+ LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("  </sql>" + LINECHAR);
		msg.add("  <sql id=\"OracleDialectSuffix\" >" + LINECHAR);
		msg.add("    <if test=\"page != null\" >" + LINECHAR);
		msg.add("      <![CDATA[ ) row_ ) where rownum_ > #{page.begin} and rownum_ <= #{page.end} ]]>"
				+ LINECHAR);
		msg.add("    </if>" + LINECHAR);
		msg.add("  </sql>" + LINECHAR);

		msg.add(LINECHAR + LINECHAR + "</mapper>");

		return msg;
	}

	/**
	 * 类型适配成数据库对应类型
	 * @param type
	 * @param length
	 * @param percision
	 * @return
	 */
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
}