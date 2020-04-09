package PortPackage.ClientPackage;

import PortPackage.ServerPackage.Port;

import java.awt.*;
import java.io.Serializable;

public class Ship implements Serializable {
    /**Скорость корабля, напрямуюю влияет на его визуальную скорость перемещения*/
    private int speed=15;

    /**Грузоподъмность ( вместимость в контейнерах)*/
    private int capacity;

    /**Путь к файлу с изображением корабля*/
    private String imgSource;

    /**Текущая координата корабля*/
    private Point currentPoint;

    /**Номер причала*/
    private int dockNumber;

    /**Флаг на то, везет ли корабль контейнеры в порт или нет*/
    private boolean isEmpty;

    private Ship() {}

    /**В завимости от флага isEmpty, будет меняться изображение корабля*/
    public void setEmpty(boolean empty) {
        isEmpty = empty;
        imgSource=empty?"Images\\Ship.png":"Images\\ShipLoaded.jpg";
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void changeEmpty() {
        setEmpty(!isEmpty);
    }

    /**В зависимости от номера причала, будут браться необходимые координаты входа в причал*/
    public void setDockNumber(int dockNumber) {
        this.dockNumber = dockNumber;
        if (dockNumber>=0 && dockNumber<4)
            currentPoint=new Point(Port.getDocks()[dockNumber].getStartPoint());
    }

    public int getDockNumber() {
        return dockNumber;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getImgSource() {
        return imgSource;
    }

    public void setImgSource(String imgSource) {
        this.imgSource = imgSource;
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }

    /**Класс-билдер для удобного создания объектов класса Ship*/
    public static class ShipBuilder {
        private Ship ship;

        public ShipBuilder() {
            ship=new Ship();
        }

        public ShipBuilder withSpeed(int speed) {
            ship.speed=speed;
            return this;
        }

        public ShipBuilder withCarryingCapacity(int carryingCapacity) {
            ship.capacity =carryingCapacity;
            return this;
        }

        public ShipBuilder withIsEmpty(boolean isEmpty) {
            ship.isEmpty=isEmpty;
            if (!isEmpty) {
                ship.imgSource="Images\\ShipLoaded.jpg";
            }
            else
                ship.imgSource="Images\\Ship.png";
            return this;
        }

        public Ship build() {
            return ship;
        }
    }

}
