CREATE TABLE zz.cust_relate  (
  cust_no varchar(64),
  cust_group varchar(64),
  relate_type varchar(1),
  PRIMARY KEY (cust_no, cust_group)
);
CREATE TABLE zz.risk_stocks  (
  stock_id varchar(16),
  stock_name varchar(255),
  PRIMARY KEY (stock_id)
);

CREATE TABLE zz.sh50_stocks  (
  stock_id varchar(16),
  stock_name varchar(255),
  PRIMARY KEY (stock_id)
);

CREATE TABLE zz.stock_yes_info  (
  stock_id varchar(10),
  yes_close_price decimal(10, 4) ,
  up_limit_price decimal(10, 4) ,
  down_limit_price decimal(10, 4),
  is_risk char(1),
  cur_date date,
  PRIMARY KEY (stock_id)
);
