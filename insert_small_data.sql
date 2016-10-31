delete from bike;
delete from biker;
delete from bikestand;
delete from showroom;
delete from ownership;
delete from rentdata;

-- Add sample data
insert into bike values ('b1', 'btwin', 'white', 'company', 'usable', 'available_for_renting', null, null);

insert into biker values ('u1', 'Shubham', 'pass', 'sgoel160@gmail.com', 'Hostel 3');

insert into bikestand values ('s1', 20, 'Hostel 4', (0.0,0.0));
insert into bikestand values ('s2', 20, 'Hostel 5', (0.0,1.0));

insert into bike_at_stand values ('b1', 's1');
