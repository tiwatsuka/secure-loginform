CREATE TABLE account(
	username varchar(128),
	password varchar(60),
	first_name varchar(128),
	last_name varchar(128),
	role varchar(10),
	last_password_change_date TIMESTAMP,
	constraint pk_tbl_account primary key (username)
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

CREATE INDEX idx_tbl_aasl_n ON account_authentication_success_log (username);
CREATE INDEX idx_tbl_aasl_t ON account_authentication_success_log (authentication_timestamp);

CREATE INDEX idx_tbl_aafl_n ON account_authentication_failure_log (username);
CREATE INDEX idx_tbl_aafl_t ON account_authentication_failure_log (authentication_timestamp);

CREATE TABLE password_history(
	username VARCHAR(128),
	password VARCHAR(128),
	use_from TIMESTAMP,
	CONSTRAINT pk_tbl_ph PRIMARY KEY (username, use_from)
);

CREATE INDEX idx_tbl_phi ON password_history (use_from);
