package user.org.mockito.model;

public interface RecursiveJavaBuilder<B extends RecursiveJavaBuilder<B, T>, T> {
    T build();
}
