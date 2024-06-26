package burrow.chain;

import burrow.chain.event.Event;
import burrow.chain.event.ThrowableEvent;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Chain<C extends Context, R> {
    public final Pre pre = new Pre();
    public final Post post = new Post();

    /**
     * List of middleware components in the chain.
     */
    private final List<Middleware<C>> middlewareList = new ArrayList<>();

    /**
     * Map to store event listeners for different events.
     */
    private final Map<Class<? extends Event>, List<Listener<C, ? extends Event>>>
        eventListenerStore = new HashMap<>();

    public static <C extends Context> Middleware<C> pre(@NonNull final Consumer<C> preProcessor) {
        return ((ctx, next) -> {
            preProcessor.accept(ctx);
            next.run();
        });
    }

    public static <C extends Context> Middleware<C> post(@NonNull final Consumer<C> postProcessor) {
        return ((ctx, next) -> {
            next.run();
            postProcessor.accept(ctx);
        });
    }

    /**
     * Creates a context from the given request.
     * @param request the request to create the context from.
     * @return the created context.
     */
    @NonNull
    public abstract C createContext(@NonNull final R request);

    @NonNull
    public C apply(@NonNull final R request) {
        final var context = createContext(request);
        if (!middlewareList.isEmpty()) {
            dispatch(context, 0);
        }

        return context;
    }

    private void dispatch(@NonNull final C context, final int middlewareIndex) {
        final var eventQueue = context.getEventQueue();
        if (eventQueue != null) {
            while (!eventQueue.isEmpty()) {
                trigger(eventQueue.poll(), context);
            }
        }

        if (middlewareIndex >= middlewareList.size()) {
            return;
        }

        final var middleware = middlewareList.get(middlewareIndex);

        try {
            middleware.accept(context, () -> dispatch(context, middlewareIndex + 1));
        } catch (final Throwable throwable) {
            trigger(new ThrowableEvent(throwable), context);
        }
    }

    public void use(@NonNull final Middleware<C> middleware) {
        middlewareList.add(middleware);
    }

    public void useFirst(@NonNull final Middleware<C> middleware) {
        middlewareList.addFirst(middleware);
    }

    public <E extends Event> void on(
        @NonNull final Class<E> eventClass,
        @NonNull final Listener<C, E> listener
    ) {
        getEventListenerList(eventClass).add(listener);
    }

    public void trigger(@NonNull final Event event, @NonNull final C context) {
        getEventListenerList(event.getClass()).forEach(listener -> listener.accept(context, event));
    }

    @NonNull
    private List<Listener<C, ? extends Event>> getEventListenerList(
        @NonNull final Class<? extends Event> eventClass) {
        return eventListenerStore.computeIfAbsent(eventClass, (k) -> new ArrayList<>());
    }

    public final class Pre {
        private Pre() {
        }

        public void use(@NonNull final Middleware.Pre<C> middleware) {
            Chain.this.use(pre(middleware));
        }

        public void useFirst(@NonNull final Middleware.Pre<C> middleware) {
            Chain.this.useFirst(pre(middleware));
        }
    }

    public final class Post {
        private Post() {
        }

        public void use(@NonNull final Middleware.Post<C> middleware) {
            Chain.this.use(post(middleware));
        }

        public void useFirst(@NonNull final Middleware.Post<C> middleware) {
            Chain.this.useFirst(post(middleware));
        }
    }
}
