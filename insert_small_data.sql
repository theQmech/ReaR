delete from ride;
delete from rider;
delete from stand;
delete from showroom;
delete from ownership;
delete from rentdata;
delete from requests;

-- Add sample data
insert into ride values ('b1', '2 wheeler', 'btwin', 'White', 'company', 'lent');
insert into ride values ('b2', '2 wheeler', 'myBike', 'Black', 'user', 'lent');
insert into ride values ('b3', '2 wheeler', 'Hercules', 'Brown', 'user', 'lent');
insert into ride values ('b4', '2 wheeler', 'Hero', 'Black', 'user', 'lent');
insert into ride values ('b5', '2 wheeler', 'Firefox', 'Black', 'user', 'lent');
insert into ride values ('b6', '2 wheeler', 'Kross', 'Black', 'user', 'lent');

insert into rider values ('u1', 'Shubham', 'pass', 'sgoel160@gmail.com', 'Hostel 3');

insert into stand values ('s1', 20, 'Hostel 4', 'Hostel 4, Mumbai', 0.0, 0.0);
insert into stand values ('s2', 20, 'Hostel 5', 'Hostel 5, Mumbai', 0.0, 0.0);
insert into stand values ('s3', 20, 'Hostel 6', 'Hostel 6, Mumbai', 0.0, 0.0);
insert into stand values ('s4', 20, 'Hostel 7', 'Hostel 7, Mumbai', 0.0, 0.0);
insert into stand values ('s5', 20, 'Hostel 8', 'Hostel 8, Mumbai', 0.0, 0.0);
insert into stand values ('s6', 20, 'Hostel 9', 'Hostel 9, Mumbai', 0.0, 0.0);
insert into stand values ('s7', 20, 'Hostel 10', 'Hostel 10, Mumbai', 0.0, 0.0);
insert into stand values ('s8', 20, 'Hostel 11', 'Hostel 11, Mumbai', 0.0, 0.0);
insert into stand values ('s9', 20, 'Hostel 12', 'Hostel 12, Mumbai', 0.0, 0.0);
insert into stand values ('s10', 20, 'Hostel 13', 'Hostel 13, Mumbai', 0.0, 0.0);
insert into stand values ('s101', 20, 'Bandra', 'Bandra, Mumbai', 0.0, 0.0);
insert into stand values ('s102', 20, 'Dadar', 'Dadar, Mumbai', 0.0, 0.0);
insert into stand values ('s103', 20, 'Andheri', 'Andheri, Mumbai', 0.0, 0.0);
insert into stand values ('s104', 20, 'CST', 'CST, Mumbai', 0.0, 0.0);
insert into stand values ('s105', 20, 'Powai', 'Powai, Mumbai', 0.0, 0.0);
insert into stand values ('s106', 20, 'Borivali', 'Borivali, Mumbai', 0.0, 0.0);

insert into ride_at_stand values ('b1', 's1');
insert into ride_at_stand values ('b2', 's10');
insert into ride_at_stand values ('b3', 's2');
insert into ride_at_stand values ('b4', 's1');
insert into ride_at_stand values ('b5', 's3');
insert into ride_at_stand values ('b6', 's1');

insert into ownership values ('b2','u1');
