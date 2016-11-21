delete from ride;
delete from rider;
delete from stand;
delete from showroom;
delete from ownership;
delete from rentdata;
delete from requests;

-- Add sample data
insert into ride values ('b1', 'bike', 'btwin', 'White', 'company', 'lent');
insert into ride values ('b2', 'bike', 'myBike', 'Black', 'user', 'lent');
insert into ride values ('b3', 'bike', 'Hercules', 'Brown', 'user', 'lent');
insert into ride values ('b4', 'bike', 'Hero', 'Black', 'user', 'lent');
insert into ride values ('b5', 'bike', 'Firefox', 'Black', 'user', 'lent');
insert into ride values ('b6', 'bike', 'Kross', 'Black', 'user', 'lent');

insert into rider values ('u1', 'Shubham', 'pass', 'sgoel160@gmail.com', 'Hostel 3');

insert into stand values ('s1', 20, 'Hostel 4', (0.0,0.0));
insert into stand values ('s2', 20, 'Hostel 5', (0.0,1.0));
insert into stand values ('s3', 20, 'Hostel 6', (0.0,1.0));
insert into stand values ('s4', 20, 'Hostel 7', (0.0,1.0));
insert into stand values ('s5', 20, 'Hostel 8', (0.0,1.0));
insert into stand values ('s6', 20, 'Hostel 9', (0.0,1.0));
insert into stand values ('s7', 20, 'Hostel 10', (0.0,1.0));
insert into stand values ('s8', 20, 'Hostel 11', (0.0,1.0));
insert into stand values ('s9', 20, 'Hostel 12', (0.0,1.0));
insert into stand values ('s10', 20, 'Hostel 13', (0.0,1.0));
insert into stand values ('s101', 20, 'Bandra', (0.0,1.0));
insert into stand values ('s102', 20, 'Dadar', (0.0,1.0));
insert into stand values ('s103', 20, 'Andheri', (0.0,1.0));
insert into stand values ('s104', 20, 'CST', (0.0,1.0));
insert into stand values ('s105', 20, 'Powai', (0.0,1.0));
insert into stand values ('s106', 20, 'Borivali', (0.0,1.0));

insert into ride_at_stand values ('b1', 's1');
insert into ride_at_stand values ('b2', 's10');
insert into ride_at_stand values ('b3', 's2');
insert into ride_at_stand values ('b4', 's1');
insert into ride_at_stand values ('b5', 's3');
insert into ride_at_stand values ('b6', 's1');

insert into ownership values ('b2','u1');
