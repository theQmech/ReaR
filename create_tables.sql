
create type ownedby_ as enum('company', 'user');
create type status_ as enum('with_owner', 'unusable', 'usable');
create type usage_ as enum('on_rent', 'available_for_renting', 'available_for_buying');
create type reason_ as enum('missing', 'undecided', 'repairing', 'damaged');
create type request_ as enum('nullable', 'repair', 'sell', 'lend');

/* this table needs serious reviews
 * valid_from etc also need to be included in primary key
 */

create table bike(
	bikeid			varchar(20) primary key not null,
	makemodel		varchar(20),
	color			varchar(20),
	ownedby			ownedby_,
	status			status_,
	usage			usage_,
	reason			reason_,
	request			request_
);

create table biker(
	userid			varchar(20) primary key not null,
	name			varchar(40),
	password		varchar(20),
	email			varchar(20),
	address			varchar(60)
);

create table bikestand(
	bikestandid		varchar(20) primary key not null,
	capacity		int			not null,
	address			varchar(60)	not null,
	location		point		not null
);

create table showroom(
	showroomid		varchar(20)	primary key not null,
	address			varchar(60)	not null,
	location		point		not null,
	openingtime		time		not null,
	closingtime		time		not null
);

create table ownership(
	bikeid			varchar(20)	not null,
	ownerid			varchar(20)	not null,
	primary key (bikeid, ownerid),
	foreign key (bikeid) references bike,
	foreign key (ownerid) references biker
);

create table rentdata(
	bikeid			varchar(20) primary key not null,
	userid			varchar(20) not null,
	frombikestandid	varchar(20) not null,
	tobikestandid	varchar(20),
	foreign key (bikeid) references bike,
	foreign key (userid) references biker,
	foreign key (frombikestandid) references bikestand,
	foreign key (tobikestandid) references bikestand
);
