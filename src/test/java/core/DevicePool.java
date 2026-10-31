package core;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Repartit les telephones (emulateurs) entre les tests qui tournent en parallele.
 *
 * Un emulateur ne peut executer qu'un seul test a la fois. Chaque test "emprunte" donc
 * un appareil libre au debut et le rend a la fin. S'il n'y a qu'un emulateur, les tests
 * attendent leur tour : le parallele ne casse rien, il est simplement limite.
 *
 * Chaque appareil a aussi son propre systemPort (port interne d'UiAutomator2),
 * sinon deux sessions Appium simultanees entrent en conflit.
 */
public final class DevicePool {

    /** Un appareil : son identifiant adb et le port UiAutomator2 qui lui est reserve. */
    public record Device(String udid, int systemPort) {
    }

    private static final BlockingQueue<Device> FREE_DEVICES = buildPool();

    private DevicePool() {
    }

    private static BlockingQueue<Device> buildPool() {
        // ex : devices=emulator-5554,emulator-5556
        List<String> udids = Arrays.stream(AppConfig.get("devices", "emulator-5554").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        BlockingQueue<Device> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < udids.size(); i++) {
            queue.add(new Device(udids.get(i), 8200 + i));
        }
        return queue;
    }

    public static Device acquire() {
        try {
            Device device = FREE_DEVICES.poll(10, TimeUnit.MINUTES);
            if (device == null) {
                throw new IllegalStateException("Aucun appareil libre apres 10 minutes d'attente");
            }
            return device;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Attente d'un appareil interrompue", e);
        }
    }

    public static void release(Device device) {
        if (device != null) {
            FREE_DEVICES.add(device);
        }
    }
}
