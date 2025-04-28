package ru.inno.course.player.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;
import ru.inno.course.player.tests.annotations.GeneratePlayers;
import ru.inno.course.player.tests.paramsProviders.PointsAndPlayersProvider;
import ru.inno.course.player.tests.paramsProviders.PointsProvider;
import ru.inno.course.player.tests.resolvers.TestDataResolver;
import ru.inno.course.player.tests.testWatchers.MyTestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MyTestWatcher.class, TestDataResolver.class})
public class PositiveTests {

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
    @DisplayName("Test 01: Добавление игрока и проверка наличия его в списке")
    public void addAndCheckNewPlayer() {

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());
        int playerId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(playerId);

        assertEquals(playerId, playerById.getId(), "Некорректный id игрока. При создании был получен id: " +
                playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое " +
                "количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nickname не совпадает со значением, переданным при " +
                "создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }

    @Test
    @DisplayName("Test 02: Удаление нового игрока и проверка отсутствия его в списке")
    public void deleteAndCheckNewPlayer() {

        int playerId = service.createPlayer(NICKNAME);
        Player playerById = service.deletePlayer(playerId);
        Collection<Player> listAfter = service.getPlayers();

        assertEquals(0, listAfter.size(), "Список игроков не пустой");
    }

    @Test
    @DisplayName("Test 03: Добавление игрока при отсутствии json-файла при запуске приложения")
    public void addNewPlayerWithoutJSONFile() throws IOException {

        Files.deleteIfExists(Path.of("./src/test/resources/data_files/data.json"));

        service = new PlayerServiceImpl();
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size(), "При запуске приложения список игроков не пустой");

        int playerId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(playerId);

        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " +
                playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое " +
                "количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nickname игрока не совпадает со значением, переданным " +
                "при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
        assertEquals(1, service.getPlayers().size(), "Количество игроков в списке не равно 1");

    }

    @Test
    @GeneratePlayers(5)
    @DisplayName("Test 04: Добавление игрока при наличии json-файла при запуске приложения")
    public void addNewPlayerWithJSONFile(PlayerService service) {

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(5, listBefore.size(), "Количество игроков в списке не равно 5");

        int playerId = service.createPlayer(NICKNAME);
        listBefore = service.getPlayers();
        assertEquals(6, listBefore.size(), "Количество игроков в списке не равно 6");

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " +
                playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое " +
                "количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nickname игрока не совпадает со значением, " +
                "переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }

    @ParameterizedTest
    @ArgumentsSource(PointsProvider.class)
    @DisplayName("Test 05: Начисление баллов существующему игроку")
    public void addPointsToPlayer(int pointsToAdd, int pointsToBe) {

        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, pointsToAdd);
        Player playerById = service.getPlayerById(playerId);

        assertEquals(pointsToBe, playerById.getPoints(), "Количество points не соответствует значению " + pointsToBe);
    }

    @ParameterizedTest
    @ArgumentsSource(PointsAndPlayersProvider.class)
    @DisplayName("Test 06: Добавление баллов игроку с ненулевым количеством баллов")
    public void doubleAddPointsToPlayer(Player player, int pointsToAdd, int pointsToBe) {

        int playerId = service.createPlayer(player.getNick());
        service.addPoints(playerId, player.getPoints());
        service.addPoints(playerId, pointsToAdd);

        Player playerById = service.getPlayerById(playerId);
        assertEquals(pointsToBe, playerById.getPoints(), "Количество points не соответствует значению " +
                pointsToBe);
    }

    @Test
    @DisplayName("Test 07: Добавление нового игрока и получение его данных")
    public void getPlayerInformation() {

        int playerId = service.createPlayer(NICKNAME);

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id игрока. При создании был получен id: " +
                playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое " +
                "количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nick не совпадает со значением, переданным при " +
                "создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }

    @Test
    @DisplayName("Test 08: Добавление нового игрока и его сохранение в файл")
    public void checkSavePlayerToFile() {

        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, 50);
        service = new PlayerServiceImpl();

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(1, listBefore.size());

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " +
                playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(50, playerById.getPoints(), "Количество points не совпадает. Ожидаемое " +
                "количество points: 50. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nickname игрока не совпадает со значением, " +
                "переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }

    @Test
    @GeneratePlayers(5)
    @DisplayName("Test 09: Проверка загрузки файла и корректности данных в нем")
    public void checkPlayerListFromJSONFile(PlayerService service) {

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(5, listBefore.size(), "Количество игроков в списке не равно 5");
    }

    @Test
    @DisplayName("Test 10: Проверка уникальности id игроков")
    public void checkUniqueIdPlayers() {

        service.createPlayer("Player_1");
        service.createPlayer("Player_2");
        service.createPlayer("Player_3");
        service.createPlayer("Player_4");
        service.createPlayer("Player_5");
        service.deletePlayer(3);
        int playerId = service.createPlayer(NICKNAME);
        assertEquals(6, playerId, "Количество игроков в списке не равно 6");
    }

    @Test
    @DisplayName("Test 11: Получение списка игроков при отсутствии json-файла при запуске приложения")
    public void getPlayersListWithoutJSONFile() throws IOException {

        Files.deleteIfExists(Path.of("./data.json"));

        Collection<Player> list = service.getPlayers();
        assertEquals(0, list.size(), "Количество игроков в списке не равно 0");
    }

    @Test
    @DisplayName("Test 12: Создание игрока с никнеймом с 15-ю символами")
    public void createNewPlayerWithNicknameWith15Symbols() {

        int playerId = service.createPlayer("Natalya_Guzeeva");

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " +
                playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое " +
                "количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals("Natalya_Guzeeva", playerById.getNick(), "Nickname игрока не совпадает с " +
                "переданным значением");
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }
}

