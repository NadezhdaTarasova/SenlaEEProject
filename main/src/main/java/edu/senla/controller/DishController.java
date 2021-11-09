package edu.senla.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.senla.controller.controllerinterface.DishControllerInterface;
import edu.senla.dto.DishDTO;
import edu.senla.service.serviceinterface.DishServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DishController implements DishControllerInterface {

    private final DishServiceInterface dishService;

    private final ObjectMapper jacksonObjectMapper;

    @SneakyThrows
    @Override
    public void createDish(String newDishJson) {
        DishDTO newDishDTO = jacksonObjectMapper.readValue(newDishJson, DishDTO.class);
        dishService.createDish(newDishDTO);
        System.out.println("Dish" + readDish(newDishDTO.getId()) + " was successfully created");
    }

    @SneakyThrows
    @Override
    public String readDish(int id) {
        DishDTO dishDTO = dishService.read(id);
        return jacksonObjectMapper.writeValueAsString(dishDTO);
    }

    @SneakyThrows
    @Override
    public void updateDish(String dishToUpdateJson, String updatedDishJson) {
        DishDTO dishToUpdateDTO = jacksonObjectMapper.readValue(dishToUpdateJson, DishDTO.class);
        DishDTO updatedDishDTO = jacksonObjectMapper.readValue(updatedDishJson, DishDTO.class);
        dishService.update(dishToUpdateDTO, updatedDishDTO);
        System.out.println("Dish was successfully updated");
    }

    @Override
    public void deleteDish(int id) {
        dishService.delete(id);
        System.out.println("Dish was successfully deleted");
    }

}
