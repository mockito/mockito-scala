package user.org.mockito.model;

public interface JavaFoo {

    Integer varargMethod(Integer... arg);

    /** Mixed Java varargs: fixed prefix argument followed by Integer varargs. */
    Integer mixedVarargMethod(String name, Integer... values);

    /** Mixed Java varargs: fixed prefix argument followed by Object varargs (like JAX-RS queryParam). */
    Integer mixedObjectVarargMethod(String name, Object... values);

}
