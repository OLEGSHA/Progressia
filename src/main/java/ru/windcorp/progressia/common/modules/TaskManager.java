package ru.windcorp.progressia.common.modules;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

//TODO Maybe I want to make it singleton @"Nullkat"
//TODO Optimize task iteration: If task is not runnable check its requirement tasks

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();
    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("LogicCore-%d")
            .build();
    private boolean wakeUpFlag = false;

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
        //DEBUG START
        Module mod = new Module("Module:Mod");
        Task task = new Task("Task:Task") {
            @Override
            protected void perform() {
                int i = 0;
                while(i < 1000000) {
                    i++;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + getId() + "has been performed by" + Thread.currentThread().getName());
            }
        };
        mod.addTask(task);
        mod.addTask(task1);
        mod.addTask(task2);
        registerModule(mod);
        //DEBUG END

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executorService.submit(() -> {
                while (!tasks.isEmpty()) {
                    Task t = getRunnableTask();
                    if (t == null) {
                        while (!wakeUpFlag) {
                            try {
                                System.out.println(Thread.currentThread().getName() + " go to sleep");
                                synchronized (tasks) {
                                    tasks.wait();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println(Thread.currentThread().getName() + " wake up");
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
                    //DEBUG
                    assert t != null;
                    System.out.println(Thread.currentThread().getName() + " has iterated && Task id:" + t.getId());
                }
                System.out.println(Thread.currentThread().getName() + " has completed its work!");
            });
        }
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
