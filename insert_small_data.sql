delete from ride;
delete from ride_seq;
delete from rider;
delete from stand;
delete from ownership;
delete from rentdata;
delete from requests;
delete from ride_at_stand;

-- Add sample data
insert into ride values
('b1','2 wheeler', 'btwin', 'White', 'company', 'lent',123,'https://www.cse.iitb.ac.in/~shubham.g/images/dp.jpg'),
('b2','2 wheeler', 'myBike', 'Black', 'user', 'lent',123,'https://www.cse.iitb.ac.in/~shubham.g/images/dp.jpg'),
('b3','2 wheeler', 'Hercules', 'Brown', 'user', 'lent',123,'https://www.cse.iitb.ac.in/~shubham.g/images/dp.jpg'),
('b4','2 wheeler', 'Hero', 'Black', 'user', 'lent',123,'https://www.cse.iitb.ac.in/~shubham.g/images/dp.jpg'),
('b5','2 wheeler', 'Firefox', 'Black', 'user', 'lent',123,'https://www.cse.iitb.ac.in/~shubham.g/images/dp.jpg'),
('b6','2 wheeler', 'Kross', 'Black', 'user', 'lent',123,'https://www.cse.iitb.ac.in/~shubham.g/images/dp.jpg');

insert into ride_seq(txt) values
('1'),
('1'),
('1'),
('1'),
('1'),
('1');

insert into rider values ('u1', 'Shubham', 'pass', 'Hostel 3');

insert into stand values ('s1', 20, 'Hostel 4', 'Hostel 4, Mumbai', 19.136762, 72.910607);
insert into stand values ('s2', 20, 'Hostel 5', 'Hostel 5, Mumbai', 19.135226, 72.910306);
insert into stand values ('s3', 20, 'Hostel 6', 'Hostel 6, Mumbai', 19.135465, 72.907105);
insert into stand values ('s4', 20, 'Hostel 7', 'Hostel 7, Mumbai', 19.134918, 72.908264);
insert into stand values ('s5', 20, 'Hostel 8', 'Hostel 8, Mumbai', 0.0, 0.0);
insert into stand values ('s6', 20, 'Hostel 9', 'Hostel 9, Mumbai', 0.0, 0.0);
insert into stand values ('s7', 20, 'Hostel 10', 'Hostel 10, Mumbai', 19.128282, 72.915729);
insert into stand values ('s8', 20, 'Hostel 11', 'Hostel 11, Mumbai', 0.0, 0.0);
insert into stand values ('s9', 20, 'Hostel 12', 'Hostel 12, Mumbai', 19.135191, 72.904927);
insert into stand values ('s10', 20, 'Hostel 13', 'Hostel 13, Mumbai', 19.134411, 72.905034);
insert into stand values ('s101', 20, 'Bandra', 'Bandra, Mumbai', 19.055260, 72.830252);
insert into stand values ('s102', 20, 'Dadar', 'Dadar, Mumbai', 19.020739, 72.843443);
insert into stand values ('s103', 20, 'Andheri', 'Andheri, Mumbai', 19.112022, 72.868446);
insert into stand values ('s104', 20, 'CST', 'CST, Mumbai', 18.939760, 72.834974);
insert into stand values ('s105', 20, 'Powai', 'Powai, Mumbai', 19.123328, 72.908461);
insert into stand values ('s106', 20, 'Borivali', 'Borivali, Mumbai', 19.230793, 72.856593);

insert into ride_at_stand values ('b1', 's1');
insert into ride_at_stand values ('b2', 's10');
insert into ride_at_stand values ('b3', 's2');
insert into ride_at_stand values ('b4', 's1');
insert into ride_at_stand values ('b5', 's3');
insert into ride_at_stand values ('b6', 's1');

insert into ownership values ('b2','u1');
