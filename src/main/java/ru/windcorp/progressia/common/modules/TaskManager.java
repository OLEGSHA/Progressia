package ru.windcorp.progressia.common.modules;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ru.windcorp.progressia.common.util.crash.CrashReports;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TaskManager {
    private static final TaskManager instance = new TaskManager();
    private final List<Task> tasks = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();
    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("LogicCore-%d")
            .build();
    private boolean wakeUpFlag = false;

    private TaskManager() {}

    public static TaskManager getInstance() {
        return instance;
    }

    //Thread pool with size of logical cores of CPU
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()
                    , threadFactory);

    public void registerModule(Module module) {
        tasks.addAll(module.getTasks());
        modules.add(module);
    }

    public void startLoading() {
        System.out.println("Loader is started");
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executorService.submit(() -> {
                while (!tasks.isEmpty()) {
                    Task t = getRunnableTask();
                    if (t == null) {
                        while (!wakeUpFlag) {
                            try {
                                synchronized (tasks) {
                                    tasks.wait();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        wakeUpFlag = false;
                    }
                    else if (!t.isActive()) {
                        t.run();
                        removeTask(t);
                        wakeUpFlag = true;
                        synchronized (tasks) {
                            tasks.notifyAll();
                        }
                    }
                }
            });
        }

        for (Module m : modules) {
            if (!m.done()) {
                throw CrashReports.crash(new Exception("Modules loading failed"),
                        null);
            }
        }
        while(true) {
            try {
                executorService.shutdown();
                if (executorService.awaitTermination(5, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        System.out.println("Loader has completed its job");
    }

    private void removeTask(Task task) {
        synchronized (tasks) {
            tasks.remove(task);
        }
    }

    public Task getRunnableTask() {
        synchronized (tasks) {
            for (Task t : tasks) {
                if (t.canRun()) return t;
            }
            return null;
        }
    }
}
