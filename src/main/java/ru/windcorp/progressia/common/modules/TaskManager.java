package ru.windcorp.progressia.common.modules;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TaskManager {
    private static final List<Task> tasks = new ArrayList<>();
    private static final List<Module> modules = new ArrayList<>();
    private static final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("LogicCore-%d")
            .build();

    //Thread pool with size of logical cores of CPU
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()
                    , threadFactory);

    public static void registerModule(Module module) {
        tasks.addAll(module.getTasks());
        modules.add(module);
    }

    public static void startLoading() {
        Module mod = new Module("Module:Mod");
        Task task = new Task("Task:Task") {
            @Override
            protected void perform() {
                int i = 0;
                while(i < 1000000) {
                    i++;
                }
                System.out.println("Task " + getId() + "has been performed by" + Thread.currentThread().getName());
            }
        };
        Task task1 = new Task("Task:Task1") {
            @Override
            protected void perform() {
                int i = 0;
                while(i < 1000000) {
                    i++;
                }
                System.out.println("Task " + getId() + "has been performed by" + Thread.currentThread().getName());
            }
        };
        Task task2 = new Task("Task:Task2") {
            @Override
            protected void perform() {
                int i = 0;
                while(i < 1000000) {
                    i++;
                }
                System.out.println("Task " + getId() + "has been performed by" + Thread.currentThread().getName());
            }
        };
        mod.addTask(task);
        mod.addTask(task1);
        mod.addTask(task2);
        registerModule(mod);

        while (!tasks.isEmpty()) {
            executorService.submit(() -> {
                while (true) {
                    Task t = getRunnableTask();
                    if(t == null) {
                        break;
                    }
                    else if (!t.isActive()) {
                        t.setActive(true);
                        removeTask(t);
                        t.run();
                    }
                }
                System.out.println(Thread.currentThread().getName() + " has been stopped!");
                Thread.yield();
            });
        }

        executorService.shutdownNow();
    }

    private static void removeTask(Task task) {
        synchronized (tasks) {
            tasks.remove(task);
        }
    }

    public static Task getRunnableTask() {
        synchronized (tasks) {
            for (Task t : tasks) {
                if (t.canRun()) return t;
            }
            return null;
        }
    }
}
