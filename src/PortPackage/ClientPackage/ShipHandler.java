package PortPackage.ClientPackage;

import PortPackage.ServerPackage.Port;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**Класс обработчик запроса от кораблей на вход в порт.
 * Реализует интерфейс Runnable, чтобы запускаться в отдельном потоке.
 * Реализвует ActionListener, чтобы по тикам таймера менять координаты корабля в порту*/
public class ShipHandler implements Runnable, ActionListener {
    /**Клиентский сокет*/
    private Socket clientDialog;

    /**Сервер-порт, который нас вызвал*/
    private Port port;

    /**Корабль, который хочет войти в порт*/
    private Ship ship;

    /**Флаг на то, прибывает корабль в порт, или уже отчаливает*/
    boolean isArriving;

    /**Таймер для изменения координат корабля в порту*/
    Timer timer = new Timer(20, this);

    public ShipHandler(Socket socket, Port port) {
        clientDialog=socket;
        this.port=port;
    }

    /**Метод стартует сразу после запуска текущего потока и ожидает передачи по сокету
     * объекта типа корабль. Если есть свободные причалы в порту, он запускает таймер timer,
     * который будет менять координаты корабля, имитируя его движение в порту. Если свободных причалов нет,
     * корабль ставится в общую очередь.*/
    @Override
    public void run() {
        try {
            ObjectOutputStream out=new ObjectOutputStream(clientDialog.getOutputStream());
            ObjectInputStream in=new ObjectInputStream(clientDialog.getInputStream());
            while (!clientDialog.isClosed()) {
                Object entry= in.readObject();
                if(entry instanceof Ship) {
                    ship = (Ship) entry;
                    int num=port.bookFreeDock();
                    ship.setDockNumber(num);
                    if (num==-1) {
                        port.getShipsInQueue().add(ship);
                        System.out.println("Ship added in queqe. № "+port.shipsInQueue());
                        return;
                    }
                    port.getShipsInPort()[num]=ship;
                    isArriving=true;
                    port.setTotalShips(port.getTotalShips()+1);
                    timer.setDelay((int)1000/ship.getSpeed());//чем выше скорость (больше значение поля) корабля, тем чаще таймер будет срабатывать.
                    timer.start();
                    System.out.println("Ship "+port.getTotalShips() +" arriving to dock № "+(num+1));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
        }
    }


    /**Метод, вызываемый на тиках таймера*/
    @Override
    public void actionPerformed(ActionEvent e) {
        if(ship.getCurrentPoint().x>= port.getDocks()[0].getEndPoint().x && isArriving)
            ship.getCurrentPoint().x--;//корабль еще подходит к причалу
        else {
            isArriving=false;//корабль уже подошел к причалу
            if (ship.getCurrentPoint().x== port.getDocks()[0].getEndPoint().x-1) {
                if (ship.isEmpty())//если был пустой, загружается
                    port.setNumberOfContainers(port.getNumberOfContainers()-ship.getCapacity());
                else//если был полный, разгружается
                    port.setNumberOfContainers(port.getNumberOfContainers()+ship.getCapacity());
                ship.changeEmpty();
            }
            if (ship.getCurrentPoint().x<= port.getDocks()[0].getStartPoint().x)//корабль начинает отчаливать
                ship.getCurrentPoint().x++;
            if (ship.getCurrentPoint().x> port.getDocks()[0].getStartPoint().x) {//вышел за зону отрисовки
                if (port.getShipsInQueue().size()==0) {//если очередь кораблей пуста, таймер перестает работу
                    port.freeDock(ship.getDockNumber());
                    timer.stop();
                }
                else {//если нет, берем корабль из очереди и продолжаем менять уже его координаты
                    Ship temp=port.getShipsInQueue().remove(0);
                    int num=ship.getDockNumber();
                    temp.setDockNumber(num);
                    port.getShipsInPort()[num]=temp;
                    port.setTotalShips(port.getTotalShips()+1);
                    ship=temp;
                    isArriving=true;
                    System.out.println("Ship "+port.getTotalShips() +" is moving from queue to dock №" +(num+1)+". "+port.shipsInQueue()+" ships in queue");
                }
            }
        }
    }
}
