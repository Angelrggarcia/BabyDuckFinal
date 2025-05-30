public class Type {
    private final String name;

    public Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isError() {
        return "error".equals(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
