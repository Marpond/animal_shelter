use AnimalShelter go
-- Insert multiple rows into tbl_Customers
insert into tbl_Customers (fld_Customer_Name, fld_Customer_Address, fld_Customer_Phone_Number)
values ('Customer1', 'Address1', 'Phone1'),
       ('Customer2', 'Address2', 'Phone2'),
       ('Customer3', 'Address3', 'Phone3'),
       ('Customer4', 'Address4', 'Phone4')
go
-- Insert multiple rows into tbl_Animals
insert into tbl_Animals (fld_Customer_ID, fld_Animal_Species, fld_Animal_Description)
values (1, 'Pet1', 'A friendly dog'),
       (2, 'Pet2', 'Peace was never an option'),
       (3, 'Pet3', 'Has no friends'),
       (4, 'Pet4', 'Has friends')
go
-- Insert multiple rows into tbl_Cages
insert into tbl_Cages (fld_Cage_Size, fld_Cage_Price_Per_Day)
values (1, 10),
       (2, 20),
       (3, 30)
go
-- Insert multiple rows into tbl_Bookings
insert into tbl_Bookings (fld_Customer_ID, fld_Animal_ID, fld_Cage_ID, fld_Booking_Start, fld_Booking_End)
values (1, 1, 1,'2022-04-6','2022-04-12'),
       (2, 2, 2,'2022-04-6','2022-04-12'),
       (3, 3, 3,'2022-04-7','2022-04-15'),
       (4, 4, 1,'2022-04-18','2022-04-24')
go
-- Insert multiple rows into tbl_Extra_Services
insert into tbl_Extra_Services (fld_Service_Name, fld_Service_Price)
values ('Special food', 100),
       ('Special care', 200)
go
-- Insert multiple rows into tbl_Extra_Service_Link
insert into tbl_Extra_Service_Link (fld_Booking_ID, fld_Service_ID)
values (1, 1),
       (1, 2),
       (2, 2),
       (4, 1)
go