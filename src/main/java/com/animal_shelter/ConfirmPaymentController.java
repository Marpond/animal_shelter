package com.animal_shelter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

// TODO:
//          After clicking ok:
//          Insert values into -> tbl_bookings, tbl_extra_service_links, tbl_payments
public class ConfirmPaymentController implements Initializable
{
    private Database DB = new Database();
    @FXML
    private TextArea summarizeTextArea;
    @FXML
    private Button payButton;
    @FXML
    private final ArrayList<String> BOOKING_DETAILS = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Add every public static method to the ArrayList
        BOOKING_DETAILS.add("Customer name: "+  RegistrationController.selectedCustomerName);
        BOOKING_DETAILS.add("Customer ID: "+    RegistrationController.selectedCustomerID);
        BOOKING_DETAILS.add("Pet ID: "+         RegistrationController.selectedPetID);
        BOOKING_DETAILS.add("Cage no.: "+       BookingController.selectedCageID);
        BOOKING_DETAILS.add("Cage size: "+      BookingController.selectedCageSize);
        BOOKING_DETAILS.add("Cage price /day: "+BookingController.selectedCagePrice);
        BOOKING_DETAILS.add("Booking start: "+  BookingController.selectedStartDate);
        BOOKING_DETAILS.add("Booking end: "+    BookingController.selectedEndDate);
        // Add the extra services
        for (int i:BookingController.selectedServiceIDs)
        {
            String query = String.format("select fld_service_name, fld_service_price " +
                                        "from tbl_extra_services where fld_service_id = %d",i);
            BOOKING_DETAILS.add("Service: "+DB.returns(query).get(0).replace("_", " kr. "));
        }
        BOOKING_DETAILS.add("Total days: "+  BookingController.totalDays);
        BOOKING_DETAILS.add("Total price: "+ BookingController.totalPrice);
        setSummarizeTextArea();
    }

    @FXML
    private void payButtonClicked()
     {

         // Insert into tbl_bookings
         DB.executes(String.format("insert_booking %d, %d, %d, '%s', '%s'",
                 RegistrationController.selectedCustomerID,
                 RegistrationController.selectedPetID,
                 BookingController.selectedCageID,
                 BookingController.selectedStartDate,
                 BookingController.selectedEndDate));
         // Get the booking id
         int bookingID = Integer.parseInt(DB.returns("select max(fld_booking_id) from tbl_bookings").get(0));
         // Insert into tbl_extra_service_links
         for (int serviceID:BookingController.selectedServiceIDs)
         {
             DB.executes(String.format("insert_extra_service_link %d, %d",bookingID,serviceID));
         }
         // Insert into tbl_payments
         String priceString = String.format("%.2f",BookingController.totalPrice).replace(",",".");
         DB.executes(String.format("insert_payment %d, %s",bookingID,priceString));
     }

    private void setSummarizeTextArea()
    {
        for (String s : BOOKING_DETAILS)
        {
            summarizeTextArea.appendText(s + "\n");
        }
    }
}
