public class MemorySegment {
    private final int base;
    private final int limit;
    private int next;

    public MemorySegment(int base, int limit) {
        this.base = base;
        this.limit = limit;
        this.next = base;
    }

    public int allocate() {
        if (next >= limit) {
            throw new RuntimeException("Segmento de memoria lleno [" + base + " - " + (limit - 1) + "]");
        }
        return next++;
    }

    public void reset() {
        this.next = base;
    }

    public boolean contains(int address) {
        return address >= base && address < limit;
    }
}
