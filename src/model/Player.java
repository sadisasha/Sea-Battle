package model;

import common.Coord;
import common.ShootResult;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Vlasov Alexander
 * Date: 25.08.2014
 * Time: 17:55
 *
 * @author Alexander Vlasov
 */
public class Player {
    final static boolean WON = true;
    final static boolean DONTWON = false;
    private String name;
    private Field myField;
    private Field enemyField;
    private int width, height;
    private CurrentStatistic currentStatistic;
    private Coord shootCoord;

    public Player(int width, int height, String name) {
        this.width = width;
        this.height = height;
        enemyField = new Field(width, height);
        currentStatistic = new CurrentStatistic();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMyField(Field myField) {
        this.myField = myField;
    }


    public boolean isEnemyLoose() {
        return enemyField.getKilled() == myField.getShipSize();
    }

    public boolean isLoose() {
        return myField.getKilled() == myField.getShipSize();
    }

    public boolean isGameOver() {
        return isLoose() || isEnemyLoose();
    }

    /**
     * помечает вражеское поле в соответствии с сообщением от оппонента о результатах выстрела
     *
     * @param shootResult сообщение
     */
    public void setShootResult(ShootResult shootResult) {
        switch (shootResult) {
            case HURT:
                setHurt();
                break;
            case KILLED:
                setHurt();
                enemyField.addKilled();
                enemyField.place(constructKilledShip());
                break;
            case MISSED:
                enemyField.setShoot(shootCoord);
        }

    }

    private void setHurt() {
        Ship ship = new Ship(1);
        ship.setCoords(shootCoord);
        enemyField.place(ship);  // устанавливает временный корабль-маркер
        enemyField.setShoot(shootCoord);
    }

    /**
     * восстанавливает убитый корабль по временным кораблям-маркерам
     *
     * @return
     */
    private Ship constructKilledShip() {
        Set<Coord> wrecks = new HashSet<>();
        searchWrecks(wrecks, shootCoord);
        Ship ship = new Ship(wrecks.size());
        Coord[] coords = ship.getShipCoords();
        int i = 0;
        for (Coord wreck : wrecks) {
            coords[i++] = wreck;
        }
        return ship;
    }

    /**
     * находит цепочку рядом стоящих частей корабля
     *
     * @param coords начальная точка поиска
     * @param coord  set найденных координат
     */
    private void searchWrecks(Set<Coord> coords, Coord coord) {
        if (coords.add(coord)) {
            int x = coord.getX();
            int y = coord.getY();
            Cell[][] field = enemyField.getField();
            if (x > 0 && field[x - 1][y].getShip() != null) searchWrecks(coords, new Coord(x - 1, y));
            if (y > 0 && field[x][y - 1].getShip() != null) searchWrecks(coords, new Coord(x, y - 1));
            if (x < width - 1 && field[x + 1][y].getShip() != null) searchWrecks(coords, new Coord(x + 1, y));
            if (y < height - 1 && field[x][y + 1].getShip() != null) searchWrecks(coords, new Coord(x, y + 1));
        }
    }

    /**
     * Jпределяет и возвращает результат выстрела врага
     *
     * @param coord координаты выстрела врага
     *
     * @return результат выстрела
     */
    public ShootResult receiveShoot(Coord coord) {
        return myField.shoot(coord);
    }

    public void printEnemy() {
        enemyField.printField();
    }

    public void printMy() {
        myField.printField();
    }

    private boolean isAlreadyShooted(Coord coord) {
        return enemyField.getCell(coord).isShoot();
    }

    /**
     * выбирает рандомно координаты выстрела по врагу
     *
     * @return
     */
    public Coord shooting() {
        Random random = new Random();
        Coord coord;
        do {
            coord = new Coord(random.nextInt(10), random.nextInt(10));
        } while (isAlreadyShooted(coord));
        shootCoord = coord;
        return coord;
    }


//    public static boolean turn(Player shooting, Player shooted) {
//        ShootResult shootResult;
//
//        do {
//            do {
//                Random random = new Random();
//                Coord coord;
//                do {
//                    coord = new Coord(random.nextInt(10), random.nextInt(10));
//                } while (shooting.isAlreadyShooted(coord));
//
//                shooting.shootCoord = coord;
//                shootResult = shooted.receiveShoot(shooting.shootCoord);
//                shooting.setShootResult(shootResult);
//                System.out.println(shooting.getName() + " now shoots at " + coord + " with result " + shootResult + ", overall killed " + shooting.enemyField.getKilled() + " enemy ships, enemy map:");
//                shooting.printEnemy();
//                System.out.println("in reality:");
//                shooted.printMy();
//            }
//            while (shootResult == ShootResult.HURT);
//            if (shootResult == ShootResult.KILLED) {
//                if (shooting.isEnemyLoose()) {
//                    System.out.println(shooting.getName() + " won");
//                    return WON;
//                }
//            } else break;
//        } while (true);
//        return DONTWON;
//    }

}