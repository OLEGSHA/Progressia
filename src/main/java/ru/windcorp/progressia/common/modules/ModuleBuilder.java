package ru.windcorp.progressia.common.modules;

public class ModuleBuilder {
    private final Module module;

    public ModuleBuilder(String id) {
        module = new Module(id);
    }

    public ModuleBuilder AddTask(Task task) {
        module.addTask(task);
        return this;
    }
}
