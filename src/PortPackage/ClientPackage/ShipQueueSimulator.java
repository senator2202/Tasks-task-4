package PortPackage.ClientPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

/**Класс-симулятор запросов кораблей в порт*/
public class ShipQueueSimulator{


    public static void main(String []args) {
        ShipQueueSimulator s=new ShipQueueSimulator();
        s.start();
    }


    /**Собственно запуск симуляции*/
    public void start() {
        for (int i = 0; i < 20; i++) {
            try {
                Socket clientSocket = new Socket("localhost", 3333);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                Random r=new Random();
                Ship ship=new Ship.ShipBuilder()
                        .withSpeed(r.nextInt(50)+50)
                        .withCarryingCapacity(r.nextInt(5)+5)
                        .withIsEmpty(i<4?false:r.nextBoolean())
                        .build();
                oos.writeObject(ship);
                oos.flush();
                Thread.sleep(new Random().nextInt(3000)+1000);
            } catch (IOException | InterruptedException  ex) {
                //e.printStackTrace();
            }
        }
    }
}
