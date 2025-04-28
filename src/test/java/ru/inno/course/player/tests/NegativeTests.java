package ru.inno.course.player.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;
import ru.inno.course.player.tests.annotations.GeneratePlayers;
import ru.inno.course.player.tests.resolvers.TestDataResolver;
import ru.inno.course.player.tests.testWatchers.MyTestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({MyTestWatcher.class, TestDataResolver.class})
public class NegativeTests {

    private PlayerService service;
    private static final String NICKNAME = "Natalya";

    @BeforeEach
    public void setUp() {
        service = new PlayerServiceImpl();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }

    @Test
    @GeneratePlayers(5)
    @DisplayName("Test 01: Удаление игрока c несуществующим id")
    public void deletePlayerWithNonexistentId(PlayerService service) throws IOException {

        assertThrows(NoSuchElementException.class, () -> service.deletePlayer(10), "Доступно удаление " +
                "игрока по несуществующему id");
    }

    @Test
    @DisplayName("Test 02: Создание игрока с занятым Nickname")
    public void createPlayerWithDuplicateNickname() {
        service.createPlayer(NICKNAME);

        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME),"Доступно создание" +
                "игрока с занятым Nickname");
    }

    @Test
    @DisplayName("Test 03: Получение данных игрока по несуществующему id")
    public void getPlayerWithNonexistentId() {

        assertThrows(NoSuchElementException.class, () -> service.getPlayerById(100), "Получены данные " +
                "игрока по несуществующему id");
    }

    @Test
    @DisplayName("Test 04: Создание игрока с пустым Nickname")
    public void addPlayerWithoutNickname() {

        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(""), "Создан " +
                "игрок с пустым Nickname");
    }

    @Test
    @DisplayName("Test 05: Добавление игроку отрицательное количество баллов")
    public void addNegativeNumberPointsToPlayer() {

        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, -50);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(0, playerById.getPoints(), "Игроку начислено отрицательное количество points");
    }

    @Test
    @DisplayName("Test 06: Начисление баллов несуществующему игроку")
    public void addPointsToNonexistentPlayer() {

        assertThrows(NoSuchElementException.class, () -> service.addPoints(10, 50), "Игроку с" +
                " несуществующим id начислены points");
    }

    // Метод addPoints всегда требует Id. Решение пока не найдено
    //@Test
    //@DisplayName("Test 07: Начисление баллов  без указания id игрока")
    //public void startWithIncorrectJSONFile() throws IOException {

     //}

    // Метод addPoints всегда требует Id типа int. Решение пока не найдено
    //@Test
    //@DisplayName("Test 08: Введение невалидного id игрока")
    //public void addPlayerWithIncorrectId() throws IOException {

    //}

    @Test
    @DisplayName("Test 09: Проверка загрузки системы с другим json-файлом")
    public void startWithJSONFileWithIncorrectFile() throws IOException {

        Files.copy(Path.of("./src/test/resources/data_files/second_file.json"), Path.of("./data.json"),
                StandardCopyOption.REPLACE_EXISTING);
        service = new PlayerServiceImpl();
        Collection<Player> listBefore = service.getPlayers();

        assertEquals(5, listBefore.size(), "Файл с некорректными данными");
    }

    // Points всегда целое число, дробное передать нельзя. Решение пока не найдено
    //@Test
    //@DisplayName("Test 10: Начисление 1.5 балла игроку")
    //public void addFractionalPointsToPlayer() throws IOException {

    //}

    @Test
    @DisplayName("Test 11: Запуск системы с json-файлом с дублирующимися записями")
    public void startWithJSONFileWithDuplicateData() throws IOException {

        Files.copy(Path.of("./src/test/resources/data_files/duplicate_data.json"), Path.of("./data.json"),
                StandardCopyOption.REPLACE_EXISTING);
        service = new PlayerServiceImpl();

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(1, listBefore.size(), "Количество игроков в списке не равно 1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Natalya_Guzeeva_", "Natalya_Guzeeva1", "Natalya__Guzeeva"})
    @DisplayName("Test 12: Создание игрока с количеством символов в Nickname более 15")
    public void addPlayerWithNicknameLengthMoreThan15Symbols(String nickname) {

        assertThrows(Exception.class, () -> service.createPlayer(nickname), "Создан игрок с ником длинной более 15 символов");
    }
}
