package com.koror.controller;

import com.koror.domain.Car;
import com.koror.domain.Person;
import com.koror.pojo.CarPojo;
import com.koror.pojo.PersonWithCars;
import com.koror.pojo.Statistics;
import com.koror.repo.CarRepo;
import com.koror.repo.PersonRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceController {

    private final PersonRepo personRepo;
    private final CarRepo carRepo;

    public ServiceController(PersonRepo personRepo, CarRepo carRepo) {
        this.personRepo = personRepo;
        this.carRepo = carRepo;
    }

    @PostMapping("/person")
    public ResponseEntity<?> postPerson(@RequestBody Person person){
        if(person.getId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(person.getName() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(person.getBirthdate() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(person.getBirthdate().isAfter(LocalDate.now()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(personRepo.findById(person.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        personRepo.save(person);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/car")
    public ResponseEntity<?> postCar(@RequestBody CarPojo carPojo){
        if(carPojo.getId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(carRepo.findById(carPojo.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(carPojo.getHorsepower() <= 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(carPojo.getModel() == null || carPojo.getModel().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(carPojo.getModel().split("-").length < 2 || carPojo.getModel().charAt(0) == '-')
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(carPojo.getOwnerId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(!personRepo.findById(carPojo.getOwnerId()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        Person person = personRepo.getOne(carPojo.getOwnerId());
        //calculate person age
        int personAge = Period.between(person.getBirthdate(), LocalDate.now()).getYears();
        if(personAge<18)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        Car car = new Car();
        car.setId(carPojo.getId());
        car.setModel(carPojo.getModel());
        car.setHorsepower(carPojo.getHorsepower());
        car.setOwner(person);
        carRepo.save(car);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/personwithcars")
    public ResponseEntity<?> getPersonWithCars(@RequestParam @NotNull Long personid){
        if(personid == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка валидации");
        if(!personRepo.findById(personid).isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person с таким id не существует");
        Person person = personRepo.getOne(personid);
        List<Car> carList = person.getCars();
        List<CarPojo> carPojoList = new ArrayList<>();
        for(Car car: carList) {
            CarPojo carPojo = new CarPojo();
            carPojo.setId(car.getId());
            carPojo.setModel(car.getModel());
            carPojo.setHorsepower(car.getHorsepower());
            carPojo.setOwnerId(personid);
            carPojoList.add(carPojo);
        }
        PersonWithCars personWithCars = new PersonWithCars();
        personWithCars.setId(personid);
        personWithCars.setName(person.getName());
        personWithCars.setBirthdate(person.getBirthdate());
        personWithCars.setCars(carPojoList);
        return new ResponseEntity<>(personWithCars,HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public Statistics getStatistics(){
        Statistics statistics = new Statistics();
        statistics.setPersoncount(personRepo.count());
        statistics.setCarcount(carRepo.count());
        statistics.setUniquevendorcount(carRepo.uniqueVendorCount());
        return statistics;
    }

    @GetMapping("/clear")
    public void clear(){
        carRepo.deleteAll();
        personRepo.deleteAll();
    }

}
