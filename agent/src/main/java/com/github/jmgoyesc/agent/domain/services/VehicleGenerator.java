package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.biz.Position;
import com.github.jmgoyesc.agent.domain.models.biz.Vehicle;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * @author Juan Manuel Goyes Coral
 */

class VehicleBuilder {

    static Vehicle build() {
        return Vehicle.builder()
                .name(RandomStringUtils.randomAlphabetic(5, 20))
                .vin(RandomStringUtils.randomAlphabetic(17))
                .brand(random(Vehicle.Brand.values()))
                .licensePlate(RandomStringUtils.randomAlphanumeric(6))
                .fuelType(random(Vehicle.FuelType.values()))
                .initial(new Vehicle.Initial(3333.33, Position.builder()
                        .latitude(52.0001)
                        .longitude(10.33333)
                        .build()))
                .build();
    }

    private static <T> T random(T[] type) {
        Random r = new Random();
        var choice = r.nextInt(type.length);
        return type[choice];
    }

}
