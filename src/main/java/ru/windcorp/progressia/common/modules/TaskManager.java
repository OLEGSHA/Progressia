package ru.windcorp.progressia.common.modules;

import ru.windcorp.progressia.common.state.StateFieldBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class TaskManager {
    private static final TaskManager instance = new TaskManager();
    private final List<Task> tasks = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();
    private boolean loadingDone;
    private int activeThreadsCount;

    private  final ExecutorService executorService;

    private TaskManager() {
        loadingDone = false;
        activeThreadsCount = 0;
        executorService = newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(), Thread::new);
    }
    
    public static TaskManager getInstance() {
        return instance;
    }

    public void registerModule(Module module) {
        tasks.addAll(module.getTasks());
        modules.add(module);
    }

    public boolean isLoadingDone() {
        return loadingDone;
    }

    public void startLoading() {

        for(int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executorService.submit(() -> {
                while(!loadingDone) {
                    Task t = getRunnableTask();
                    if (t != null) {
                        synchronized (this) {
                            activeThreadsCount++;
                        }
                        t.run();
                        synchronized (this) {
                            activeThreadsCount--;
                            notifyAll();
                        }
                    } else if (activeThreadsCount > 0) {
                        try {
                            synchronized (this) {
                                this.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        synchronized (this) {
                            loadingDone = true;
                        }
                    }
                }
            });
        }

        waitForLoadingEnd();
        executorService.shutdownNow();
    }

    public synchronized Task getRunnableTask() {
        if(!tasks.isEmpty()) {
            for (Task t :
                 tasks) {
                if(t.canRun()) {
                    tasks.remove(t);
                    return t;
                }
            }
        }
        return null;
    }

    private void waitForLoadingEnd() {
        synchronized (this) {
            while(!loadingDone) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
