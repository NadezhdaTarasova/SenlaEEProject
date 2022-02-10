package edu.senla.service.impl;

import edu.senla.dao.DishRepository;
import edu.senla.exeption.BadRequest;
import edu.senla.exeption.ConflictBetweenData;
import edu.senla.exeption.NotFound;
import edu.senla.model.dto.ContainerComponentsDTO;
import edu.senla.model.dto.DishDTO;
import edu.senla.model.entity.Dish;
import edu.senla.model.enums.CRUDOperations;
import edu.senla.model.enums.DishType;
import edu.senla.service.DishService;
import edu.senla.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
@Log4j2
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final ValidationService validationService;
    private final ModelMapper mapper;

    public List<DishDTO> getAllDishes(int pages) {
        log.info("Getting all dishes");
        Page<Dish> dishes = dishRepository.findAll(PageRequest.of(0, pages, Sort.by("name").descending()));
        return dishes.stream().map(d -> mapper.map(d, DishDTO.class)).toList();
    }

    public void createDish(DishDTO newDishDTO) {
        log.info("A request to create a dish {} was received", newDishDTO);
        checkDishDTOName(newDishDTO, CRUDOperations.CREATE);
        Dish dish = setDishDTOTypeToDishEntity(newDishDTO);
        dishRepository.save(dish);
        log.info("Dish with name {} and type {} successfully created", dish.getName(), dish.getDishType());
    }

    public DishDTO getDish(long id) {
        log.info("Getting dish with id {}: ", id);
        Dish dish = getDishIfExists(id, CRUDOperations.READ);
        DishDTO dishDTO = mapper.map(dish, DishDTO.class);
        dishDTO.setDishType(dish.getDishType().toString().toLowerCase());
        log.info("Dish found: {}: ", dishDTO);
        return dishDTO;
    }

    public void updateDish(long id, DishDTO updatedDishDTO) {
        Dish dishToUpdate = getDishIfExists(id, CRUDOperations.UPDATE);
        log.info("Updating dish with id {} with new data {}: ", id, updatedDishDTO);
        checkDishDTOName(updatedDishDTO, CRUDOperations.UPDATE);
        Dish dishWithUpdatedOptions = updateDishOptions(dishToUpdate, updatedDishDTO);
        dishRepository.save(dishWithUpdatedOptions);
        log.info("Dish with id {} successfully updated", id);
    }

    public void deleteDish(long id) {
        log.info("Deleting dish with id: {}", id);
        checkDishExistence(id);
        dishRepository.deleteById(id);
        log.info("Dish with id {} successfully deleted", id);
    }

    private boolean isDishExists(String name) {
        return dishRepository.getByName(name) != null;
    }

    public boolean isDishHasDishInformation(long id) {
        return dishRepository.getById(id).getDishInformation() != null;
    }

    public boolean isAllDishesHaveDishInformation(ContainerComponentsDTO containerComponentsDTO) {
        long meat = containerComponentsDTO.getMeat();
        long garnish = containerComponentsDTO.getGarnish();
        long salad = containerComponentsDTO.getSalad();
        long sauce = containerComponentsDTO.getSauce();
        return isDishHasDishInformation(meat) && isDishHasDishInformation(garnish) && isDishHasDishInformation(salad) && isDishHasDishInformation(sauce);
    }

    private Dish setDishDTOTypeToDishEntity(DishDTO newDishDTO) {
        Dish dish = mapper.map(newDishDTO, Dish.class);
        DishType dishType = translateDishType(newDishDTO.getDishType(), CRUDOperations.CREATE);
        dish.setDishType(dishType);
        return dish;
    }

    private Dish getDishIfExists(long id, CRUDOperations operation) {
        if (!dishRepository.existsById(id)) {
            log.info("The attempt to {} a dish failed, there is no dish with id {}", operation.toString().toLowerCase(), id);
            throw new NotFound("There is no dish with id " + id);
        }
        return dishRepository.getById(id);
    }

    private void checkDishExistence(long id) {
        if (!dishRepository.existsById(id)) {
            log.info("The attempt to delete a dish failed, there is no dish with id {}", id);
            throw new NotFound("There is no dish with id " + id);
        }
    }

    private void checkDishDTOName(DishDTO newDishDTO, CRUDOperations operation) {
        String newDishName = newDishDTO.getName();
        if (isDishExists(newDishName)) {
            log.error("The attempt to {} a dish failed, a dish with name {} already exists", operation.toString().toLowerCase(), newDishName);
            throw new ConflictBetweenData("Dish with name " + newDishName + " already exists");
        }
        if (!validationService.isNameCorrect(newDishName)) {
            log.error("The attempt to {} a dish failed, a dish name {} contains invalid characters", operation.toString().toLowerCase(), newDishName);
            throw new BadRequest("Dish name " + newDishName + " contains invalid characters");
        }
        if (!validationService.isNameLengthValid(newDishName)) {
            log.error("The attempt to {} a dish failed, a dish name {} is to short", operation.toString().toLowerCase(), newDishName);
            throw new BadRequest("Dish name " + newDishName + " is too short");
        }
    }

    private DishType translateDishType(String dishType, CRUDOperations operation) {
        return switch (dishType) {
            case "meat" -> DishType.MEAT;
            case "garnish" -> DishType.GARNISH;
            case "salad" -> DishType.SALAD;
            case "sauce" -> DishType.SAUCE;
            default -> {
                log.error("The attempt to {} a dish failed, a dish type {} invalid", operation.toString().toLowerCase(), dishType);
                throw new BadRequest("Dish type " + dishType + " invalid");
            }
        };
    }

    private Dish updateDishOptions(Dish dishToUpdate, DishDTO updatedDishDTO) {
        dishToUpdate.setName(updatedDishDTO.getName());
        dishToUpdate.setDishType(translateDishType(updatedDishDTO.getDishType(), CRUDOperations.UPDATE));
        return dishToUpdate;
    }
}