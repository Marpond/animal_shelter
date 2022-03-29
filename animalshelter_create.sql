-- Do the thing
use master go
drop database if exists AnimalShelter go
create database AnimalShelter go
use AnimalShelter go
-- Do the table thing
create table tbl_Customers
(
    fld_Customer_ID int identity primary key,
    fld_Customer_Name nvarchar(30),
    fld_Customer_Phone_Number nvarchar(12),
    fld_Customer_Address nvarchar(max)
)go
create table tbl_Cages
(
    fld_Cage_ID int identity primary key,
    fld_Cage_Size int,
    fld_Cage_Price_Per_Day double precision
)go
create table tbl_Animals
(
    fld_Animal_ID int identity primary key,
    fld_Customer_ID int foreign key references tbl_Customers,
    fld_Animal_Name nvarchar(30),
    fld_Animal_Species nvarchar(15),
    fld_Animal_Description nvarchar(max)
)go
create table tbl_Bookings
(
    fld_Booking_ID int identity primary key,
    fld_Customer_ID int foreign key references tbl_Customers,
    fld_Animal_ID int foreign key references tbl_Animals,
    fld_Cage_ID int foreign key references tbl_Cages,
    fld_Booking_Start date,
    fld_Booking_End date
)go
create table tbl_Extra_Services
(
    fld_Service_ID int identity primary key,
    fld_Service_Name nvarchar(20),
    fld_Service_Price double precision
)go
create table tbl_Extra_Service_Link
(
    fld_Service_Link_ID int identity primary key,
    fld_Booking_ID int foreign key references tbl_Bookings,
    fld_Service_ID int foreign key references tbl_Extra_Services
)go
create table tbl_Payments
(
    fld_Payment_ID int identity primary key,
    fld_Booking_ID int foreign key references tbl_Bookings,
    fld_Payment_Amount double precision
)go
-- Create a procedure to get all bookings for the cage with the given ID
create procedure get_bookings(@cage_id int) as
begin
    select fld_Booking_Start, fld_Booking_End from tbl_Bookings where fld_Booking_ID = @cage_id
end
go
-- Create a procedure to return the number of days since epoch
create procedure get_seconds_since_epoch as
begin
    select DATEDIFF(second, '1970-01-01', GETDATE())
end
go
-- Create a procedure to insert a new booking
create procedure insert_booking (@customer_id int, @animal_id int, @cage_id int, @booking_start date, @booking_end date) as
    insert into tbl_Bookings (fld_customer_id, fld_animal_id, fld_cage_id, fld_booking_start, fld_booking_end)
    values (@customer_id, @animal_id, @cage_id, @booking_start, @booking_end)
go
-- Create a procedure to insert a new customer
create procedure insert_customer (@customer_name nvarchar(30), @customer_phone_number nvarchar(12), @customer_address nvarchar(max)) as
    insert into tbl_Customers (fld_customer_name, fld_customer_phone_number, fld_customer_address)
    values (@customer_name, @customer_phone_number, @customer_address)
go
-- Create a procedure to insert a new animal
create procedure insert_animal(@customer_id int, @animal_name nvarchar(30), @animal_species nvarchar(15), @animal_description nvarchar(max)) as
    insert into tbl_Animals (fld_customer_id, fld_animal_name, fld_animal_species, fld_animal_description)
    values (@customer_id, @animal_name, @animal_species, @animal_description)
go