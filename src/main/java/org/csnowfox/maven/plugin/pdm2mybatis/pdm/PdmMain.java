package org.csnowfox.maven.plugin.pdm2mybatis.pdm;

import java.util.List;

import org.csnowfox.maven.plugin.pdm2mybatis.entity.Column;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Table;

public class PdmMain {

	public static void readpdm(String tablename, String pdmPath,
			List<String> colnamelis) {
		PdmManager pp = new PdmManager();
		Table[] tab = pp.parsePDM_VO(tablename, pdmPath);
		for (Table t : tab) {
			Column[] cols = t.getCols();
			for (Column c : cols) {
				if (!isHave(colnamelis, c.getName())) {
					colnamelis.add(c.getName());
				}
				if (!isHave(colnamelis, c.getComment())) {
					colnamelis.add(c.getComment());
				}
			}
		}
	}

	public static boolean isHave(List<String> colnamelis, String colname) {
		if ((colname == null) || (colname.trim().length() <= 0)) {
			return true;
		}
		for (String s : colnamelis) {
			if (s.indexOf(colname) >= 0) {
				return true;
			}
			if (colname.indexOf(s) >= 0) {
				return true;
			}
		}
		return false;
	}

}