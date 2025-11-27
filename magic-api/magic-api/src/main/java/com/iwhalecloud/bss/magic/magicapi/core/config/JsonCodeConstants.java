package com.iwhalecloud.bss.magic.magicapi.core.config;

import org.apache.commons.lang3.StringUtils;
import com.iwhalecloud.bss.magic.magicapi.core.exception.InvalidArgumentException;
import com.iwhalecloud.bss.magic.magicapi.core.model.JsonCode;

public interface JsonCodeConstants {

	JsonCode SUCCESS = new JsonCode(1, Constants.RESPONSE_MESSAGE_SUCCESS);

	JsonCode IS_READ_ONLY = new JsonCode(-2, "Currently in read-only mode, operation is not possible");

	JsonCode PERMISSION_INVALID = new JsonCode(-10, "Permission denied.");

	JsonCode GROUP_NOT_FOUND = new JsonCode(1001, "Group information not found");

	JsonCode NOT_SUPPORTED_GROUP_TYPE = new JsonCode(1002, "This group type is not supported");

	JsonCode TARGET_IS_REQUIRED = new JsonCode(1003, "Target URL cannot be empty");

	JsonCode SECRET_KEY_IS_REQUIRED = new JsonCode(1004, "SecretKey cannot be empty");

	JsonCode MOVE_NAME_CONFLICT = new JsonCode(1005, "The name will be duplicated after moving. Please change the name and try again.");

	JsonCode SRC_GROUP_CONFLICT = new JsonCode(1006, "Source object and group cannot match");

	JsonCode FILE_NOT_FOUND = new JsonCode(1007, "Corresponding file or group not found");

	JsonCode RESOURCE_LOCKED = new JsonCode(1008, "Current resource is locked, please unlock it before proceeding");

	JsonCode PATH_CONFLICT = new JsonCode(1009, "This path is already in use, please try a different path");

	JsonCode RESOURCE_PATH_CONFLICT = new JsonCode(1010, "There is a conflict in [%s] of the resource, please check");

	JsonCode MOVE_PATH_CONFLICT = new JsonCode(1011, "The path will conflict after moving, please try a different path");

	JsonCode SAVE_GROUP_PATH_CONFLICT = new JsonCode(1036, "The path will conflict after saving, please try a different path");

	JsonCode REQUEST_METHOD_REQUIRED = new JsonCode(1012, "Request method cannot be empty");

	JsonCode REQUEST_PATH_REQUIRED = new JsonCode(1013, "Request path cannot be empty");

	JsonCode FUNCTION_PATH_REQUIRED = new JsonCode(1014, "Function path cannot be empty");

	JsonCode FILE_PATH_NOT_EXISTS = new JsonCode(1015, "Configuration file path does not exist, please check");

	JsonCode REQUEST_PATH_CONFLICT = new JsonCode(1016, "Interface [%s(%s)] conflicts with application, unable to register");

	JsonCode SCRIPT_REQUIRED = new JsonCode(1017, "Script content cannot be empty");

	JsonCode NAME_REQUIRED = new JsonCode(1018, "Name cannot be empty");

	JsonCode PATH_REQUIRED = new JsonCode(1019, "Path cannot be empty");

	JsonCode DS_URL_REQUIRED = new JsonCode(1020, "jdbcURL cannot be empty");

	JsonCode DS_KEY_REQUIRED = new JsonCode(1021, "key cannot be empty");

	JsonCode DS_KEY_CONFLICT = new JsonCode(1022, "Data source key has been used, please change it and try again");

	JsonCode GROUP_ID_REQUIRED = new JsonCode(1023, "Please select group");

	JsonCode CRON_ID_REQUIRED = new JsonCode(1024, "cron expression cannot be empty");

	JsonCode NAME_INVALID = new JsonCode(1025, "Name cannot contain special characters; only Chinese characters, numbers, letters, and combinations of +, _, -, ., and () are allowed, and it cannot start with a dot (.)");

	JsonCode DATASOURCE_KEY_INVALID = new JsonCode(1026, "Data source key cannot contain special characters; only Chinese characters, numbers, letters, and underscores (_) are allowed");

	JsonCode FILE_SAVE_FAILURE = new JsonCode(1027, "Save failed; group names within the same group cannot be duplicated and cannot contain special characters");

	JsonCode PARAMETER_INVALID = new JsonCode(1028, "Parameter validation failed");

	JsonCode HEADER_INVALID = new JsonCode(1029, "Header validation failed");

	JsonCode PATH_VARIABLE_INVALID = new JsonCode(1030, "Path variable validation failed");

	JsonCode BODY_INVALID = new JsonCode(1031, "Body validation failed");

	JsonCode FILE_IS_REQUIRED = new JsonCode(1032, "Please upload the file");

	JsonCode SIGN_IS_INVALID = new JsonCode(1033, "Signature validation failed; please check if the key is correct");

	JsonCode BACKUP_NOT_ENABLED = new JsonCode(1034, "Backup is not enabled; operation unavailable");

	JsonCode API_NOT_FOUND = new JsonCode(1035, "Interface not found");

	default void notNull(Object value, JsonCode jsonCode) {
		if (value == null) {
			throw new InvalidArgumentException(jsonCode);
		}
	}

	default void isTrue(boolean value, JsonCode jsonCode) {
		if (!value) {
			throw new InvalidArgumentException(jsonCode);
		}
	}

	default void notBlank(String value, JsonCode jsonCode) {
		isTrue(StringUtils.isNotBlank(value), jsonCode);
	}
}
