package ch.mno.tatoo.facade.tac;

enum RETURN_CODES {
		SUCCESS(0, "Success"),
		UNKNOWN_ERROR(1, "Unknown error"),
		INVALID_REQUEST(2, "Invalid request"),
		AUTHENTICATION_ERROR(3, "Authentication error"),
		LICENSE_PROBLEM(4, "License problem"),
		INVALID_PARAMETER(5, "Invalid parameter"),
		ERROR_FORMATTING_RESPONSE(6, "Error formatting response"),

		// deleteTask
		//	5: No task with this taskId
		TASK_IS_PROCESSING_DURING_DELETION(230, "Task is processing during deletion");

		private int code;
		private String message;

		RETURN_CODES(int code, String message) {
			this.code = code;
			this.message = message;
		}

	public String getMessage() {
		return message;
	}

		public static RETURN_CODES fromCode(Long value) {
			if (value==null) return null;
			int ivalue = value.intValue();
			for (RETURN_CODES itReturnCode: values()) {
				if (itReturnCode.code==ivalue) {
					return itReturnCode;
				}
			}
			return null;
		}


	}