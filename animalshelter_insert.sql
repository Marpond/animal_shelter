use AnimalShelter go
-- Insert multiple rows into tbl_Customers
insert_customer 'Customer1', 'PhoneNumber1', 'Address1' go
insert_customer 'Customer2', 'PhoneNumber2', 'Address2' go
insert_customer 'Customer3', 'PhoneNumber3', 'Address3' go
insert_customer 'Customer4', 'PhoneNumber4', 'Address4' go
-- Insert multiple rows into tbl_Animals
insert_animal 1, 'Animal1', 'Type1', 'Description1' go
insert_animal 2, 'Animal2', 'Type2', 'Description2' go
insert_animal 3, 'Animal3', 'Type3', 'Description3' go
insert_animal 4, 'Animal4', 'Type4', 'Description4' go
-- Insert multiple rows into tbl_Cages
insert into tbl_Cages (fld_Cage_Size, fld_Cage_Price_Per_Day)
values (1, 10),
       (2, 20),
       (3, 30)
go
-- Insert multiple rows into tbl_Bookings
insert_booking 1,1,1,'2022-04-06','2022-04-08' go
insert_booking 2,2,2,'2022-04-07','2022-04-22' go
insert_booking 3,3,3,'2022-04-06','2022-04-08' go
insert_booking 4,4,1,'2022-04-09','2022-04-12' go
-- Insert multiple rows into tbl_Extra_Services
insert into tbl_Extra_Services (fld_Service_Name, fld_Service_Price)
values ('Special food', 100),
       ('Special care', 200)
go
-- Insert multiple rows into tbl_Extra_Service_Link
insert_extra_service_link 1,1 go
insert_extra_service_link 1,2 go
insert_extra_service_link 2,2 go
insert_extra_service_link 4,1 go