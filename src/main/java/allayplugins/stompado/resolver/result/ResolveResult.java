package allayplugins.stompado.resolver.result;

public final class ResolveResult<T> {

    private final T value;
    private final String error;
    private final String message;

    private ResolveResult(T value, String error, String message) {
        this.value = value;
        this.error = error;
        this.message = message;
    }

    public static <T> ResolveResult<T> success(T value) {
        return new ResolveResult<>(value, null, "");
    }

    public static <T> ResolveResult<T> error(String error) {
        return new ResolveResult<>(null, error, "");
    }

    public boolean success() {
        return error == null;
    }

    public T value() {
        return value;
    }

    public String error() {
        return error;
    }

    public String message() {
        return message;
    }

}