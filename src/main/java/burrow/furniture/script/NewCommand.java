package burrow.furniture.script;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import burrow.furniture.entry.EntryFurniture;
import org.springframework.lang.NonNull;
import picocli.CommandLine;

@CommandLine.Command(
    name = "new",
    description = "Create a new command entry."
)
@CommandType(EntryFurniture.COMMAND_TYPE)
public class NewCommand extends Command {
    @CommandLine.Parameters(
        index = "0",
        description = "The label associated with the command."
    )
    private String label;

    @CommandLine.Parameters(
        index = "1",
        description = "The command the execute."
    )
    private String command;

    @CommandLine.Parameters(
        index = "2",
        paramLabel = "<working-directory>",
        description = {
            "Specific working directory to execute the command.",
            "If not specified, use the current working directory instead."
        },
        defaultValue = ""
    )
    private String workingDirectory;

    public NewCommand(@NonNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() {
        final var scriptFurniture = useFurniture(ScriptFurniture.class);
        final var entry = scriptFurniture.createEntry(label, command);
        scriptFurniture.setWorkingDirectory(entry, workingDirectory);

        buffer.append(EntryFurniture.entryToString(context, entry));

        return CommandLine.ExitCode.OK;
    }
}
