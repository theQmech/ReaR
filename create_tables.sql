drop schema if exists public cascade;
create schema public;

create type ride_ as enum('2 wheeler', '4 wheeler');
create type ownedby_ as enum('company', 'user');
create type status_ as enum('with_owner', 'lent');
create type request_ as enum('nullable', 'repair', 'unlend', 'lend');

create table ride(
	rideid			varchar(20) primary key not null,
	ridetype		ride_ not null,
	makemodel		varchar(20),
	color			varchar(20),
	ownedby			ownedby_ not null,
	status			status_ not null,
	code			int,
	url				varchar(6000)
);

create table rider(
	riderid			varchar(50) primary key not null,
	name			varchar(40), not null
	password		varchar(20), not null
	address			varchar(60),
	phone			varchar(20)
);

create table stand(
	standid			varchar(20) primary key not null,
	capacity		int			not null,
	name			varchar(20)	not null,
	address			varchar(60)	not null,
	lat				float		not null,
	long			float		not null
);

create table ownership(
	rideid			varchar(20)	not null,
	ownerid			varchar(20)	not null,
	primary key (rideid, ownerid),
	foreign key (rideid) references ride on DELETE cascade,
	foreign key (ownerid) references rider on DELETE cascade
);

create table rentdata(
	rideid			varchar(20) primary key not null,
	userid			varchar(20) not null,
	fromstandid		varchar(20) not null,
	tostandid		varchar(20),
	foreign key (rideid) references ride on DELETE cascade,
	foreign key (userid) references rider on DELETE cascade,
	foreign key (fromstandid) references stand on DELETE cascade,
	foreign key (tostandid) references stand on DELETE cascade
);

create table ride_at_stand(
	rideid			varchar(20)	not null,
	standid			varchar(20)	not null,
	primary key (rideid, standid),
	foreign key (rideid) references ride on DELETE cascade,
	foreign key (standid) references stand on DELETE cascade
);

create table requests(
	rideid			varchar(20)	not null,
	riderid			varchar(20) not null,
	type			request_,
	standid			varchar(20),
	primary key (rideid, riderid),
	foreign key (rideid) references ride on DELETE cascade,
	foreign key (riderid) references rider on DELETE cascade
);
