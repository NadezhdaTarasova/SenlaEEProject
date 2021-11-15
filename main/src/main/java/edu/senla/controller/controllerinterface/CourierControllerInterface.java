package edu.senla.controller.controllerinterface;

public interface CourierControllerInterface {

    public void createCourier(String newCourierJson);

    public String readCourier(int id);

    public void updateCourier(String courierToUpdateJson, String updatedCourierJson);

    public void deleteCourier(int id);

}