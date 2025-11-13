import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition oddCondition = lock.newCondition();
    private static final Condition evenCondition = lock.newCondition();
    private static int count = 1;
    private static final int MAX = 100;

    public static void main(String[] args) {
        Thread oddThread = new Thread(Main::printOdd);
        Thread evenThread = new Thread(Main::printEven);

        oddThread.start();
        evenThread.start();
    }

    private static void printOdd() {
        lock.lock();
        try {
            while (count <= MAX) {
                if (count % 2 == 1) {
                    System.out.println("Odd: " + count);
                    count++;
                    evenCondition.signal(); // 唤醒偶数线程
                } else {
                    oddCondition.await(); // 等待偶数线程打印  这个时候会自动释放锁，唤醒的时候会自动获取锁
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static void printEven() {
        lock.lock();
        try {
            while (count <= MAX) {
                if (count % 2 == 0) {
                    System.out.println("Even: " + count);
                    count++;
                    oddCondition.signal(); // 唤醒奇数线程
                } else {
                    evenCondition.await(); // 等待奇数线程打印
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}