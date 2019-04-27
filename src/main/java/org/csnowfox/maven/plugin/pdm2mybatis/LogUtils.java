package org.csnowfox.maven.plugin.pdm2mybatis;

import org.apache.maven.plugin.logging.Log;

/**
 * 日志服务类
 */
public class LogUtils {

	public static Log logger;

	static {

		logger = new Log() {

			public boolean isDebugEnabled() {
				return false;
			}

			public void debug(CharSequence charsequence) {

			}

			public void debug(CharSequence charsequence, Throwable throwable) {

			}

			public void debug(Throwable throwable) {

			}

			public boolean isInfoEnabled() {
				return false;
			}

			public void info(CharSequence charsequence) {
				System.out.println(charsequence);
			}

			public void info(CharSequence charsequence, Throwable throwable) {

			}

			public void info(Throwable throwable) {

			}

			public boolean isWarnEnabled() {
				return false;
			}

			public void warn(CharSequence charsequence) {

			}

			public void warn(CharSequence charsequence, Throwable throwable) {

			}

			public void warn(Throwable throwable) {

			}

			public boolean isErrorEnabled() {
				return false;
			}

			public void error(CharSequence charsequence) {

			}

			public void error(CharSequence charsequence, Throwable throwable) {

			}

			public void error(Throwable throwable) {

			}

		};
	}
}
