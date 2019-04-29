package org.csnowfox.maven.plugin.model2mybatis.pdm;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.csnowfox.maven.plugin.model2mybatis.entity.Column;
import org.csnowfox.maven.plugin.model2mybatis.entity.Key;
import org.csnowfox.maven.plugin.model2mybatis.entity.Table;
import org.csnowfox.maven.plugin.model2mybatis.utils.MavenLogger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.csnowfox.maven.plugin.model2mybatis.entity.User;

/**
 * @ClassName: PdmParser
 * @Description pdm文件解析器
 * @Author Csnowfox
 * @Date 2019/4/27 16:45
 **/
public class PdmParser {

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
			MavenLogger.info("=====read error[file:" + filePath + "]======");
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
					if (eOwner == null) {
						throw new Exception("表[" + vo.getTableCode() + "]无所属用户");
					}
					Element eUser = eOwner.element("User");
					if (eUser == null) {
						throw new Exception("表[" + vo.getTableCode() + "]所属用户[" + eUser.getName() + "]不存在");
					}

					String userid = eUser.attributeValue("Ref");
					if (userid == null) {
						throw new Exception("表[" + vo.getTableCode() + "]所属用户[" + eUser.getName() + "]无关联存在");
					}
					vo.setUser((User) mpUser.get(userid));
				} catch (Exception e) {
					e.printStackTrace();
					MavenLogger.error("[" + new Date() + "]+++++++++有错误[" + vo.getComment() + ", " + vo.getTableCode() + "] 具体如下：++++");
					MavenLogger.error("Error", e);
					MavenLogger.error("[" + new Date() + "]+++++++++有错误[" + vo.getComment() + ", " + vo.getTableCode() + "] 结束++++");
				}
				voS.add(vo);
			}
		}
		return (Table[]) voS.toArray(tabs);
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
			col.setSPrecision(e_col.elementTextTrim("Precision"));
			col.setName(e_col.elementTextTrim("Name"));
			col.setComment(e_col.elementTextTrim("Comment"));
			col.setSLength(e_col.elementTextTrim("Length"));
			col.setLength(e_col.elementTextTrim("Length") == null ? null
					: Integer.valueOf(Integer.parseInt(e_col
							.elementTextTrim("Length"))));
			col.setTypefull(e_col.elementTextTrim("DataType"));
			if (e_col.elementTextTrim("DataType").indexOf("(") > 0) {
				col.setType(e_col.elementTextTrim("DataType").substring(0,
						e_col.elementTextTrim("DataType").indexOf("(")));
			} else {
				col.setType(e_col.elementTextTrim("DataType"));
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
		if (e_table.element("PrimaryKey") == null) {
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