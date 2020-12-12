package steptech.compactquickinventoryaccess.api;

import org.jetbrains.annotations.NotNull;
import steptech.compactquickinventoryaccess.FactoryHandler;

public abstract class AbstractModuleInstructionFactoryModule implements ModuleInstructionFactoryModule {
    public AbstractModuleInstructionFactoryModule(@NotNull FactoryHandler factoryHandler) {
        factoryHandler.registerFactoryModule(this);
    }
}
