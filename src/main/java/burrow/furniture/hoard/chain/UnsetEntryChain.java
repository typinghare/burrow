package burrow.furniture.hoard.chain;

import burrow.chain.Chain;
import burrow.furniture.hoard.Entry;
import org.springframework.lang.NonNull;

import java.util.Collection;

public class UnsetEntryChain extends Chain<UnsetEntryContext> {
    public UnsetEntryContext apply(
        @NonNull final Entry entry,
        @NonNull final Collection<String> keys
    ) {
        final var context = new UnsetEntryContext();
        context.setEntry(entry);
        context.setKeys(keys);

        return apply(context);
    }
}
