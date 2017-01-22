package fixio.fixprotocol.fields;

public abstract class AbstractTemporalField extends AbstractField {

    protected final long value;

    public AbstractTemporalField(int tagNum, long timestampMillis) {
        super(tagNum);
        this.value = timestampMillis;
    }

    /**
     * Returns timestamp value in milliseconds.
     * Use with caution, since it causes value autoboxing.
     *
     * @return value in milliseconds as {@link java.lang.Long}. Prefer {@link #timestampMillis()} to avoid autoboxing.
     * @see #timestampMillis()
     */
    @Override
    public final Long getValue() {
        return value;
    }

    /**
     * Useful when you need to get a milliseconds without autoboxing
     *
     * @return value in milliseconds
     */
    public final long timestampMillis() {
        return value;
    }
}
