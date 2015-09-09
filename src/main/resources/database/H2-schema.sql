CREATE TABLE account(
	username VARCHAR(128),
	password VARCHAR(60) NOT NULL,
	first_name VARCHAR(128) NOT NULL,
	last_name VARCHAR(128) NOT NULL,
	email VARCHAR(128) NOT NULL,
	constraint pk_tbl_account primary key (username)
);

CREATE TABLE role(
	username VARCHAR(128),
	role VARCHAR(10) NOT NULL,
	CONSTRAINT pk_tbl_role PRIMARY KEY (username, role),
	CONSTRAINT fk_tbl_role FOREIGN KEY (username) REFERENCES account(username)
);

CREATE TABLE account_authentication_success_log(
	username VARCHAR(128),
	authentication_timestamp TIMESTAMP,
	CONSTRAINT pk_tbl_aasf PRIMARY KEY (username, authentication_timestamp),
);

CREATE TABLE account_authentication_failure_log(
	username VARCHAR(128),
	authentication_timestamp TIMESTAMP,
	CONSTRAINT pk_tbl_aaff PRIMARY KEY (username, authentication_timestamp),
);

CREATE INDEX idx_tbl_aasl_t ON account_authentication_success_log (authentication_timestamp);

CREATE INDEX idx_tbl_aafl_t ON account_authentication_failure_log (authentication_timestamp);

CREATE TABLE password_history(
	username VARCHAR(128),
	password VARCHAR(128) NOT NULL,
	use_from TIMESTAMP,
	CONSTRAINT pk_tbl_ph PRIMARY KEY (username, use_from)
);

CREATE TABLE password_reissue_info(
	username VARCHAR(128) NOT NULL,
	token VARCHAR(128),
	secret VARCHAR(60) NOT NULL,
	expiry_date TIMESTAMP NOT NULL,
	CONSTRAINT pk_tbl_pri PRIMARY KEY (token),
	CONSTRAINT fk_tbl_pri FOREIGN KEY (username) REFERENCES account(username)
);

CREATE TABLE password_reissue_failure_log(
	token VARCHAR(128),
	attempt_date TIMESTAMP,
	CONSTRAINT pk_tbl_prfl PRIMARY KEY (token, attempt_date)
);

CREATE INDEX idx_tbl_prfl ON password_reissue_failure_log (token);