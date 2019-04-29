spool execute.log


prompt
prompt Model2MybatisJavaCode table fund.FUND_CALENDAR
prompt =================================
prompt
prompt
create table fund.FUND_CALENDAR
(
  taid                 varchar(3)      not null,
  fundcode             varchar(6)      not null,
  idate                varchar(8)      not null,
  marketflag           varchar(1)     ,
  updatetime           varchar(14)    
);

alter table fund.FUND_CALENDAR add constraint PK_FUND_CALENDAR primary key(taid,fundcode,idate);

comment on table fund.FUND_CALENDAR is 'FUND_CALENDAR(基金日历)';

comment on column fund.FUND_CALENDAR.taid is 'TA代码';
comment on column fund.FUND_CALENDAR.fundcode is '基金代码';
comment on column fund.FUND_CALENDAR.idate is 'yyyyMMdd';
comment on column fund.FUND_CALENDAR.marketflag is '0交易日，1非交易日';
comment on column fund.FUND_CALENDAR.updatetime is 'yyyyMMddhhmmss';

spool off
