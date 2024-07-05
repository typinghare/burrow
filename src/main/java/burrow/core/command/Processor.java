package burrow.core.command;

import burrow.chain.event.Event;
import burrow.core.chamber.Chamber;
import burrow.core.chamber.ChamberModule;
import burrow.core.common.ColorUtility;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public final class Processor extends ChamberModule {
    public static final String DEFAULT_COMMAND_NAME = "";

    private final Map<String, Class<? extends Command>> commandClassStore = new HashMap<>();

    public Processor(@NonNull final Chamber chamber) {
        super(chamber);

        register(DefaultCommand.class);

        final var executeCommandChain = getChamber().getExecuteCommandChain();
        executeCommandChain.use(this::execute);
        executeCommandChain.on(CommandNotFoundEvent.class, CommandNotFoundEvent::handler);
    }

    @NonNull
    public Map<String, Class<? extends Command>> getCommandClassStore() {
        return commandClassStore;
    }

    @Nullable
    public Class<? extends Command> getCommand(@NonNull final String commandName) {
        return commandClassStore.get(commandName);
    }

    public void execute(@NonNull final CommandContext context, @Nullable final Runnable next) {
        final var commandName = CommandContext.Hook.commandName.getNonNull(context);
        final var commandArgs = CommandContext.Hook.commandArgs.getNonNull(context);

        if (!commandClassStore.containsKey(commandName)) {
            context.trigger(new CommandNotFoundEvent());
            return;
        }

        final var commandClass = commandClassStore.get(commandName);
        try {
            final var constructor = commandClass.getConstructor(CommandContext.class);
            final var command = constructor.newInstance(context);
            final var exitCode = new CommandLine(command)
                .setParameterExceptionHandler(command)
                .setExecutionExceptionHandler(command)
                .execute(commandArgs.toArray(new String[0]));
            CommandContext.Hook.exitCode.set(context, exitCode);
        } catch (final Throwable ex) {
            CommandContext.Hook.exitCode.set(context, CommandLine.ExitCode.SOFTWARE);
        }
    }

    public void register(@NonNull final Class<? extends Command> commandClass) {
        var commandAnnotation = Command.getCommandAnnotation(commandClass);
        commandClassStore.put(commandAnnotation.name(), commandClass);
    }

    public void disable(@NonNull final Class<? extends Command> commandClass) {
        commandClassStore.entrySet().removeIf(entry -> entry.getValue() == commandClass);
    }

    public final static class CommandNotFoundEvent extends Event {
        public static void handler(
            @NonNull final CommandContext context,
            @NonNull final CommandNotFoundEvent event
        ) {
            final var buffer = CommandContext.Hook.buffer.getNonNull(context);
            final var commandName = CommandContext.Hook.commandName.getNonNull(context);

            buffer.append("Command not found: ")
                .append(ColorUtility.render(commandName, ColorUtility.Type.NAME_COMMAND));
            CommandContext.Hook.exitCode.set(context, CommandLine.ExitCode.USAGE);
        }
    }
}
