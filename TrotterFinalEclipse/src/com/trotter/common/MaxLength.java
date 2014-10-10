package com.trotter.common;

public interface MaxLength {

	public static interface UserTblFields{
		public static final int userEmailId = 100;
		public static final int userId = 100;
		public static final int userFirstName = 30;
		public static final int userLastName = 30;
		public static final int userPassword = 10;
		public static final int userCompanyName = 100;
	}
	
	public static interface AppTblFields{
		public static final int appName = 100;
		public static final int appPackage = 200;
		public static final int appC2DM = 50;
	}
	
	public static interface NoteTblFields{
		public static final int noteMessage = 500;
		public static final int noteUrl = 200;
		public static final int noteSmsMsg = 160;
	}
	
}
