package user.org.mockito.model;

public final class RecursiveJavaResponse implements RecursiveJavaModel<RecursiveJavaResponse.Builder, RecursiveJavaResponse> {
    public interface Builder extends RecursiveJavaBuilder<Builder, RecursiveJavaResponse> {}

    @Override
    public Builder toBuilder() {
        return RecursiveJavaResponse::new;
    }
}
