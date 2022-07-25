show databases

use mybatis_db;

select * from person;


ALTER TABLE  person MODIFY PEOPLE_ID int AUTO_INCREMENT;


commit;

insert into person (first_name, last_name) values("BUMSU","KIM");
insert into person (first_name, last_name) values("HEEJAE","KANG");
insert into person (first_name, last_name) values("JIYONG","GO");
insert into person (first_name, last_name) values("JAHYUN","GU");
insert into person (first_name, last_name) values("ADAMS","MAC");
insert into person (first_name, last_name) values("DOHEE","NA");
insert into person (first_name, last_name) values("MIHO","GOO");
insert into person (first_name, last_name) values("HEEJUNG","MOO");
insert into person (first_name, last_name) values("HYUNSOO","GANG");
insert into person (first_name, last_name) values("GUIVER","MAC");


drop table person;

commit;




CREATE TABLE PERSON  (
	personID INT  NOT NULL PRIMARY key  AUTO_INCREMENT ,
	first_name VARCHAR(30),
	last_name VARCHAR(30)
	);



