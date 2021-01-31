package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.udacity.vehicles.domain.car.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;

import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getTestCar(0);
        long id = 1L;
        car.setId(id);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getTestCar(0);
        long id = 1L;

        mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.write(car).getJson())
                        .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is((int) id)))
            .andExpect(jsonPath("$.details.model", is(car.getDetails().getModel())))
            .andExpect(jsonPath("$.location.lat", is(car.getLocation().getLat())))
            .andExpect(jsonPath("$.location.lon", is(car.getLocation().getLon())));

        ArgumentCaptor<Car> argument = ArgumentCaptor.forClass(Car.class);
        verify(carService, times(1)).save(argument.capture());
        Assertions.assertNull(argument.getValue().getId());
        Assertions.assertEquals(car.getDetails().getModel(), argument.getValue().getDetails().getModel());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        long id = 1L;

        mvc.perform(get("/cars")
                        .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.carList", hasSize(1)))
            .andExpect(jsonPath("$._embedded.carList[0].id", is((int) id)));

        verify(carService, times(1)).list();
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation of a vehicle fails
     */
    @Test
    public void findCar() throws Exception {
        long id = 1L;

        mvc.perform(get("/cars/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is((int) id)));

        verify(carService, times(1)).findById(id);
    }

    /**
     * Tests the update operation for a single car by ID.
     * @throws Exception if the update operation of a vehicle fails
     */
    @Test
    public void updateCar() throws Exception {
        Car car = getTestCar(0);
        long id = 1L;
        car.setCondition(Condition.NEW);
        car.setLocation(new Location(34.020728, -118.692599));

        mvc.perform(put("/cars/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.write(car).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)));

        ArgumentCaptor<Car> argument = ArgumentCaptor.forClass(Car.class);
        verify(carService, times(1)).save(argument.capture());
        Assertions.assertEquals(id, argument.getValue().getId());
        Assertions.assertEquals(car.getCondition(), argument.getValue().getCondition());
        Assertions.assertEquals(car.getDetails().getModel(), argument.getValue().getDetails().getModel());
        Assertions.assertEquals(car.getLocation().getLat(), argument.getValue().getLocation().getLat());
        Assertions.assertEquals(car.getLocation().getLon(), argument.getValue().getLocation().getLon());
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        long id = 1L;

        mvc.perform(delete("/cars/{id}", id))
            .andExpect(status().isNoContent());

        verify(carService, times(1)).delete(id);
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getTestCar(int index) {

        Car car = null;
        Manufacturer manufacturer = null;
        Details details = null;

        List<Car> cars = new ArrayList<>();

        car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        details = new Details();
        manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        cars.add(car);

        car = new Car();
        car.setLocation(new Location(25.782340, -80.369541));
        details = new Details();
        manufacturer = new Manufacturer(100, "Audi");
        details.setManufacturer(manufacturer);
        details.setModel("A5");
        details.setMileage(45734);
        details.setExternalColor("black");
        details.setBody("coupe");
        details.setEngine("3.0 TFSI quattro");
        details.setFuelType("Gasoline");
        details.setModelYear(2016);
        details.setProductionYear(2017);
        details.setNumberOfDoors(2);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        cars.add(car);

        return cars.get(index);
    }
}