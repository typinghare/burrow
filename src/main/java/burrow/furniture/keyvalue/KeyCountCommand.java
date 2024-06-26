package burrow.furniture.keyvalue;

import burrow.core.command.Command;
import burrow.core.command.CommandContext;
import burrow.core.command.CommandType;
import org.springframework.lang.NonNull;
import picocli.CommandLine;

@CommandLine.Command(
    name = "key-count",
    description = "Get the count of entries associated with a specified key."
)
@CommandType(KeyValueFurniture.COMMAND_TYPE)
public class KeyCountCommand extends Command {
    @CommandLine.Parameters(index = "0", description = "The key.")
    private String key;

    public KeyCountCommand(@NonNull final CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public Integer call() {
        buffer.append(KeyValueFurniture.countByKey(context, key));

        return CommandLine.ExitCode.OK;
    }
}
