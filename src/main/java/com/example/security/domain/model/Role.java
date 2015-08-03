package com.example.security.domain.model;

import org.terasoluna.gfw.common.codelist.EnumCodeList;;

public enum Role implements EnumCodeList.CodeListItem{
	ADMN("ADMN", "administrator"),
	USER("USER", "user");
	
	private final String value;
	private final String label;

	private Role(String codeValue, String codeLabel) {
		this.value = codeValue;
		this.label = codeLabel;
	}
	
	@Override
	public String getCodeLabel() {
		return label;
	}

	@Override
	public String getCodeValue() {
		return value;
	}

}
