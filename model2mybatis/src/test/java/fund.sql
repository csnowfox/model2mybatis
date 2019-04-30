create table fund.FUND_CALENDAR(
   tutorial_id INT NOT NULL AUTO_INCREMENT,
   tutorial_title VARCHAR(100) NOT NULL,
   tutorial_author VARCHAR(40) NOT NULL,
   submission_date DATE,
   PRIMARY KEY ( tutorial_id, tutorial_title )
) comment='fund calendard table';

create table fund.FUND_DETAIL(
   fund_code VARCHAR(8) NOT NULL PRIMARY KEY comment "fund code",
   fund_name VARCHAR(100) NOT NULL comment "fund name"
) comment='fund detail table';