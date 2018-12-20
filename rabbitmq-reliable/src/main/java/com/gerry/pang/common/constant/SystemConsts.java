package com.gerry.pang.common.constant;

public interface SystemConsts {

	interface BaseConstants {
		public static final String YES = "yes";
		public static final String NO = "no";

		public static final String YES_NUMBER = "0";
		public static final String NO_NUMBER = "1";
	}

	interface MessageConstants {
		public static final String ORDER_SENDING = "0"; // 发送中

		public static final String ORDER_SEND_SUCCESS = "1"; // 成功

		public static final String ORDER_SEND_FAILURE = "2"; // 失败

		public static final int ORDER_TIMEOUT = 1; /* 分钟超时单位：min */
	}

}
