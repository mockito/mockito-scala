package user.org.mockito.model;

public interface RecursiveJavaModel<B extends RecursiveJavaBuilder<B, T>, T extends RecursiveJavaModel<B, T>> {
    B toBuilder();
}
