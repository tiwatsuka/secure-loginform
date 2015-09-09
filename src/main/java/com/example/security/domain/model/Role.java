package com.example.security.domain.model;

import org.terasoluna.gfw.common.codelist.EnumCodeList;

public enum Role implements EnumCodeList.CodeListItem {
	ADMN("administrator"), USER("user");

	private final String label;

	private Role(String codeLabel) {
		this.label = codeLabel;
	}

	@Override
	public String getCodeLabel() {
		return label;
	}

	@Override
	public String getCodeValue() {
		return this.name();
	}

}
