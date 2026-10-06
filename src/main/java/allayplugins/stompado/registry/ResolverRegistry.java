package allayplugins.stompado.registry;

import allayplugins.stompado.resolver.ArgumentResolver;

import java.util.HashMap;
import java.util.Map;

public class ResolverRegistry {

    private final Map<Class<?>, ArgumentResolver<?>> resolvers = new HashMap<>();

    public <T> void register(Class<T> type, ArgumentResolver<T> resolver) {
        resolvers.put(type, resolver);
    }

    @SuppressWarnings("unchecked")
    public <T> ArgumentResolver<T> get(Class<T> type) {
        return (ArgumentResolver<T>) resolvers.get(type);
    }

    public boolean contains(Class<?> type) {
        return resolvers.containsKey(type);
    }

}