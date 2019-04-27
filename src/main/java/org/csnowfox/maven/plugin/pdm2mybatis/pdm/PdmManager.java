package org.csnowfox.maven.plugin.pdm2mybatis.pdm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Table;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.csnowfox.maven.plugin.pdm2mybatis.LogUtils;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Column;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Key;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.User;

public class PdmManager {
	
	Logger log = Logger.getLogger(getClass());

	public Table[] parsePDM_VO(String tableName, String filePath) {
		Table[] tabs = new Table[0];
		List voS = new ArrayList();
		Table vo = null;
		Column[] cols = (Column[]) null;
		Key[] keys = (Key[]) null;
		File f = new File(filePath);
		SAXReader sr = new SAXReader();
		Document doc = null;
		try {
			doc = sr.read(f);
		} catch (DocumentException e) {
			LogUtils.logger.info("=====read error[file:" + filePath + "]======");
			e.printStackTrace();
		}
		HashMap mpUser = readUsers(doc);
		Iterator itr = doc.selectNodes("//c:Tables//o:Table").iterator();
		while (itr.hasNext()) {
			vo = new Table();
			cols = new Column[0];
			keys = new Key[0];
			Element e_table = (Element) itr.next();
			if ((tableName == null) || (tableName.isEmpty())
					|| (tableName.equals(e_table.elementTextTrim("Code")))) {
				try {
					vo.setId(e_table.attributeValue("Id"));
					vo.setTableName(e_table.elementTextTrim("Name"));
					vo.setTableCode(e_table.elementTextTrim("Code"));
					vo.setComment(e_table.elementTextTrim("Comment"));
					vo.setCols((Column[]) readColums(e_table).toArray(cols));
					vo.setKeys((Key[]) readKeys(e_table).toArray(keys));

					Element eOwner = e_table.element("Owner");

					if (eOwner != null) {
						Element eUser = eOwner.element("User");
						if (eUser != null) {
							String userid = eUser.attributeValue("Ref");
							if (userid != null) {
								vo.setUser((User) mpUser.get(userid));
							}

						}

					}

				} catch (Exception e) {
					e.printStackTrace();
					this.log.error("[" + new Date() + "]+++++++++有错误["
							+ vo.getComment() + ", " + vo.getTableCode()
							+ "] 具体如下：++++");
					this.log.error(e);
					this.log.error("[" + new Date() + "]+++++++++有错误["
							+ vo.getComment() + ", " + vo.getTableCode()
							+ "] 结束++++");
				}
				voS.add(vo);
			}
		}
		return (Table[]) voS.toArray(tabs);
	}

	public boolean updPDM_VO(String tableName, String filePath, String outpath,
			List<Column> collis) {
		File f = new File(filePath);
		SAXReader sr = new SAXReader();
		Document doc = null;
		try {
			doc = sr.read(f);
		} catch (DocumentException e) {
			LogUtils.logger.info("=====read error[file:" + filePath + "]======");
			e.printStackTrace();
		}
		Iterator itr = doc.selectNodes("//c:Tables//o:Table").iterator();
		while (itr.hasNext()) {
			Element e_table = (Element) itr.next();
			if ((tableName == null) || (tableName.isEmpty())
					|| (tableName.equals(e_table.elementTextTrim("Code")))) {
				String comment = "";
				String name = "";
				try {
					Iterator itc = e_table.selectNodes("//c:Columns//o:Column")
							.iterator();
					while (itc.hasNext()) {
						Element ec = (Element) itc.next();
						String id = ec.attributeValue("Id");

						name = ec.elementTextTrim("Name");
						comment = ec.elementTextTrim("Comment");
						String dataType = ec.elementTextTrim("DataType");
						String length = ec.elementTextTrim("Length");
						String precision = ec.elementTextTrim("Precision");
						if (id.equals("o157")) {
							LogUtils.logger.info("=====" + name + "======");
						}
						Column c = getColumn(name, collis);
						if (c == null)
							c = getColumn(comment, collis);
						if (c != null) {
							Node ncode = ec.selectSingleNode("a:Code");
							ncode.setText(c.getCode().toUpperCase());

							if (dataType != null) {
								Node ndatatype = ec
										.selectSingleNode("a:DataType");
								ndatatype
										.setText(c.getTypefull().toUpperCase());
							} else {
								ec.addElement("a:DataType").setText(
										c.getTypefull().toUpperCase());
							}

							if (length != null) {
								if (c.getSLength() != null) {
									Node n = ec.selectSingleNode("a:Length");
									n.setText(c.getSLength());
								}
							} else if (c.getSLength() != null) {
								ec.addElement("a:Length").setText(
										c.getSLength());
							}

							if (precision != null) {
								if (c.getSPrecision() != null) {
									Node n = ec.selectSingleNode("a:Precision");
									n.setText(c.getSPrecision().toUpperCase());
								}
							} else if (c.getSPrecision() != null) {
								ec.addElement("a:Precision").setText(
										c.getSPrecision());
							}

						}

						if (comment == null) {
							ec.addElement("a:Comment").setText(
									name.toUpperCase());
						}
						Node nComment = ec.selectSingleNode("a:Comment");
						System.out.println(nComment.getText());
					}

				} catch (Exception e) {
					e.printStackTrace();
					this.log.error("[" + new Date() + "]+++++++++有错误[" + name	+ ", " + comment + "] 具体如下：++++");
					this.log.error(e);
					this.log.error("[" + new Date() + "]+++++++++有错误[" + name	+ ", " + comment + "] 结束++++");
				}
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();

		format.setEncoding("UTF-8");
		try {
			FileOutputStream output = new FileOutputStream(new File(outpath));
			XMLWriter writer = new XMLWriter(output, format);
			writer.write(doc);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private Column getColumn(String name, List<Column> collis) {
		if (name == null) {
			return null;
		}
		for (Column c : collis) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		int minindex = -1;
		int count = collis.size();
		for (int i = 0; i < collis.size(); i++) {
			Column c = (Column) collis.get(i);
			int namelen = name.length();
			int clen = c.getName().length();
			int indexof = name.indexOf(((Column) collis.get(i)).getName());
			if ((indexof >= 0) && (namelen - clen < count)) {
				count = namelen - clen;
				minindex = i;
			}

		}

		if (minindex >= 0) {
			return (Column) collis.get(minindex);
		}
		return null;
	}

	private Column getColumnCode(String code, List<Column> collis) {
		for (Column c : collis) {
			if (c.getCode().equalsIgnoreCase(code)) {
				return c;
			}
		}
		return null;
	}

	private HashMap<String, User> readUsers(Document doc) {
		HashMap mp = new HashMap();
		Iterator itr = doc.selectNodes("//c:Users//o:User").iterator();
		while (itr.hasNext()) {
			Element e_user = (Element) itr.next();
			User u = new User();
			u.setId(e_user.attributeValue("Id"));
			u.setCode(e_user.elementTextTrim("Code"));
			u.setName(e_user.elementTextTrim("Name"));
			mp.put(u.getId(), u);
		}
		return mp;
	}

	private List<Column> readColums(Element e_table) {
		List list = new ArrayList();
		Column col = null;
		Iterator itr1 = e_table.element("Columns").elements("Column")
				.iterator();
		while (itr1.hasNext()) {
			col = new Column();
			Element e_col = (Element) itr1.next();
			col.setId(e_col.attributeValue("Id"));
			col.setDefaultValue(e_col.elementTextTrim("DefaultValue"));
			col.setCode(e_col.elementTextTrim("Code"));
			col.setName(e_col.elementTextTrim("Name"));
			col.setComment(e_col.elementTextTrim("Comment"));
			col.setSLength(e_col.elementTextTrim("Length"));
			col.setLength(e_col.elementTextTrim("Length") == null ? null
					: Integer.valueOf(Integer.parseInt(e_col
							.elementTextTrim("Length"))));
			col.setTypefull(e_col.elementTextTrim("DataType"));
			if (col.getTypefull() != null) {
				if (e_col.elementTextTrim("DataType").indexOf("(") > 0) {
					col.setType(e_col.elementTextTrim("DataType").substring(0,
							e_col.elementTextTrim("DataType").indexOf("(")));
				} else {
					col.setType(e_col.elementTextTrim("DataType"));
				}
			}

			list.add(col);
		}

		return list;
	}

	private List<Key> readKeys(Element e_table) {
		List lisKey = new ArrayList();
		Key key = null;
		if (e_table.element("Keys") == null) {
			return lisKey;
		}
		String keys_primarykey_ref_id = e_table.element("PrimaryKey")
				.element("Key").attributeValue("Ref");
		Iterator itkey = e_table.element("Keys").elements("Key").iterator();
		while (itkey.hasNext()) {
			key = new Key();
			Element ekey = (Element) itkey.next();
			if (ekey.element("Key.Columns") != null) {
				key.setId(ekey.attributeValue("Id"));
				key.setName(ekey.elementTextTrim("Name"));
				key.setCode(ekey.elementTextTrim("Code"));
				List lisColum = ekey.element("Key.Columns").elements("Column");
				String[] columnId = new String[lisColum.size()];
				for (int j = 0; j < lisColum.size(); j++) {
					columnId[j] = ((Element) lisColum.get(j))
							.attributeValue("Ref");
				}
				key.setColumnId(columnId);
				if (keys_primarykey_ref_id.equals(key.getId())) {
					key.setPkFlag(Boolean.valueOf(true));
				}
				lisKey.add(key);
			}
		}
		return lisKey;
	}

	public void initTable(Table[] tabs) {
		List list = new ArrayList();
		for (Table tab : tabs) {
			list.add(tab.getTableName());
			System.out.println(tab.getTableName());
		}
	}
}