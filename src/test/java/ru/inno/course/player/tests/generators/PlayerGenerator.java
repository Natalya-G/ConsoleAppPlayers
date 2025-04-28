package ru.inno.course.player.tests.generators;

import com.github.javafaker.Faker;
import ru.inno.course.player.model.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerGenerator {

    public static Set<Player> generate(int count) {
        Faker faker = new Faker();

        Set<Player> playerSet = new HashSet<>();
        for (int i = 0; i < count; i++) {
            int c = i + 1;
            String firstName;
            do {
                firstName = faker.name().firstName();
            } while (firstName.length() > 15);
            int points = faker.number().numberBetween(1, 1000);
            boolean isOnline = faker.bool().bool();

            playerSet.add(new Player(c, firstName, points, isOnline));
        }
        return playerSet;
    }

}
