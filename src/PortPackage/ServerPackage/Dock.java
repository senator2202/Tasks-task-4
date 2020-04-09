package PortPackage.ServerPackage;

import java.awt.*;

/**Класс Причал*/
public class Dock {
    private int number;//номер причала (1,2,3,4)
    private boolean isBusy;//флаг на занятость причала кораблем
    private Point startPoint;//точка, с которой корабль подходит к причалу
    private Point endPoint;//точка возле причала, у которой корабль останавливается


    public Dock(int number) {
        this.number=number;
        initPoints();
    }

    /**В зависимости от номера причала, метод жестко прописывает координаты причала */
    private void initPoints() {

        endPoint=new Point();
        startPoint=new Point();
        endPoint.x=110;
        startPoint.x=775;
        switch (number) {
            case 0:
                endPoint.y=75;
                startPoint.y=75;
                break;
            case 1:
                endPoint.y=205;
                startPoint.y=205;
                break;
            case 2:
                endPoint.y=340;
                startPoint.y=340;
                break;
            case 3:
                endPoint.y=470;
                startPoint.y=470;
                break;
            default:
                endPoint.y=75;
                startPoint.y=75;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
}
