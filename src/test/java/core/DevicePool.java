package core;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class DevicePool {
    public record Device(String udid, int systemPort) {
    }

    private static final BlockingQueue<Device> FREE_DEVICES = buildPool();

    private DevicePool() {
    }

    private static BlockingQueue<Device> buildPool() {
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
                throw new IllegalStateException("No free device after waiting 10 minutes");
            }
            return device;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for a device", e);
        }
    }

    public static void release(Device device) {
        if (device != null) {
            FREE_DEVICES.add(device);
        }
    }
}
