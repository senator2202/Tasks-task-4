package PortPackage.ServerPackage;


import PortPackage.ClientPackage.Ship;
import PortPackage.ClientPackage.ShipHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**Класс порт, принимающий запорсы от кораблей (реализован как сервер, ожидающий подключений от клиентов).
 * Наследует Jpanel, чтобы на нем можно было отрисовать объекты.
 * Реализует интерфейс ActionListener, чтобы по тикам таймера перерисовывать состояние нашего порта,
 * и находящихся в нем кораблей.*/
public class Port extends JPanel implements ActionListener {
    /**Сервис для контроля количества одновременных обращений в порт (корабли в порту + в очереди)*/
    private static ExecutorService executeIt = Executors.newFixedThreadPool(10);

    /**Собственно картинка нашего морского порта*/
    private Image imgPort=new ImageIcon("Images\\Port.png").getImage();

    /**Таймер, после тиков которого отрисовываются объекты в порту*/
    private Timer timer = new Timer(10, this);

    /**Рамка, которой мы будем обрамлять панель, на которой рисуем*/
    private JFrame frame;

    /**Вместимость порта*/
    private int capacity;

    /**Количество контейнеров, выгруженных в порту*/
    private int numberOfContainers;

    /**Итоговое количество кораблей, прошедших через порт*/
    private int totalShips;

    /**Постоянное количество причалов в данном порту*/
    private static final Dock []docks=new Dock[4];

    /**Массив, максимум на 4 корабля, которые муогут находится у причалов в порту*/
    private Ship[] shipsInPort =new Ship[4];

    /**Список кораблей, которые стоят в очереди на вход в причал*/
    private ArrayList<Ship> shipsInQueue=new ArrayList<>();

    /**Метка с инфой про контейнеры в порту*/
    private Label info;

    static
    {
        for (int i=0;i<4;i++)
            docks[i]=new Dock(i);
    }


    public static void main(String []args) {
        Port port =new Port();
        port.startWorking();
    }

    /**Визуальная инициализация нашего порта при создании объекта*/
    public Port() {
        frame = new JFrame("PortSimulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800,638));
        info=new Label();
        info.setPreferredSize(new Dimension(200,20));
        this.add(info);
        frame.add(this);
        frame.setVisible(true);
        timer.start();
    }

    public static ExecutorService getExecuteIt() {
        return executeIt;
    }

    public static void setExecuteIt(ExecutorService executeIt) {
        Port.executeIt = executeIt;
    }

    public static Dock[] getDocks() {
        return docks;
    }

    /**Запуск сервера для приема запросов на наш сервер*/
    public void startWorking() {
        try (ServerSocket server = new ServerSocket(3333)) {//сокет на стороне сервера
            System.out.println("Server socket created, command console is ready to listen to server commands");
            while (!server.isClosed()) {
                Socket client = server.accept();//ожидаем подключение клиента, получаем клиентский сокет

                /**новый поток для каждого подлючения обрабатывается в отдельном классе*/
                executeIt.execute(new ShipHandler(client,this));
                //new Thread(new ShipHandler(client,this)).start();
            }

            /** закрытие пула нитей после завершения работы всех нитей*/
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Метод для отрисовки состояния объектов в нашем порту*/
    @Override
    public void paint(Graphics g) {
        info.setText("Containers in port: "+numberOfContainers);
        g.drawImage(imgPort, 0, 0,800, 600, null);
        for (int i=0;i<4;i++) {
            if(shipsInPort[i]!=null)
                g.drawImage(new ImageIcon(shipsInPort[i].getImgSource()).getImage(), shipsInPort[i].getCurrentPoint().x, shipsInPort[i].getCurrentPoint().y,
                        72, 56, null);
        }

    }

    /**Метод, вызваемый при срабатывании таймера, вызывает перерисовку объектов на нашей панели */
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    /**Метод бронирует первый по списку свободный причал и возврашает его номер.
     * Если все причалы заняты, возвращает -1*/
    public static int bookFreeDock() {
        for (int i=0; i<4; i++)
            if (!docks[i].isBusy()) {
                docks[i].setBusy(true);
                return i;
            }
        return -1;
    }

    /**Метод освобождает причал, и убираует корабль из массива*/
    public void freeDock(int num) {
        if (num<0 || num>3)
            return;
        docks[num].setBusy(false);
        shipsInPort[num]=null;
    }

    /**Метод возвращает количество кораблей на вход в порт*/
    public int shipsInQueue() {
        return shipsInQueue.size();
    }

    public Image getImgPort() {
        return imgPort;
    }

    public void setImgPort(Image imgPort) {
        this.imgPort = imgPort;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNumberOfContainers() {
        return numberOfContainers;
    }

    public void setNumberOfContainers(int numberOfContainers) {
        this.numberOfContainers = numberOfContainers;
    }

    public int getTotalShips() {
        return totalShips;
    }

    public void setTotalShips(int totalShips) {
        this.totalShips = totalShips;
    }

    public Ship[] getShipsInPort() {
        return shipsInPort;
    }

    public void setShipsInPort(Ship[] shipsInPort) {
        this.shipsInPort = shipsInPort;
    }

    public ArrayList<Ship> getShipsInQueue() {
        return shipsInQueue;
    }

    public void setShipsInQueue(ArrayList<Ship> shipsInQueue) {
        this.shipsInQueue = shipsInQueue;
    }

    public Label getInfo() {
        return info;
    }

    public void setInfo(Label info) {
        this.info = info;
    }
}
