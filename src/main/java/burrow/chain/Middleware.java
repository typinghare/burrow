package burrow.chain;

import org.springframework.lang.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Middleware<C extends Context> extends BiConsumer<C, Runnable> {
    @Override
    void accept(@NonNull final C ctx, @NonNull final Runnable next);

    interface Pre<C extends Context> extends Consumer<C> {
    }

    interface Post<C extends Context> extends Consumer<C> {
    }
}
